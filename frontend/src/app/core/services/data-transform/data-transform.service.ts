import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";
import { BucketAggregateType } from "@app/core/enum/bucket-aggregate-type.enum";
import { AggregateResponseModel } from "@app/core/model/aggregate-response/aggregate-reponse.model";
import { Bucket, CompositeBucket, DataHistogramBucket, NestedBucket, TermsBucket } from "@app/core/model/bucket/bucket.model";
import { Metric } from "@app/core/model/metic/metric.model";
import { DateFieldFormatterConfig, FieldFormatterType, IndicatorConfigBucket, IndicatorConfigCompositeBucket, IndicatorConfigDataHistogramBucket, IndicatorConfigMetric, IndicatorConfigNestedBucket, IndicatorConfigTermsBucket, IndicatorDashboardSankeyChartConfig, ConnectionLimitOrder, ConnectionLimitType, SeriesValueFormatterType, SeriesValueNumberFormatter, CommonDashboardItemConfiguration, BaseServerFetchConfiguration, ChildParentRelationShipConfig } from "@app/ui/indicator-dashboard/indicator-dashboard-config";

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
				type: configurationBucket.type,
				bucketSort: configurationBucket.bucketSort
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


	public configurationToBucketsAndMetrics(chartConfiguration: BaseServerFetchConfiguration):{metrics: Metric[], bucket:Bucket}{

		const metrics: Metric[] = chartConfiguration.metrics?.map(configurationMetric => this._configurationMetricToLookupMetric(configurationMetric))
		const bucket: Bucket = this._configurationBucketToLookupBucket(chartConfiguration.bucket);

		return {
			metrics,
			bucket
		}
	}



	

	public aggregateResponseModelToConnections(records: AggregateResponseModel, config: IndicatorDashboardSankeyChartConfig): ChartConnection[]{
		if(!config.connectionExtractor){
			throw 'No sankey extractor provided in config';
		}

		const {sourceKeyExtractor, targetKeyExtractor, valueKeyExtractor, valueTests, groupTests} = config.connectionExtractor;

		if(!sourceKeyExtractor || !targetKeyExtractor){
			throw 'No source key or target key extractor specified';
		}

		let connections: ChartConnection[] = [];


		records.items?.forEach(record =>{

			if(!record?.group?.items){
				return;
			}

			if(! this._validateTest(record.group.items, groupTests)){
				return;
			}
			
			const valueRecord = record?.values?.find(value => this._validateTest(value, valueTests));
			if(!valueRecord) return;
			

			const target = record.group.items[targetKeyExtractor];
			const source = record.group.items[sourceKeyExtractor];

			if(!target || !source){
				return;
			}


			const value = valueRecord[valueKeyExtractor] ?? 0;

			connections.push({
				target,
				source,
				value
			})

		});



		applyLimit: if(config.connectionExtractor?.limit){
			const {count, order, type} = config.connectionExtractor.limit;

			if(
				isNaN(count) ||
				count <= 0 ||
				[
					count,
					order,
					type
				].some(x => x === null || x === undefined)
			){
				console.warn('invalid limit configuration for sankey -- skipping limit')
				break applyLimit;
			}

			if(type === ConnectionLimitType.Connection){
				connections = connections.sort((a, b ) => order === ConnectionLimitOrder.DESCENDING ? b.value - a.value : a.value - b.value )
								.splice(0, count);
				break applyLimit;
			}
			
			const reporter = connections.reduce((aggr, current) => {

				const field = type === ConnectionLimitType.Target ? current.target : current.source;

				const total = (aggr[field] ?? 0) + current.value;
				
				return {...aggr, [field]: total};
			} , {});


			const validNodes = Object.keys(reporter)
						.map(key => ({target: key, value: reporter[key]}))
						.sort((a, b) => {
							if(order === ConnectionLimitOrder.DESCENDING){
								return b.value - a.value
							}
							return a.value - b.value
						})
						.splice(0, count)
						.map(x => x.target)

			connections = connections.filter(
					x => validNodes.includes(
							type === ConnectionLimitType.Target ? x.target : x.source
						)
				).sort(
					(a, b) => order === ConnectionLimitOrder.DESCENDING ? b.value - a.value : a.value - b.value
				);
		}


		return connections;

	}


	private _validateTest(value: Record<string, any>, test: Record<string, any>):boolean{
		if(!test){
			return true
		}
		return Object.keys(test).every(key => test[key] === value?.[key]);
	} 



	
	public getParentChildrenRelationships(params: {
		records: AggregateResponseModel,
		relationShipConfig: ChildParentRelationShipConfig
	}): RelationShipResult | null{

		const {relationShipConfig, records}  = params;

		const {identifierKey, labelKey, parentIdentifierKey} = relationShipConfig ??{}


		if([identifierKey, labelKey, parentIdentifierKey].some(x => !x)){
			console.warn('Parent child key misconfiguration. Exiting');
			return;
		}

		const map = new Map<string, {children: string[] }>();

		const codeMap = new Map<string, string>();

		records?.items?.forEach(record =>{
			const labelValue = record.group.items[labelKey];
			const indentifier = record.group.items[identifierKey];
			const parentIdentifier = record.group.items[parentIdentifierKey];

			codeMap.set(indentifier, labelValue);

			if(parentIdentifier && (parentIdentifier !== indentifier)){// is a child of smoe identifier
				const parent = map.get(parentIdentifier) ?? {children: [] };
				parent.children.push(indentifier);
				map.set(parentIdentifier, parent);
			}
		});

		return {
			relationShips: map, 
			codeMap
		}
	}

	//!! BAD NAMING CHANGE IT TO SOMETHING MORE SUITABLE
	public aggregateResponseModelToLineChartDataFromConfiguration(
		records: AggregateResponseModel,
		// configurationMetrics: ConfigurationMetrics,
		config: CommonDashboardItemConfiguration
	):LineChartData{

		let seriesData: SeriesData[] = [];
		let standAloneData: StandAloneData[];

		// * SORTING RESULTS IF CONFIGURED
		if(config.labelSortKey){
			// throw('Not sort key specified');
			records.items.sort(
				(a, b) => a.group.items[config.labelSortKey]?.localeCompare(b.group.items[config.labelSortKey])
			)
		}



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
				splitSeriesData: serieConfiguration.splitSeries?.reduce((aggr, current) => ({...aggr, [current.key]: {}}) , {}), // * if serie has split series store them here
				type: serieConfiguration.nested?.type,
				show: serieConfiguration.label.show != null ? serieConfiguration.label.show : null,
				position: serieConfiguration.label.position,
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



					const groupTestsSuite = serieConfiguration?.values?.groupTests

					let groupTestsPassed = true;

					if(groupTestsSuite?.length){
						groupTestsPassed =  groupTestsSuite.every(
							test =>{
								return Object.keys(test).every(key => test[key] === record.group?.items?.[key]);
							}	
						)
					}
					let value: any =0;
					if(groupTestsPassed){
						//* find the value that passes tests specified in configuration and get its value
						value = record.values.find(value => {		
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
					}


					const formatter = serieConfiguration.values.formatter;
					if(formatter && ((value !== undefined) && ( value !==null ))){
						switch(formatter.type){
							case SeriesValueFormatterType.Number:{
								const numberFormatter = formatter as SeriesValueNumberFormatter;
								if(!isNaN(numberFormatter.decimalAccuracy) && numberFormatter.decimalAccuracy >= 0){
									const multiplier = Math.pow(10, numberFormatter.decimalAccuracy);
									value = Math.round(value * multiplier)/multiplier;
								}
								break;
							}
						}
					}

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
							color: commonserie.color,
							type: commonserie.type
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
							break;
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

			if(config.reverseValues){
				seriesData.forEach(serieData =>{
					serieData.labels = serieData.labels?.reverse();
					Object.keys(serieData.series).forEach(key =>{
						const serie = serieData.series[key];
						serie.data = serie.data?.reverse();
					})
				})
			}
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
							data: aggrData,
							type: commonserie.type
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

		const defaultValue = 0;

		if(type === AggregateDataType.Max){
			return tempArray.map(record => Math.max(...record.map(x => {
				if((x!== null) && (x!==undefined)){
					return x;
				}
				return defaultValue;
			})));
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

export interface StandAloneData{
	name: string;
	value: number;
	color?: string;
}

export interface LineChartData{
	seriesData?:SeriesData[],
	standAloneData?:StandAloneData[]
}

export type ChartSerie = Record<string, {color?: string,name: string, data: number[], type: string}>



interface SerieDataInfo{
	type: string;
	labelKey: string;
	data: number[];
	color?: string;
	name: string;
	// *  splitserieKey => splitseriekeyvalue => splitseirie data [] (store at numbers array the index of the data array)
	splitSeriesData: Record<string, Record<string, number[]>>
}


interface ChartConnection{
	source: string;
	target: string;
	value: number;
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



export interface RelationShipResult{
	relationShips: Map<string, {children: string[]}>
	codeMap: Map<string, string>;
}