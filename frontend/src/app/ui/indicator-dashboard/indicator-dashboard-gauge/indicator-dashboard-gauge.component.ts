import { Component, Input, OnChanges, OnInit, ElementRef } from '@angular/core';
import { ChartBuilderService } from '@app/core/services/data-transform/charts-common.service';
import { DataTransformService } from '@app/core/services/data-transform/data-transform.service';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { BehaviorSubject, combineLatest, interval, Observable, of, Subject } from 'rxjs';
import { delayWhen, filter, map, takeUntil } from 'rxjs/operators';
import { BaseIndicatorDashboardGaugeConfig, GaugeType, IndicatorFilterType} from '../indicator-dashboard-config';
import { IndicatorQueryParams } from '../indicator-dashboard.component';
import { IndicatorPointReportLookup, RawDataRequest } from '@app/core/query/indicator-point-report.lookup';
import { IndicatorPointLookup } from '@app/core/query/indicator-point.lookup';
import moment from 'moment';
import { DashboardUITagsService } from '../ui-services/dashboard-tags.service';

@Component({
    templateUrl:'./indicator-dashboard-gauge.component.html',
    styleUrls:[
        './indicator-dashboard-gauge.component.scss'
    ],
    selector: 'app-indicator-dashboard-gauge',
    animations: GENERAL_ANIMATIONS
})
export class IndicatorDashboardGaugeComponent extends BaseComponent implements OnInit, OnChanges {

	@Input() 
    gaugeConfig: BaseIndicatorDashboardGaugeConfig;

	@Input()
	indicatorQueryParams: IndicatorQueryParams;

	private _observer: IntersectionObserver;
	private _previewSubject$ = new BehaviorSubject<boolean>(false);
	private _inputChanges$ = new Subject<void>();

	protected noDataFound: boolean = false;

	private _extraFields: {fieldCode: string, value: any, indicatorFilterType: IndicatorFilterType  }[] = [];
	private _ComponentInitializedSubject$ = new BehaviorSubject<boolean>(false);
	public errorLoading = false;

	public uiMinimumTimePased$ = this._ComponentInitializedSubject$.asObservable().pipe(
		delayWhen(val => val ? interval(100) : of(val)),
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

	valueCards: ValueCard[];

	constructor(
		private chartBuilderService: ChartBuilderService,
		private dataTransformService: DataTransformService,
		private indicatorPointService: IndicatorPointService,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private elementRef:ElementRef,
		private dashboardTagsUIService: DashboardUITagsService,
		) {
		super();


		
		this._initialize()


	}
	ngOnInit(): void {

		
	}
	ngOnChanges(): void {
		this._inputChanges$.next();

		if(this.gaugeConfig?.tags?.attachedTags?.length){
			this._chartTags$.next(this.gaugeConfig.tags?.attachedTags);
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

	private _buildChartOptions(): void {
		this._ComponentInitializedSubject$.next(false);
		this._ComponentInitializedSubject$.next(true);
		if (!this.gaugeConfig) {
			// this.chartOptions = null;
			return;
		}

		//TODO REMOVE IN FUTURE once no more cases in "generateChartOptions"
		const availableResponseExtractionTypes = [
			GaugeType.ValueCard,
		]
		if (availableResponseExtractionTypes.includes(this.gaugeConfig.type)) {
			try { // TODO FIX THIS

				const indicatorId = this.gaugeConfig?.indicatorId; ;


				if (!indicatorId) {
					console.warn('No indicator id set');
					return;
				}

				this.errorLoading = false;
				this.noDataFound = false;
				this.indicatorPointService.getIndicatorPointReport(
					indicatorId, this._buildIndicatorLookup(),
					true
				)
				.pipe(
					takeUntil(this._destroyed)
				)
				.subscribe((response) => {
					try{
						const {seriesData : sd, standAloneData} = this.dataTransformService.aggregateResponseModelToLineChartDataFromConfiguration(response, this.gaugeConfig); // TODO FIX THIS
					

						const [seriesData] = sd;

						if(!seriesData.labels?.length){
							this.noDataFound = true;
							return;
						}

						if(this.gaugeConfig.type === GaugeType.ValueCard){
							
                            
                            // console.log({ seriesData });

							if(!seriesData) return;

								// * get unique sereis Key
								const seriesKeys = Object.keys(seriesData.series);
								if(seriesKeys.length !== 1){
									console.warn('Unique key for dataseries not found - value card');
									// break standAloneCheck;
									return;
								}
								const uniqueSeriesKey = seriesKeys[0];

								const labels = seriesData.labels;
								const data = seriesData.series[uniqueSeriesKey]?.data;


								this.valueCards  = labels
									// .slice(0,4)
									.map((label, index) => ({
										title: this.gaugeConfig.labelOverride ?? label,
										value: data[index]
									}));

							return;
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
	}



	public selectTag(tagString: string): void{
		this.dashboardTagsUIService.toggleChartTag(tagString);
	}

	private onLoadError(error: any): void {
		this.errorLoading = true;
	}

	private _buildIndicatorLookup(): IndicatorPointReportLookup {

		const {metrics, bucket} = this.dataTransformService.configurationToBucketsAndMetrics(this.gaugeConfig);

		const lookup = new IndicatorPointReportLookup();

		lookup.bucket = bucket;
		lookup.metrics = metrics;

		if (this.indicatorQueryParams?.keywordFilters?.length || this._extraFields.length || this.indicatorQueryParams?.groupHash) {
			const indicatorPointLookup = new IndicatorPointLookup();

			//query params
			if (this.indicatorQueryParams?.keywordFilters?.length) {
				
				indicatorPointLookup.keywordFilters = this.indicatorQueryParams.keywordFilters;
			}

			if (this.indicatorQueryParams?.groupHash) {
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
		
		// ** Replace/add keywords filters coming from dashboard configuration if any
		const staticKeywordFilters = this.gaugeConfig?.staticFilters?.keywordsFilters;
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
		rawDataBlock: if(this.gaugeConfig.rawDataRequest){

			const {keyField, order, valueField, page} = this.gaugeConfig.rawDataRequest;

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


interface ChartTag{
	selected: boolean;
	name: string;
}

interface ValueCard{
	title: string,
	value: string | number 
}