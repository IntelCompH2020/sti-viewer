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
import { AggregateResponseModel } from '@app/core/model/aggregate-response/aggregate-reponse.model';
import { Guid } from '@common/types/guid';
import { PublicService } from '@app/core/services/http/public.service';
import { ChartHelperService } from '@app/core/services/ui/chart-helper.service';
import { IndicatorPointReportExternalTokenPersist } from '@app/core/model/external-token/external-token.model';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { ExternalTokenService } from '@app/core/services/http/external-token.service';
import { ShareDialogComponent, ShareDialogData } from '../share-dialog/share-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
    templateUrl:'./indicator-dashboard-gauge.component.html',
    styleUrls:[
        './indicator-dashboard-gauge.component.scss'
    ],
    selector: 'app-indicator-dashboard-gauge',
    animations: GENERAL_ANIMATIONS
})
export class IndicatorDashboardGaugeComponent extends BaseComponent implements OnInit, OnChanges {

	@Input()  gaugeConfig: BaseIndicatorDashboardGaugeConfig;
	@Input() indicatorQueryParams: IndicatorQueryParams;
	@Input() showSpinner: boolean = false;
	@Input() token: string = null;

	private _observer: IntersectionObserver;
	private _previewSubject$ = new BehaviorSubject<boolean>(false);
	private _inputChanges$ = new Subject<void>();

	protected noDataFound: boolean = false;

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
		private dataTransformService: DataTransformService,
		private indicatorPointService: IndicatorPointService,
		private dialog: MatDialog,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private publicService: PublicService,
		private elementRef: ElementRef,
		private chartHelperService: ChartHelperService,
		private dashboardTagsUIService: DashboardUITagsService,
		) {
		super();



		this._initialize()


	}
	ngOnInit(): void {


	}
	ngOnChanges(): void {
		this._inputChanges$.next();
		console.log(this.gaugeConfig);
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
				let getIndicatorPointReportResult: Observable<AggregateResponseModel> = null;
				if (this.token == null || this.token.length == 0) {
					getIndicatorPointReportResult =
					this.indicatorPointService.getIndicatorPointReport(
						indicatorId, this.chartHelperService.buildGaugeIndicatorLookup(this.gaugeConfig, this.indicatorQueryParams),
						true
					);
				} else {
					getIndicatorPointReportResult = this.publicService.reportPublic({
						dashboardId: this.indicatorQueryParams?.dashboard,
						token: this.token,
						indicatorId: Guid.parse(indicatorId),
						chartId: this.gaugeConfig.chartId,
						...this.chartHelperService.buildGaugeIndicatorLookup(this.gaugeConfig, this.indicatorQueryParams)
					});
				}
				getIndicatorPointReportResult.pipe(
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

	public shareGraph(): void {
		const config: IndicatorPointReportExternalTokenPersist = {
			name: this.indicatorQueryParams.dashboard + this.gaugeConfig.labelOverride, //TODO
			lookups: [{
				chartId: this.gaugeConfig.chartId,
				dashboardId: this.indicatorQueryParams.dashboard,
				indicatorId: Guid.parse(this.gaugeConfig.indicatorId),
				lookup: this.chartHelperService.buildGaugeIndicatorLookup(this.gaugeConfig, this.indicatorQueryParams)
			}]
		};
		const data: ShareDialogData = {
			config: config,
			indicatorQueryParams: {
				displayName: this.indicatorQueryParams.displayName,
				dashboard: this.indicatorQueryParams.dashboard,
				keywordFilters: []
			},
			chartId: this.gaugeConfig.chartId
		}
		this.dialog.open(ShareDialogComponent, {
			width: '400px',
			data: data
		})
			.afterClosed()
			.subscribe(() => { });
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
