import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";
import { BucketAggregateType } from "@app/core/enum/bucket-aggregate-type.enum";
import { AggregateResponseModel } from "@app/core/model/aggregate-response/aggregate-reponse.model";
import { Bucket, CompositeBucket, DataHistogramBucket, NestedBucket, TermsBucket } from "@app/core/model/bucket/bucket.model";
import { Metric } from "@app/core/model/metic/metric.model";
import { BaseIndicatorDashboardChartConfig, DateFieldFormatterConfig, FieldFormatterType, IndicatorConfigBucket, IndicatorConfigCompositeBucket, IndicatorConfigDataHistogramBucket, IndicatorConfigMetric, IndicatorConfigNestedBucket, IndicatorConfigTermsBucket } from "@app/ui/indicator-dashboard/indicator-dashboard-config";

@Injectable()
export class DataTransformService {

	private _configurationMetricToLookupMetric(configurationMetric: IndicatorConfigMetric): Metric{
		return {
			field: configurationMetric.field,
			type: configurationMetric.type
		}
	}



	private _configurationBucketToLookupBucket(configurationBucket:IndicatorConfigBucket): Bucket {
		if(!configurationBucket) return null;
		let bucket: Bucket;
		if(configurationBucket){
			bucket = {
				field: configurationBucket.field,
				having: configurationBucket.having,
				metrics: configurationBucket.metrics?.map((configurationMetric) => this._configurationMetricToLookupMetric(configurationMetric)),
				type: configurationBucket.type
			}


			switch(bucket.type){
				case BucketAggregateType.Composite:{
					const castedBucket = configurationBucket as IndicatorConfigCompositeBucket;
					const compositeBucket: CompositeBucket = {
						...bucket,
						dateHistogramSource: castedBucket.dateHistogramSource,
						sources: castedBucket.sources,
						afterKey: castedBucket.afterKey,
						type: castedBucket.type
					}
					bucket = compositeBucket;
					break;
				}
				case BucketAggregateType.DateHistogram:{
					const castedBucket = configurationBucket as IndicatorConfigDataHistogramBucket;
					const dataHistogramBucket: DataHistogramBucket = {
						...bucket,
						interval: castedBucket.interval,
						type: castedBucket.type,
						order: castedBucket.order,
						timezone: castedBucket.timezone,
					}
					bucket = dataHistogramBucket;
					break;
				}
				case BucketAggregateType.Nested:
					{
						const castedBucket = (configurationBucket as Bucket as IndicatorConfigNestedBucket).bucket;
						const nestedBucket: NestedBucket = {
							...bucket,
							type: bucket.type,
							bucket: this._configurationBucketToLookupBucket(castedBucket),

						}
						bucket = nestedBucket;
						break;
					}
				case BucketAggregateType.Terms:
					{
						const castedBucket = (configurationBucket as Bucket as IndicatorConfigTermsBucket);
						const termsBucket: TermsBucket = {
							...bucket,
							order: castedBucket.order,
							type: castedBucket.type
						}
						bucket = termsBucket;
					}
					break;
			}

		}

		return bucket
	}


	public configurationToBucketsAndMetrics(chartConfiguration: BaseIndicatorDashboardChartConfig):{metrics: Metric[], bucket:Bucket}{

		const metrics: Metric[] = chartConfiguration.metrics?.map(configurationMetric => this._configurationMetricToLookupMetric(configurationMetric))
		const bucket: Bucket = this._configurationBucketToLookupBucket(chartConfiguration.bucket);

		return {
			metrics,
			bucket
		}
	}


