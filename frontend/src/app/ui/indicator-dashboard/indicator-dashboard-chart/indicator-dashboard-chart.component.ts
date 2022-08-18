import { Component, Input, OnChanges, OnInit, SimpleChanges, NgZone } from '@angular/core';
import { ChartBuilderService, GraphOptions } from '@app/core/services/data-transform/charts-common.service';
import { DataTransformService } from '@app/core/services/data-transform/data-transform.service';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { EChartsOption, registerMap} from 'echarts';
import { BehaviorSubject, interval, of, Subject } from 'rxjs';
import { delayWhen, filter, takeUntil } from 'rxjs/operators';
import { BaseIndicatorDashboardChartConfig, IndicatorDashboardBarChartConfig, IndicatorDashboardGraphChartConfig, IndicatorDashboardLineChartConfig, IndicatorDashboardMapChartConfig, IndicatorDashboardPolarBarChartConfig, IndicatorDashboardScatterChartConfig, IndicatorFilterType} from '../indicator-dashboard-config';
import { IndicatorQueryParams } from '../indicator-dashboard.component';
import { MatDialog } from '@angular/material/dialog';
import { IndicatorDashboardFiltersComponent, IndicatorListingFiltersComponentData } from './indicator-dashboard-filters/indicator-dashboard-filters.component';
import * as FileSaver from 'file-saver';
import { IndicatorPointReportLookup } from '@app/core/query/indicator-point-report.lookup';
import { IndicatorPointLookup, IndicatorPointDateRangeFilter } from '@app/core/query/indicator-point.lookup';
import moment from 'moment';
const mapGeo = require('./world-sm.geo.json');
const MAP_GEO_DICTIONARY = mapGeo.features.reduce((aggr, country) => ({...aggr, [country.properties.name]: country.properties.name}), {});
const GRAPH_OPTIONS = require('./les-miserables.json') as GraphOptions;

@Component({
	selector: 'app-indicator-dashboard-chart',
	templateUrl: './indicator-dashboard-chart.component.html',
	styleUrls: ['./indicator-dashboard-chart.component.css'],
	animations: GENERAL_ANIMATIONS
})
export class IndicatorDashboardChartComponent extends BaseComponent implements OnInit, OnChanges {

	@Input() chartConfig: BaseIndicatorDashboardChartConfig;
	@Input()
	indicatorQueryParams: IndicatorQueryParams;
	chartOptions: EChartsOption;

	private _extraFields: {fieldCode: string, value: any, indicatorFilterType: IndicatorFilterType  }[] = [];
	private _ComponentInitializedSubject$ = new BehaviorSubject<boolean>(false);
	public errorLoading = false;

	public uiMinimumTimePased$ = this._ComponentInitializedSubject$.asObservable().pipe(
		delayWhen(val => val ? interval(600) : of(val)),
		// delay(600),
		takeUntil(this._destroyed)
	);


	constructor(
		private chartBuilderService: ChartBuilderService,
		private dataTransformService: DataTransformService,
		private indicatorPointService: IndicatorPointService,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private _dialog: MatDialog) {
		super();

	}
	ngOnInit(): void {

	}
	ngOnChanges(): void {
		this._buildChartOptions();
	}


	private _buildChartOptions(): void {
		this._ComponentInitializedSubject$.next(false);
		this._ComponentInitializedSubject$.next(true);
		this.chartOptions = null;
		if (!this.chartConfig) {
			// this.chartOptions = null;
			return;
		}

		if (this.chartConfig.type === 'line' || this.chartConfig.type === 'bar' || this.chartConfig.type === 'polar_bar' || this.chartConfig.type === 'map' || this.chartConfig.type === 'pie') {
			try { // TODO FIX THIS

				const indicatorId = this.chartConfig?.indicatorId; ;


				if (!indicatorId) {
					console.warn('No indicator id set');
					return;
				}

				this.errorLoading = false;
				this.indicatorPointService.getIndicatorPointReport(indicatorId, this._buildIndicatorLookup(), true)
				.pipe(
					takeUntil(this._destroyed)
				)
				.subscribe((response) => {

					try{
						const {seriesData : sd, standAloneData} = this.dataTransformService.aggregateResponseModelToLineChartDataFromConfiguration(response, this.chartConfig); // TODO FIX THIS
					

						const [seriesData] = sd;


						if (this.chartConfig.type === 'line') {
							if (!seriesData) {
								console.warn('No series found for line chart');
								return;
							}

							this.chartOptions = this.chartBuilderService.buildLine({
								config: this.chartConfig as IndicatorDashboardLineChartConfig,
								inputSeries: seriesData.series,
								labels: seriesData.labels,
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownload: () => {
									this.downloadData();
								}
							});
							return;
						}


						if (this.chartConfig.type === 'bar') {
							if (!seriesData) {
								console.warn('No series found for line bar');
								return;
							}
							this.chartOptions = this.chartBuilderService.buildBar({
								config: this.chartConfig as IndicatorDashboardBarChartConfig,
								labels: seriesData.labels,
								inputSeries: seriesData.series
							});
							return;
						}

						if (this.chartConfig.type === 'polar_bar') {
							if (!seriesData) {
								console.warn('No series found for polar bar');
								return;
							}
							this.chartOptions = this.chartBuilderService.buildPolarBar({
								config: this.chartConfig as IndicatorDashboardPolarBarChartConfig,
								labels: seriesData.labels,
								inputSeries: seriesData.series
							});
							return;
						}

						if (this.chartConfig.type === 'map') {
							if (!seriesData) {
								console.warn('No series found for line map');
								return;
							}
							const {series, labels} = seriesData;
							registerMap('worldmap', mapGeo);
							const seriesIds = Object.keys(series);

							this.chartOptions =  this.chartBuilderService.buildGeoMap({

								config: this.chartConfig as IndicatorDashboardMapChartConfig,
								data: seriesIds.map(sid => ({
									data: series[sid].data.map((value, _index) => ({
											value: value, 
											name: MAP_GEO_DICTIONARY[labels[_index]]
										})),
									description: sid
								}))
							});
							return;
						}


						if (this.chartConfig.type === 'pie') {
							if (!standAloneData) {
								console.warn('No Data found for pie');
								return;
							}
							this.chartOptions = this.chartBuilderService.buildPie({
								config: this.chartConfig as any,
								pieData: standAloneData
							});
						}
					}catch{
						this.errorLoading = true;
					}

				},
				error => this.onLoadError(error));
			} catch {
			}

			return;
		}

		this.chartOptions = this.generateChartOptions(this.chartConfig);
	}




