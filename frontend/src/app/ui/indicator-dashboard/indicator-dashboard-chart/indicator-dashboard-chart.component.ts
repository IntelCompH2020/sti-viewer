import { Component, Input, OnChanges, OnInit, SimpleChanges, NgZone, ViewChild, ElementRef, HostListener, Optional } from '@angular/core';
import { ChartBuilderService, GraphOptions, TreeMapData } from '@app/core/services/data-transform/charts-common.service';
import { ChartSerie, DataTransformService, StandAloneData } from '@app/core/services/data-transform/data-transform.service';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { DataZoomComponentOption, EChartsOption, registerMap} from 'echarts';
import { BehaviorSubject, combineLatest, interval, Observable, of, Subject } from 'rxjs';
import { delayWhen, distinctUntilChanged, filter, map, takeUntil } from 'rxjs/operators';
import { BaseIndicatorDashboardChartConfig, IndicatorDashboardBarChartConfig, IndicatorDashboardChartType, IndicatorDashboardConfig, IndicatorDashboardGraphChartConfig, IndicatorDashboardLineChartConfig, IndicatorDashboardMapChartConfig, IndicatorDashboardPolarBarChartConfig, IndicatorDashboardRadarChartConfig, IndicatorDashboardSankeyChartConfig, IndicatorDashboardScatterChartConfig, IndicatorDashboardTreeMapChartConfig, IndicatorFilterType} from '../indicator-dashboard-config';
import { IndicatorQueryParams } from '../indicator-dashboard.component';
import { MatDialog } from '@angular/material/dialog';
import { IndicatorDashboardFiltersComponent, IndicatorListingFiltersComponentData } from './indicator-dashboard-filters/indicator-dashboard-filters.component';
import * as FileSaver from 'file-saver';
import { IndicatorPointReportLookup, RawDataRequest } from '@app/core/query/indicator-point-report.lookup';
import { IndicatorPointLookup } from '@app/core/query/indicator-point.lookup';
import moment, { max } from 'moment';
const mapGeo = require('./world-sm.geo.json');
const MAP_GEO_DICTIONARY = mapGeo.features.reduce((aggr, country) => ({...aggr, [country.properties.name]: country.properties.name}), {});
const GRAPH_OPTIONS = require('./les-miserables.json') as GraphOptions;
import { AuthService } from '@app/core/services/ui/auth.service';
import { DashboardUITagsService } from '../ui-services/dashboard-tags.service';
import { ChartLockZoomService } from '../ui-services/chart-lock-zoom.service';
import { ExternalToken, IndicatorPointReportExternalTokenPersist } from '@app/core/model/external-token/external-token.model';
import { ExternalTokenService } from '@app/core/services/http/external-token.service';
import { Guid } from '@common/types/guid';
import { PublicService } from '@app/core/services/http/public.service';
import { AggregateResponseModel } from '@app/core/model/aggregate-response/aggregate-reponse.model';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { ChartHelperService } from '@app/core/services/ui/chart-helper.service';
import { ShareDialogComponent, ShareDialogData } from '../share-dialog/share-dialog.component';
// const TREE_MAP_DATA = require('./disk.tree.json');

@Component({
	selector: 'app-indicator-dashboard-chart',
	templateUrl: './indicator-dashboard-chart.component.html',
	styleUrls: ['./indicator-dashboard-chart.component.css'],
	animations: GENERAL_ANIMATIONS
})
export class IndicatorDashboardChartComponent extends BaseComponent implements OnInit, OnChanges {

	@Input() chartConfig: BaseIndicatorDashboardChartConfig;
	@Input() indicatorQueryParams: IndicatorQueryParams;
	@Input() token = null;
	chartOptions: EChartsOption;



	private _observer: IntersectionObserver;
	private _previewSubject$ = new BehaviorSubject<boolean>(false);
	private _inputChanges$ = new Subject<void>();

	protected noDataFound: boolean = false;

	private _extraFields: ExtraFilterField[] = [];

	protected get extraFields(){
		return this._extraFields
	}
	private _ComponentInitializedSubject$ = new BehaviorSubject<boolean>(false);
	public errorLoading = false;