	public aggregateResponseModelToLineChartDataFromConfiguration(
		records: AggregateResponseModel,
		// configurationMetrics: ConfigurationMetrics,
		config: BaseIndicatorDashboardChartConfig
	):LineChartData{

		let seriesData: SeriesData[] = [];
		let standAloneData: StandAloneData[];


		// * SORTING RESULTS
		if(!config.labelSortKey){
			throw('Not sort key specified');
		}
		records.items.sort(
			(a, b) => a.group.items[config.labelSortKey]?.localeCompare(b.group.items[config.labelSortKey])
		)



		//* Build out of result
		if(config.series?.length){

			// * unique labelKeys to extract x axis from
			const uniqueLabels = config.series.reduce((aggr, serieConfig) => {
				const labelKey = serieConfig.label.labelKey;
				if(aggr.includes(labelKey)){
					return aggr;
				}
				return [...aggr, labelKey];
			}, [] as string[]);

			// * label values dictionary
			//* key of the dictionary is the unique label and value is an array containing the values that are collected through iteration (initialized to empty array here)
			const labelValuesDictionary:Record<string, any[]> = uniqueLabels.reduce((aggr, currentUniqueLabel) =>({
				...aggr, [currentUniqueLabel]: []
			}) , {});


			// * foreace serie configration that is defined in configuration build a middle serie representation that keeps data that are collected trough iteration
			const tempSeries: SerieDataInfo[] = config.series.map(serieConfiguration => ({
				data: [], //* series value
				labelKey: serieConfiguration.label.labelKey, //* key that we may use to exract its x axis labels from labelValuesDictionary
				name: serieConfiguration.label.name, //* name of the serie
				color: serieConfiguration.label.color, //*  color preffered color of the serie
				splitSeriesData: serieConfiguration.splitSeries?.reduce((aggr, current) => ({...aggr, [current.key]: {}}) , {}) // * if serie has split series store them here
			}));

			

			// * iterate trough sorted results
			records.items
			.forEach((record, _recordIndex) =>{

				//* push labels that are found in this record to labelvalues dictionary
				uniqueLabels.forEach(labelKey =>{
					const labelValue = record.group.items[labelKey];
					labelValuesDictionary[labelKey].push(labelValue);
				})

				//* foreact serie configuration extract values and append them to series array (middle serie representation)
				config.series.forEach((serieConfiguration, _configurationIndex) =>{

					//* find the value that passes tests specified in configuration and get its value
					const value = record.values.find(value => {		
						const testPassed = serieConfiguration.values.tests.every(
							test =>{
								return Object.keys(test).every(key => test[key] === value[key]);
							}
						)
						
						if(testPassed){
							return value;
						}
						return false;
					})?.[serieConfiguration.values.valueKey];

					//* store value extracted into series array
					tempSeries[_configurationIndex].data.push(value);

					// *SPLIT SERIES
					//* if there is any splitseries configuration get their value and save it in series splitseries data 
					if(serieConfiguration.splitSeries?.length){
						serieConfiguration.splitSeries.map(sseirie => sseirie.key).forEach(
							splitKey => {
								//* get splitserie value
								const splitKeyValue = record.group.items[splitKey];

								//* get array that stores data for the specific split key value and store to it the index of the labels array that it should consume
								const indexesArray =  tempSeries[_configurationIndex].splitSeriesData[splitKey][splitKeyValue] ?? [];
								indexesArray.push(_recordIndex);
								tempSeries[_configurationIndex].splitSeriesData[splitKey][splitKeyValue] = indexesArray;
							}
						)
					}
					// * END SPLIT SERIES

				})

			})


			//* foreach labelkey extractor construct its series
			const result = uniqueLabels.map(uniquelabel =>{
				//* get all series that refer to the specific uniquelabel
				const commonLabelSeries = tempSeries.filter(serie => serie.labelKey === uniquelabel);
				//* get label values (usually x axis data) for that specific label key
				let lblValues = labelValuesDictionary[uniquelabel];


				//* construct a helperArray that keep tracks of duplicates values in label values
				//* index represents the index in lblValues and its value (in helperArray) represents the 'group' of uniqueness

				const categories = []; //* index of category array represents the category and value is the unique group
				const helperArray = [];
				lblValues.forEach(lbl =>{

					//* check if lbl already is spotted
					const indexFound = categories.indexOf(lbl);

					//* lbl first spotted
					if(indexFound < 0){
						//* new group is created (categories.length represents the group tag)
						helperArray.push(categories.length);
						//* register lbl as known value
						categories.push(lbl);
						return;
					}

					//* update helper array with category the label has
					helperArray.push(indexFound);
				})

				const chartSerie: ChartSerie = {};

				commonLabelSeries.forEach(commonserie =>{

					if(!commonserie.splitSeriesData || !Object.keys(commonserie.splitSeriesData).length){

						chartSerie[commonserie.name] = {
							data: this._aggregateData(commonserie.data, helperArray, AggregateDataType.Max) , 
							name: commonserie.name, 
							color: commonserie.color
						}

						return;
					}

					const cs = this._buildSerieFromSerieDataInfo(commonserie, helperArray);

					Object.assign(chartSerie, cs);
				});



				//* remove duplicates labels

				const temparray = [];

				lblValues.forEach((lbl, index) =>{
					const helperIndex = helperArray[index];
					temparray[helperIndex] = lbl;
				})
				lblValues = temparray;


				//* apply transformation if needed
				if(config.labelsTransform){
					switch(config.labelsTransform.type){
						case FieldFormatterType.Date:{
							const castedFormatter = config.labelsTransform as  DateFieldFormatterConfig;
							const datePipe = new DatePipe('en-US'); // TODO LOCALIZATION HERE

							if(!castedFormatter.params){
								console.debug('No params were provided in date transformation');
								lblValues = lblValues.map(lbl => datePipe.transform(lbl, 'y'));
								
							}else{
								lblValues = lblValues.map(lbl => datePipe.transform(lbl, castedFormatter.params));
							}
						}
					}
				}

				const sData: SeriesData = {
					labels: lblValues,
					series: chartSerie
				}

				return sData;

			});

			seriesData = result;

		}

		return {
			seriesData,
			standAloneData
		}

	}