	private generateChartOptions(chartConfig: BaseIndicatorDashboardChartConfig): EChartsOption {
		const xAxisData: string[] = [];
		const data1: number[] = [];
		const data2: number[] = [];

		for (let i = 0; i < 20; i++) {
			xAxisData.push('category' + i);
			data1.push(Math.abs((Math.sin(i / 5) * (i / 5 - 10) + i / 6) * 5));
			data2.push(Math.abs((Math.cos(i / 5) * (i / 5 - 10) + i / 6) * 5));
		}

		if (this.chartConfig.type === 'scatter') {
			return this.chartBuilderService.buildScatterOptions({config: this.chartConfig as IndicatorDashboardScatterChartConfig});
		}
		if (this.chartConfig.type === 'graph') {
			return this.chartBuilderService.buildGraphOptions({
				config: this.chartConfig as IndicatorDashboardGraphChartConfig,
				graph: GRAPH_OPTIONS
			});
		}

	}


	private onLoadError(error: any): void {
		this.errorLoading = true;
	}

	public openFilters(): void {


		const data: IndicatorListingFiltersComponentData = {
			config: this.chartConfig,
			values: this._extraFields,
			bannedValues: this.indicatorQueryParams.levels.reduce((aggr, current) => {
				const currentBan = {};

				currentBan[current.code] = [current.value];
				return {...aggr, ...currentBan};
			} , {})
		};

		this._dialog.open(
			IndicatorDashboardFiltersComponent,
			{
				width: '35rem',
				disableClose: true,
				data
			}
		).afterClosed()
		.pipe(
			filter(anyFilters => anyFilters),
			takeUntil(this._destroyed)
		)
		.subscribe(filters => {
			this._extraFields = filters;
			this._buildChartOptions();
		});

	}
	public downloadData(): void {
		console.log('download data');

		this.indicatorPointService
			.exportXlsx(this.chartConfig.indicatorId, this._buildIndicatorLookup())
			.pipe(
				takeUntil(this._destroyed)
			)
			.subscribe(response => {

				const filenametosave = this.getFilenameFromContentDispositionHeader(response.headers.get('Content-Disposition'));

				FileSaver.saveAs(response.body, filenametosave);
			});
	}



	private _buildIndicatorLookup(): IndicatorPointReportLookup {
		const {metrics, bucket} = this.dataTransformService.configurationToBucketsAndMetrics(this.chartConfig);

		const lookup = new IndicatorPointReportLookup();

		lookup.bucket = bucket;
		lookup.metrics = metrics;

		if (this.indicatorQueryParams?.levels?.length || this._extraFields.length || this.indicatorQueryParams?.groupHash) {
			const indicatorPointLookup = new IndicatorPointLookup();

			//query params

			// TODO Pleonasmos
			if (this.indicatorQueryParams?.levels?.length) {
				const keywordFilters: Record<string, string[]> = this.indicatorQueryParams.levels.reduce((aggr, current) => {
					aggr[current.code] = [...(aggr[current.code] ?? [] ), current.value];
					return aggr;
				} , {});

				indicatorPointLookup.keywordFilters = Object.keys(keywordFilters).map((key) => ({
					field: key,
					values: keywordFilters[key]
				}));
			}

			//TODO END

			if (this.indicatorQueryParams.groupHash) {
				indicatorPointLookup.groupHashes = [this.indicatorQueryParams.groupHash];
			}
			lookup.filters = indicatorPointLookup;


			this._extraFields
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

		return lookup;

	}


	private getFilenameFromContentDispositionHeader(header: string): string { // expecting filename=XXXX or filename="XXXX" to exist
		// const regex: RegExp = new RegExp(/filename=((\"(.*)\")|([^;]*))/g);
		const regex: RegExp = new RegExp(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/g);

		const matches = header.match(regex);
		let filename: string;
		let encodedFileName: string;
		const fileNameKey = 'filename=';
		const fileNameUTF8Key = 'filename*=UTF-8\'\'';

		for (let i = 0; i < matches.length; i++) {
			const match = matches[i];
			if (match.includes(fileNameKey + '"')) {
				filename = match.substring(fileNameKey.length + 1, match.length - 1);
			} else if (match.includes(fileNameKey)) {
				filename = match.substring(fileNameKey.length);
			} else if (match.includes(fileNameUTF8Key + '"')) {
				encodedFileName = match.substring(fileNameUTF8Key.length + 1, match.length - 1);
			} else if (match.includes(fileNameUTF8Key)) {
				encodedFileName = match.substring(fileNameUTF8Key.length);
			}
		}
		return encodedFileName ? decodeURI(encodedFileName) : filename;
	}
}