	public uiMinimumTimePased$ = this._ComponentInitializedSubject$.asObservable().pipe(
		delayWhen(val => val ? interval(100) : of(val)),
		// delay(600),
		takeUntil(this._destroyed)
	);



	private _chartTags$ = new BehaviorSubject<string[]>([]);



	tags$: Observable<ChartTag[]> = combineLatest([
		this._chartTags$.asObservable(),
		this.dashboardTagsUIService.activeChartTagsChanges$
	]).pipe(
		map(([tags, activeTags]) => tags?.map(tag => ({
			selected: !!activeTags?.includes(tag),
			name: tag
		})) ?? [])
	)



	merge: EChartsOption = {};

	supportsZoom = false;

	constructor(
		private chartBuilderService: ChartBuilderService,
		private dataTransformService: DataTransformService,
		private indicatorPointService: IndicatorPointService,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private elementRef:ElementRef,
		private dashboardTagsUIService: DashboardUITagsService,
		private publicService: PublicService,
		private _dialog: MatDialog,
		private chartHelperService: ChartHelperService,
		private dialog: MatDialog,
		@Optional() private chartLockZoomService: ChartLockZoomService,

		) {
		super();



		this._initialize()


	}
	ngOnInit(): void {


	}
	ngOnChanges(simpleChanges: SimpleChanges): void {
		this._inputChanges$.next();

		if(simpleChanges.chartConfig){
			this._extraFields = this.chartHelperService.buildExtraFilterField(this.chartConfig)
		}

		if(this.chartConfig?.tags?.attachedTags?.length){
			this._chartTags$.next(this.chartConfig.tags?.attachedTags);
		}

	}

	private _initialize(): void{
		this._registerIntersectionObserver();

		combineLatest([

			this._inputChanges$.asObservable(),
			this._previewSubject$.asObservable().pipe(filter(x => x))
		])
		.pipe(
			takeUntil(this._destroyed)
		)
		.subscribe(() => {
			this._buildChartOptions();
		})

	}


	private _registerChartLockObserver():void{

		if(this.chartLockZoomService){
			if(
				[
					IndicatorDashboardChartType.Line,
					IndicatorDashboardChartType.Bar,
					IndicatorDashboardChartType.PolarBar,
				].includes(this.chartConfig?.type)
			){
				const dataZoomArray: DataZoomComponentOption[] = Array.isArray(this.chartOptions.dataZoom) ? this.chartOptions.dataZoom : [this.chartOptions.dataZoom];
				this.supportsZoom = !!dataZoomArray?.some(dataZoomItem => dataZoomItem?.type === 'inside');
			}

			if(this.chartConfig.type === IndicatorDashboardChartType.Map){
				this.supportsZoom = true;
			}
		}


		this.chartLockZoomService?.graphsLocked$.pipe(
			distinctUntilChanged(),
			takeUntil(this._destroyed)
		)
		.subscribe(isLocked =>{

			if(this.chartConfig.type === IndicatorDashboardChartType.Line){
				this.merge = {
					dataZoom: this.chartBuilderService.buildLineChartZoomLock({
						dataZoom: this.chartOptions?.dataZoom,
						lock: isLocked
					})
				}
			}

			if(this.chartConfig.type === IndicatorDashboardChartType.Bar){
				this.merge = {
					dataZoom: this.chartBuilderService.buildLineChartZoomLock({
						dataZoom: this.chartOptions?.dataZoom,
						lock: isLocked
					})
				}
			}

			if(this.chartConfig.type === IndicatorDashboardChartType.PolarBar){
				this.merge = {
					dataZoom: this.chartBuilderService.buildLineChartZoomLock({
						dataZoom: this.chartOptions?.dataZoom,
						lock: isLocked
					})
				}
			}


			if(this.chartConfig.type === IndicatorDashboardChartType.Map){
				this.merge = {
					series: this.chartBuilderService.buildGeoMapZoomLock({
						series: this.chartOptions.series,
						lock: isLocked
					})
				}
			}

		})
	}