	private _buildSerieFromSerieDataInfo(commonserie: SerieDataInfo, helperArray: number[]):ChartSerie{

		const chartSerie:ChartSerie = {};

		Object.keys(commonserie.splitSeriesData).forEach(
			splitSerieKey =>{
				const splitSeriesGroup = commonserie.splitSeriesData[splitSerieKey];

				Object.keys(splitSeriesGroup).forEach(
					splitSerieValue =>{

						const inheritedValueIndexes = splitSeriesGroup[splitSerieValue] as number[];

						const data = Array(commonserie.data.length)
						.fill(0)
						.map((x, _index) =>{

							if(inheritedValueIndexes.includes(_index)){
								return commonserie.data[_index];
							}

							return x;
						} );

						const aggrData = this._aggregateData(data, helperArray, AggregateDataType.Max);
						chartSerie[splitSerieValue] = {
							name: splitSerieValue,
							data: aggrData
						}
					}
				)


			}
		)
		return chartSerie;
	}


	private _aggregateData(data: number[], helperArray: number[], type: AggregateDataType): number[]{

		if(data.length !== helperArray.length){
			throw'array length missmatch (data - helper array)';
		}

		const tempArray: number[][] = [];
		data.forEach((data, index) =>{
			const groupIndex = helperArray[index];
			const group = tempArray[groupIndex] ?? [];
			group.push(data);
			tempArray[groupIndex] = group;

		})

		if(type === AggregateDataType.Max){
			return tempArray.map(record => Math.max(...record));
		}

		if(type === AggregateDataType.Sum){
			return tempArray.map(
				record=> record.reduce((aggr, current) => aggr +current , 0)
			)
		}
	}
}



enum AggregateDataType{
	Sum,
	Max
}




interface SeriesData{
	labels: string[];
	series: ChartSerie;
}

interface StandAloneData{
	name: string;
	value: number;
	color?: string;
}

export interface LineChartData{
	seriesData?:SeriesData[],
	standAloneData?:StandAloneData[]
}

export type ChartSerie = Record<string, {color?: string,name: string, data: number[]}>



interface SerieDataInfo{
	labelKey: string;
	data: number[];
	color?: string;
	name: string;
	// *  splitserieKey => splitseriekeyvalue => splitseirie data [] (store at numbers array the index of the data array)
	splitSeriesData: Record<string, Record<string, number[]>>
}







					// Object.keys(commonserie.splitSeriesData).forEach(
					// 	splitSerieKey =>{
					// 		const splitSeriesGroup = commonserie.splitSeriesData[splitSerieKey];

					// 		Object.keys(splitSeriesGroup).forEach(
					// 			splitSerieValue =>{

					// 				const inheritedValueIndexes = splitSeriesGroup[splitSerieValue] as number[];

					// 				const data = Array(commonserie.data.length)
					// 				.fill(0)
					// 				.map((x, _index) =>{

					// 					if(inheritedValueIndexes.includes(_index)){
					// 						return commonserie.data[_index];
					// 					}

					// 					return x;
					// 				} );

					// 				chartSerie[splitSerieValue] = {
					// 					name: splitSerieValue,
					// 					data: data
					// 				}
					// 			}
					// 		)


					// 	}
					// )