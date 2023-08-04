import { Injectable } from '@angular/core';
import { IndicatorPointReportLookup, RawDataRequest } from '@app/core/query/indicator-point-report.lookup';
import { DataTransformService } from '../data-transform/data-transform.service';
import { BaseIndicatorDashboardChartConfig, BaseIndicatorDashboardGaugeConfig, IndicatorFilterType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { IndicatorQueryParams } from '@app/ui/indicator-dashboard/indicator-dashboard.component';
import { IndicatorPointLookup } from '@app/core/query/indicator-point.lookup';
import moment from 'moment';
import { ExtraFilterField } from '@app/ui/indicator-dashboard/indicator-dashboard-chart/indicator-dashboard-chart.component';

@Injectable()
export class ChartHelperService {
	constructor(
		private dataTransformService: DataTransformService
	) { }

	public buildExtraFilterField(chartConfig: BaseIndicatorDashboardChartConfig): ExtraFilterField[] {
		return chartConfig?.filters?.map(filter =>{

			if(!filter?.valueToAssignOnInitialLoad){
				return null;
			}
			return {
				fieldCode: filter.fieldCode,
				value: filter.valueToAssignOnInitialLoad,
				indicatorFilterType: filter.indicatorFilterType,
				displayName: filter.values?.find(filterValue => filterValue.value === filter.valueToAssignOnInitialLoad)?.name
			}
		}).filter(x => !!x) ?? []
	}

	public buildChartIndicatorLookup(chartConfig: BaseIndicatorDashboardChartConfig, indicatorQueryParams: IndicatorQueryParams, extraFields: ExtraFilterField[]): IndicatorPointReportLookup {

		const {metrics, bucket} = this.dataTransformService.configurationToBucketsAndMetrics(chartConfig);

		const lookup = new IndicatorPointReportLookup();

		lookup.bucket = bucket;
		lookup.metrics = metrics;

		if (indicatorQueryParams?.keywordFilters?.length || extraFields.length || indicatorQueryParams?.groupHash) {
			const indicatorPointLookup = new IndicatorPointLookup();

			//query params
			if (indicatorQueryParams?.keywordFilters?.length) {

				indicatorPointLookup.keywordFilters = indicatorQueryParams.keywordFilters;
			}

			if (indicatorQueryParams?.groupHash) {
				indicatorPointLookup.groupHashes = [indicatorQueryParams.groupHash];
			}
			lookup.filters = indicatorPointLookup;


			extraFields
				.filter(extraField => extraField.value !== undefined && extraField.value !== null)
				.forEach(extraField => {
					const filterValue = extraField.value ?? [];


					switch (extraField.indicatorFilterType) {
						case IndicatorFilterType.KeywordFilter: {
							const lookupKeywordFilters = lookup.filters.keywordFilters ?? [];

							const fieldCodeFilter = lookupKeywordFilters.find(x => x.field === extraField.fieldCode) ?? {field: extraField.fieldCode, values: []};

							const filterValueAsArray = Array.isArray(filterValue) ? filterValue :  [filterValue];

							const uniqueValues = filterValueAsArray.filter(x => !fieldCodeFilter.values.includes(x));

							fieldCodeFilter.values.push(
								...uniqueValues
							);
							lookup.filters.keywordFilters = [
								...lookupKeywordFilters.filter(x => x.field !== extraField.fieldCode),
								fieldCodeFilter
							];

							break;
						}
						case IndicatorFilterType.DateRangeFilter: {
							try {
								const [startYear, endYear] = extraField.value;
								const lookupDateRangeFilters = lookup.filters.dateRangeFilters ?? [];
								lookup.filters.dateRangeFilters = [
									...lookupDateRangeFilters,
									{
										field: extraField.fieldCode,
										from: moment().year(startYear).startOf('year'),
										to: moment().year(endYear).endOf('year'),
									}
								];


							} catch {
								console.log('%c Could not parse date range filter', 'color: yellow' );
							}
							break;
						}
					}

			});




		}

		// ** Replace/add keywords filters coming from dashboard configuration if any
		const staticKeywordFilters = chartConfig?.staticFilters?.keywordsFilters;
		if(staticKeywordFilters?.length){
			const filters = lookup.filters ?? new IndicatorPointLookup();
			const keywordFilters = filters.keywordFilters ?? [];

			const dashboardFieldCodes = staticKeywordFilters.map(item => item.field);

			// * remove keywords that are defined in dashboard config if any
			filters.keywordFilters = keywordFilters.filter(filter => !dashboardFieldCodes.includes(filter.field));
			// * add dashboardconfig filters if any
			staticKeywordFilters.forEach(keywordFilter =>{
				filters.keywordFilters.push({
					field: keywordFilter.field,
					values: keywordFilter.value
				})
			});

			//* update lookup
			lookup.filters = filters;
		}

		// * append ordering from backend ordering
		rawDataBlock: if(chartConfig.rawDataRequest){

			const {keyField, order, valueField, page} = chartConfig.rawDataRequest;

			if(!keyField || !valueField || !order){
				console.warn('Raw data not configured properly breaking out of sorting');
				break rawDataBlock;
			}
			const rawDataRequest: RawDataRequest = {
				keyField, order, valueField, page
			};

			lookup.isRawData = true;
			// (lookup as any).rawData = true;
			lookup.rawDataRequest = rawDataRequest;
		}

		return lookup;

	}
	public buildGaugeIndicatorLookup(gaugeConfig: BaseIndicatorDashboardGaugeConfig, indicatorQueryParams: IndicatorQueryParams): IndicatorPointReportLookup {

		const {metrics, bucket} = this.dataTransformService.configurationToBucketsAndMetrics(gaugeConfig);

		const lookup = new IndicatorPointReportLookup();

		lookup.bucket = bucket;
		lookup.metrics = metrics;

		if (indicatorQueryParams?.keywordFilters?.length || indicatorQueryParams?.groupHash) {
			const indicatorPointLookup = new IndicatorPointLookup();

			//query params
			if (indicatorQueryParams?.keywordFilters?.length) {

				indicatorPointLookup.keywordFilters = indicatorQueryParams.keywordFilters;
			}

			if (indicatorQueryParams?.groupHash) {
				indicatorPointLookup.groupHashes = [indicatorQueryParams.groupHash];
			}
			lookup.filters = indicatorPointLookup;

		}

		// ** Replace/add keywords filters coming from dashboard configuration if any
		const staticKeywordFilters = gaugeConfig?.staticFilters?.keywordsFilters;
		if(staticKeywordFilters?.length){
			const filters = lookup.filters ?? new IndicatorPointLookup();
			const keywordFilters = filters.keywordFilters ?? [];

			const dashboardFieldCodes = staticKeywordFilters.map(item => item.field);

			// * remove keywords that are defined in dashboard config if any
			filters.keywordFilters = keywordFilters.filter(filter => !dashboardFieldCodes.includes(filter.field));
			// * add dashboardconfig filters if any
			staticKeywordFilters.forEach(keywordFilter =>{
				filters.keywordFilters.push({
					field: keywordFilter.field,
					values: keywordFilter.value
				})
			});

			//* update lookup
			lookup.filters = filters;
		}

		// * append ordering from backend ordering
		rawDataBlock: if(gaugeConfig.rawDataRequest){

			const {keyField, order, valueField, page} = gaugeConfig.rawDataRequest;

			if(!keyField || !valueField || !order){
				console.warn('Raw data not configured properly breaking out of sorting');
				break rawDataBlock;
			}
			const rawDataRequest: RawDataRequest = {
				keyField, order, valueField, page
			};

			lookup.isRawData = true;
			// (lookup as any).rawData = true;
			lookup.rawDataRequest = rawDataRequest;
		}

		return lookup;

	}
}