	private _registerIntersectionObserver():void{
		if(this._observer){
			return;
		}
		this._observer = new IntersectionObserver((event) =>{

			event.forEach(entry => {
				if((entry.target === this.elementRef.nativeElement) && entry.isIntersecting){
					this._previewSubject$.next(true);
					this._unregisterObserver();
				}
			})
		}, {
			root: null,
			threshold: .1
		});


		this._observer.observe(this.elementRef.nativeElement);
	}
	private _unregisterObserver(): void{
		this._observer?.unobserve(this.elementRef.nativeElement);
		this._observer.disconnect();
		this._observer = null;
	}

	// unauthenticate(): void{
	// 	this.authService.selectedTenant(null);
	// }
	// request(): void{
	// 	this.indicatorPointService.getIndicatorPointReport(this.chartConfig.indicatorId, this._buildIndicatorLookup(), true).subscribe(response => console.log({response}));
	// }

	private _buildChartOptions(): void {
		this._ComponentInitializedSubject$.next(false);
		this._ComponentInitializedSubject$.next(true);
		this.chartOptions = null;
		if (!this.chartConfig) {
			// this.chartOptions = null;
			return;
		}

		//TODO REMOVE IN FUTURE once no more cases in "generateChartOptions"
		const availableResponseExtractionTypes = [
			IndicatorDashboardChartType.Line,
			IndicatorDashboardChartType.Bar,
			IndicatorDashboardChartType.PolarBar,
			IndicatorDashboardChartType.Map,
			IndicatorDashboardChartType.Pie,
			IndicatorDashboardChartType.TreeMap,
			IndicatorDashboardChartType.Sankey,
			IndicatorDashboardChartType.Radar,
			IndicatorDashboardChartType.Scatter,
		]



		// TODO FIX THIS
		if(!availableResponseExtractionTypes.includes(this.chartConfig.type)){
			this.chartOptions = this.generateChartOptions(this.chartConfig);
			this._registerChartLockObserver();
			return;
		}


		try {
			const indicatorId = this.chartConfig?.indicatorId; ;


			if (!indicatorId) {
				console.warn('No indicator id set');
				return;
			}

			this.errorLoading = false;
			this.noDataFound = false;
			let getIndicatorPointReportResult: Observable<AggregateResponseModel> = null;
			if (this.token == null || this.token.length == 0) {
				getIndicatorPointReportResult = this.indicatorPointService.getIndicatorPointReport(
					indicatorId, this.chartHelperService.buildChartIndicatorLookup(this.chartConfig, this.indicatorQueryParams, this._extraFields),
					true
				);
			} else {
				getIndicatorPointReportResult = this.publicService.reportPublic({
					dashboardId: this.indicatorQueryParams?.dashboard,
					token: this.token,
					indicatorId: Guid.parse(indicatorId),
					chartId: this.chartConfig.chartId,
					...this.chartHelperService.buildChartIndicatorLookup(this.chartConfig, this.indicatorQueryParams, this._extraFields)
				});
			}
			getIndicatorPointReportResult
			.pipe(
				takeUntil(this._destroyed)
			)
			.subscribe((response) => {
				try{

					buildConfiguration: { //  TODO MAKE IT A SWITCH CASE OR A FUNCTION

						// * SANKEY
						if(this.chartConfig.type === IndicatorDashboardChartType.Sankey){
							const links = this.dataTransformService.aggregateResponseModelToConnections(response, this.chartConfig as IndicatorDashboardSankeyChartConfig);

							// if(!links.length){
							// 	this.noDataFound = true;
							// 	return;
							// }

							function* spaceAppender(initialString: string = ''){
								while(true){
									initialString += ' ';
									yield initialString
								}
							}

							const sourceSet = new Set(links.map(x => x.source));
							const targetsSet = new Set(links.map(x => x.target));

							const cycles = [
								...new Set<string>(
									links.filter(x  => x.source === x.target)
									.map(x => x.target)
								)
							];



							const targetReplacements = cycles?.reduce((aggr,cycle) =>{
								const generator = spaceAppender(cycle);

								let nominee = generator.next().value as string;

								while(targetsSet.has(nominee) && sourceSet.has(nominee)){
									nominee = generator.next().value as string;
								}

								return [...aggr, {previous: cycle, current: nominee}];

							}, []) as {previous: string, current: string} [];


							targetReplacements?.forEach(replacement =>{
								links
									.filter(link => link.target === replacement.previous)
									.forEach(link => link.target = replacement.current);
							});



							const data = [
								...links.reduce((all, current) =>{
									all.add(current.source);
									all.add(current.target);
									return all;
								} , new Set())
							].map(x => ({name: x as string}))

							this.chartOptions = this.chartBuilderService.buildSankeyOptions({
								config: this.chartConfig as IndicatorDashboardSankeyChartConfig,
								data: {
									data,
									links
								},
								onShare: () => {
									this.shareGraph();
								}});

							break buildConfiguration;
						}
						const {seriesData : sd, standAloneData} = this.dataTransformService.aggregateResponseModelToLineChartDataFromConfiguration(response, this.chartConfig); // TODO FIX THIS


						const [seriesData] = sd;

						if (!seriesData.labels?.length) {
							//this.noDataFound = true;
							//return;
							seriesData.labels = [];
						}




						//  * SCATTERER CHART
						if (this.chartConfig.type === IndicatorDashboardChartType.Scatter) {
							if (!seriesData) {
								console.warn('No series found for line chart');
								return;
							}

							this.chartOptions = this.chartBuilderService.buildScatterOptions({
								config: this.chartConfig as IndicatorDashboardScatterChartConfig,
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownload: () => {
									this.downloadData();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
								labels: seriesData?.labels ?? [],
								series: seriesData?.series ?? {}
							})
							break buildConfiguration;
						}


						//  * RADAR CHART
						if (this.chartConfig.type === IndicatorDashboardChartType.Radar) {
							if (!seriesData) {
								console.warn('No series found for line chart');
								return;
							}

							this.chartOptions = this.chartBuilderService.buildRadarOptions({
								config: this.chartConfig as IndicatorDashboardRadarChartConfig,
								// radarIndicatorOptions,
								inputSeries: seriesData?.series ?? {},
								labels: seriesData?.labels ?? [],
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownload: () => {
									this.downloadData();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
							})
							break buildConfiguration;
						}

						//  * LINE CHART
						if (this.chartConfig.type === IndicatorDashboardChartType.Line) {
							if (!seriesData) {
								console.warn('No series found for line chart');
								return;
							}

							this.chartOptions = this.chartBuilderService.buildLine({
								config: this.chartConfig as IndicatorDashboardLineChartConfig,
								inputSeries: seriesData?.series ?? {},
								labels: seriesData?.labels ?? [],
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownload: () => {
									this.downloadData();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
							});
							break buildConfiguration;
						}


						// * BAR
						if (this.chartConfig.type === IndicatorDashboardChartType.Bar) {
							if (!seriesData) {
								console.warn('No series found for line bar');
								return;
							}
							this.chartOptions = this.chartBuilderService.buildBar({
								config: this.chartConfig as IndicatorDashboardBarChartConfig,
								inputSeries: seriesData?.series ?? {},
								labels: seriesData?.labels ?? [],
								onDownload: () => {
									this.downloadData();
								},
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
							});
							break buildConfiguration;
						}

						// * POLAR BAR
						if (this.chartConfig.type === IndicatorDashboardChartType.PolarBar) {
							if (!seriesData) {
								console.warn('No series found for polar bar');
								return;
							}
							this.chartOptions = this.chartBuilderService.buildPolarBar({
								config: this.chartConfig as IndicatorDashboardPolarBarChartConfig,
								inputSeries: seriesData?.series ?? {},
								labels: seriesData?.labels ?? [],
								onDownload: () => {
									this.downloadData();
								},
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
							});
							break buildConfiguration;
						}

						// * MAP
						if (this.chartConfig.type === IndicatorDashboardChartType.Map) {
							if (!seriesData) {
								console.warn('No series found for line map');
								return;
							}
							const { series, labels } = seriesData ?? { series: {} , labels: []};
							registerMap('worldmap', mapGeo);
							const seriesIds = Object.keys(series);

							const mapChartConfig = this.chartConfig as IndicatorDashboardMapChartConfig;
							this.chartOptions =  this.chartBuilderService.buildGeoMap({

								config: mapChartConfig as IndicatorDashboardMapChartConfig,
								data: seriesIds.map(sid => ({
									data: series[sid].data.map((value, _index) => ({
											value: value,

											// * give higher priortity to configurationMapping
											name: mapChartConfig.countryNameMapping?.[labels[_index]] ?? MAP_GEO_DICTIONARY[labels[_index]]
										})),
									description: sid
								}))
								,onDownload: () => {
									this.downloadData();
								},
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
							});
							break buildConfiguration;
						}

						// * TREE MAP
						if(this.chartConfig.type === IndicatorDashboardChartType.TreeMap){

							const { series, labels } = seriesData ?? { series: {} , labels: []};

							const treeMapDataArray : TreeMapData[][] = Object.keys(series).map(key =>{
								const data = series[key].data;
								if(data.length !== labels.length){
									console.warn('Mismatch lengths - Treemap ')
									return null;
								}
								return data.map((value, index) => ({name: labels[index], value: value}) )
							}).filter(x => x);

							if(treeMapDataArray.length !== 1){
								console.warn('More than one treemap data array. selecting first');
							}
							let treeMapdata = treeMapDataArray[0];

							const relationshipResult = this.dataTransformService.getParentChildrenRelationships({
								records: response,
								relationShipConfig: (this.chartConfig as IndicatorDashboardTreeMapChartConfig)?.treeNesting
							})

							if(relationshipResult && relationshipResult?.codeMap?.size && relationshipResult?.relationShips?.size){

								// map by labelValue
								const map = new Map<string, Set<string>>();
								for( const [code, params ] of relationshipResult.relationShips.entries()){
									map.set(relationshipResult.codeMap.get(code), new Set(params.children.map(x => relationshipResult.codeMap.get(x))))
								}

								treeMapdata.forEach(item => {
									item.children = treeMapdata.filter(x => map.get(item.name)?.has(x.name))
								})
								treeMapdata = treeMapdata.filter(x => x.children?.length)
							}


							this.chartOptions = this.chartBuilderService.buildTreeMap({
								config: this.chartConfig as IndicatorDashboardTreeMapChartConfig,
								data:treeMapdata,
								onDownload: () => {
									this.downloadData();
								},
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
							});
							break buildConfiguration;
						}

						// * PIE CHART
						if (this.chartConfig.type === IndicatorDashboardChartType.Pie) {
							let standAloneDataFromSeries:StandAloneData[];
							standAloneCheck: if (!standAloneData) {
								console.warn('No Data found for pie');

								if(!seriesData) return;

								// * get unique sereis Key
								const seriesKeys = Object.keys(seriesData?.series ?? {});
								if(seriesKeys.length !== 1){
									console.warn('Unique key for dataseries not found - chartpie');
									break standAloneCheck;
								}
								const uniqueSeriesKey = seriesKeys[0];

								const labels = seriesData?.labels ?? [];
								const data = seriesData?.series?.[uniqueSeriesKey]?.data;

								if(labels.length !== data?.length){
									console.warn('Arrays length misshmatch - chartpie');
									break standAloneCheck;
								}

								standAloneDataFromSeries = labels.map((label, index) => ({name: label, value: data[index]}));
							}

							if(!standAloneData && !standAloneDataFromSeries){
								console.warn('Standalone data from series not found')
								return;
							}

							this.chartOptions = this.chartBuilderService.buildPie({
								config: this.chartConfig as any,
								pieData: standAloneData ?? standAloneDataFromSeries,
								onDownload: () => {
									this.downloadData();
								},
								onFiltersOpen: () => {
									this.openFilters();
								},
								onDownloadJSON: () =>{
									this.downloadJsonData()
								},
								onShare: () => {
									this.shareGraph();
								},
							});
							break buildConfiguration;
						}
					}

					this._registerChartLockObserver();

				}catch(errorInner){
					this.errorLoading = true;
					console.error(errorInner);
				}

			},
			error => this.onLoadError(error));
		} catch(error) {
			console.error(error);
		}


	}




	private generateChartOptions(chartConfig: BaseIndicatorDashboardChartConfig): EChartsOption {
		// const xAxisData: string[] = [];
		// const data1: number[] = [];
		// const data2: number[] = [];

		// for (let i = 0; i < 20; i++) {
		// 	xAxisData.push('category' + i);
		// 	data1.push(Math.abs((Math.sin(i / 5) * (i / 5 - 10) + i / 6) * 5));
		// 	data2.push(Math.abs((Math.cos(i / 5) * (i / 5 - 10) + i / 6) * 5));
		// }

		// if (this.chartConfig.type === IndicatorDashboardChartType.Scatter) {
		// 	return this.chartBuilderService.buildScatterOptions({config: this.chartConfig as IndicatorDashboardScatterChartConfig});
		// }
		if (this.chartConfig.type === IndicatorDashboardChartType.Graph) {
			return this.chartBuilderService.buildGraphOptions({
				config: this.chartConfig as IndicatorDashboardGraphChartConfig,
				graph: GRAPH_OPTIONS,
				onShare: () => {
					this.shareGraph();
				}
			});
		}
		// if(this.chartConfig.type === IndicatorDashboardChartType.Radar){
		// 	return this.chartBuilderService.buildRadarOptions({config: this.chartConfig as IndicatorDashboardRadarChartConfig, radarIndicatorOptions: []})
		// }

	}



	public selectTag(tagString: string): void{
		this.dashboardTagsUIService.toggleChartTag(tagString);
	}

	private onLoadError(error: any): void {
		this.errorLoading = true;
	}

	public openFilters(): void {


		const data: IndicatorListingFiltersComponentData = {
			config: this.chartConfig,
			values: this._extraFields,
			bannedValues: this.indicatorQueryParams?.keywordFilters?.reduce((aggr, current) => {
				const currentBan = {};

				currentBan[current.field] = [current.values?.[0]]; // TODO
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
			.exportXlsx(this.chartConfig.indicatorId, this.chartHelperService.buildChartIndicatorLookup(this.chartConfig, this.indicatorQueryParams, this._extraFields))
			.pipe(
				takeUntil(this._destroyed)
			)
			.subscribe(response => {

				const filenametosave = this.getFilenameFromContentDispositionHeader(response.headers.get('Content-Disposition'));

				FileSaver.saveAs(response.body, filenametosave);
			});
	}

	public shareGraph(): void {
		const config: IndicatorPointReportExternalTokenPersist = {
			name: this.indicatorQueryParams.dashboard + this.chartConfig.chartName, //TODO
			lookups: [{
				chartId: this.chartConfig.chartId,
				dashboardId: this.indicatorQueryParams.dashboard,
				indicatorId: Guid.parse(this.chartConfig.indicatorId),
				lookup: this.chartHelperService.buildChartIndicatorLookup(this.chartConfig, this.indicatorQueryParams, this._extraFields)
			}]
		};
		const data: ShareDialogData = {
			config: config,
			indicatorQueryParams: {
				displayName: this.indicatorQueryParams.displayName,
				dashboard: this.indicatorQueryParams.dashboard,
				keywordFilters: []
			},
			chartId: this.chartConfig.chartId
		}
		this.dialog.open(ShareDialogComponent, {
			width: '400px',
			data: data
		})
			.afterClosed()
			.subscribe(() => { });

	}

	public downloadJsonData(): void{
		this.indicatorPointService
			.exportJSON(this.chartConfig.indicatorId, this.chartHelperService.buildChartIndicatorLookup(this.chartConfig, this.indicatorQueryParams, this._extraFields))
			.pipe(
				takeUntil(this._destroyed)
			)
			.subscribe(response => {

				const filenametosave = this.getFilenameFromContentDispositionHeader(response.headers.get('Content-Disposition'));

				FileSaver.saveAs(response.body, filenametosave);
			});
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


interface ChartTag{
	selected: boolean;
	name: string;
}


export interface ExtraFilterField{
	fieldCode: string;
	value: any;
	indicatorFilterType: IndicatorFilterType ;
	displayName?: string;
}
