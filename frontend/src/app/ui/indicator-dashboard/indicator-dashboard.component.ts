import { Component, HostListener, Input, OnInit, Optional } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BookmarkType } from '@app/core/enum/bookmark-type.enum';
import { Bookmark } from '@app/core/model/bookmark/bookmark.model';
import { IndicatorPointKeywordFilter } from '@app/core/query/indicator-point.lookup';
import { BookmarkService } from '@app/core/services/http/bookmark.service';
import { IndicatorDashboardService } from '@app/core/services/http/indicator-dashboard.service';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { USE_CACHE } from '@app/core/services/tokens/caching.token';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { combineLatest, of , BehaviorSubject, Observable} from 'rxjs';
import { switchMap, takeUntil, map, tap } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { DashboardUITagsService } from './ui-services/dashboard-tags.service';
import { BaseIndicatorDashboardGaugeConfig, GaugesBlock, IndicatorDashboardChartGroupConfig, IndicatorDashboardConfig, TabBlockConfig, TabBlockType } from './indicator-dashboard-config';
import { DashboardTabBlockToChartGroupBlockPipe } from '../../core/formatting/pipes/dashboard-chart-group.pipe';
import { DefaultsService } from '@app/core/services/data-transform/defaults.service';
import { ChartLockZoomService } from './ui-services/chart-lock-zoom.service';
import { IndicatorPointChartExternalTokenPersist, IndicatorPointReportExternalTokenPersist } from '@app/core/model/external-token/external-token.model';
import { Guid } from '@common/types/guid';
import { ExternalTokenService } from '@app/core/services/http/external-token.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { ChartHelperService } from '@app/core/services/ui/chart-helper.service';
import { MatDialog } from '@angular/material/dialog';
import { ShareDialogComponent, ShareDialogData } from './share-dialog/share-dialog.component';
import { AuthService } from '@app/core/services/ui/auth.service';
import { AppPermission } from '@app/core/enum/permission.enum';


// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-climate.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-climate-gr.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-agrifood.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-agrifood-gr.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-cancer-eu.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-cancer-world.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-cancer-ec.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-ai.json');
// const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config-ai-es.json');

@Component({
	selector: 'app-indicator-dashboard',
	templateUrl: './indicator-dashboard.component.html',
	styleUrls: ['./indicator-dashboard.component.css'],
	providers:[
		DashboardUITagsService ,
		{
			provide: USE_CACHE, useValue: true
		},
		IndicatorPointService,
		ChartLockZoomService
	]
})
export class IndicatorDashboardComponent extends BaseComponent implements OnInit {
	@Input() isEmbedded = false;
	@Input() token: string = null;
	@Input() staticDashboardConfig: IndicatorDashboardConfig = null;

	selectedTabIndex = -1;
	public canShareDashboard = false;

	dashboardConfig:IndicatorDashboardConfig;


	indicatorQueryParams: IndicatorQueryParams;

	bookmark: Partial<Bookmark>;

	bookmarks: Bookmark[];

	private _availableUniqueTags$ = new BehaviorSubject<string[]>([])


	tags$: Observable<UniqueTag[]> = combineLatest([
		this._availableUniqueTags$,
		this.dashboardTagsUIService.activeChartTagsChanges$
	]).pipe(
		map(([tags, activeTags]) => tags?.map(tag => ({
			selected: !!activeTags?.includes(tag),
			value: tag
		})) ?? [])
	 )



	@HostListener('window:keyup', ['$event'])
	keyUp(event: KeyboardEvent) {

		if(event.key.toLowerCase() !== 'shift'){
			return;
		}

		console.info("locking graphs")
		this.chartLockZoomService?.lockGraphs();
	}


	@HostListener('window:keydown', ['$event'])
	keyDown(event: KeyboardEvent) {
		if(event.repeat){
			return;
		}
		if(event.key.toLowerCase() !== 'shift' ){
			return
		}

		console.info("unlocking graphs");
		this.chartLockZoomService?.unlockGraphs();

	}

	constructor(
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private _activaterRoute: ActivatedRoute,
		private _queryParamsService: QueryParamsService,
		private _dashboardService: IndicatorDashboardService,
		private bookmarkService: BookmarkService,
		private dashboardTagsUIService: DashboardUITagsService,
		private tabBlockToChartGroupPipe: DashboardTabBlockToChartGroupBlockPipe,
		private defautlsService: DefaultsService,
		private chartHelperService: ChartHelperService,
		private dialog: MatDialog,
		public authService: AuthService,
		@Optional() private chartLockZoomService:ChartLockZoomService,
		) {
		super();


	}



	saveBookmark(): void{
		this.bookmarkService.persist({
			name: this.indicatorQueryParams.displayName,
			type: BookmarkType.Dashboard,
			value: JSON.stringify(this.indicatorQueryParams),
		},
		[
			nameof<Bookmark>(x => x.id)
		])
		.pipe(
			takeUntil(this._destroyed)
		)
		.subscribe(response => {
			this._validateBookmark();
		})
	}


	deleteBookmark(): void{
		if (this.bookmark){
			this.bookmarkService.delete(this.bookmark.id)
			.pipe(takeUntil(this._destroyed))
			.subscribe(() => this._validateBookmark());
		}

	}

	toggleBookmark():void{

		if (this.bookmark){
			this.deleteBookmark();
			return;
		}
		this.saveBookmark();
	}
	ngOnInit(): void {
		this.canShareDashboard = this.authService.hasPermission(AppPermission.CreateDashboardExternalToken);
		this._activaterRoute.queryParams.pipe(
			switchMap(
				params => {
					this.bookmark = null;
					if(params.params){
						this.indicatorQueryParams = this._queryParamsService.deserializeObject<IndicatorQueryParams>(params.params);

						if (this.token == null || this.token.length == 0 || this.staticDashboardConfig == null) {
							this._validateBookmark();
							return this._dashboardService.getDashboard(this.indicatorQueryParams.dashboard).pipe(tap((config) => console.log('%c Dashboard configuration', 'color:green', config)))
						} else {
							return of(this.staticDashboardConfig);
						}
					}
					// return of(DASHBOARD_CONFIG);
			}),
			map(
				dashboardConfig => this.defautlsService.enrichDashboardConfigWithDefaults(dashboardConfig)
			),
			takeUntil(this._destroyed)
		)
		.subscribe(
			(configuration: IndicatorDashboardConfig) => {
				this.dashboardConfig = configuration;



				const chartTags = [...(new Set(// unique tags

					configuration?.tabs?.map(
							tab => this.tabBlockToChartGroupPipe.transform(tab.chartGroups)?.map(
								chartGroup => chartGroup?.charts?.map(
									chart => chart?.tags?.attachedTags
								).filter(x => !!x && Array.isArray(x)).reduce((all, tagArray) => [...all, ...tagArray] , []) ?? []// chartGroupLevel tags
							).reduce((all, tags) => [...all, ...tags] , []) ?? [] // tab Level tags
					).reduce((all, tags) => [...all, ...tags] ,[]) ?? [] // configuration level tags

				))]
				this._availableUniqueTags$.next(chartTags);



				this.selectedTabIndex = 0;
			},
			error => {
				// this.dashboardConfig = DASHBOARD_CONFIG;
			}

		)
	}

	selectTab(index: number) {
		this.selectedTabIndex = index;
		//* Little bit hacky, find navigation content container and scroll on top (navigation content container has height 100% and overflows its content)
		document.getElementById('navigation-content-container')?.scrollTo?.({
			top: 0,
		});
	}

	selectTag(tag: string){
		this.dashboardTagsUIService.toggleChartTag(tag);
	}


	private _validateBookmark():void{

		// get bookmark
		this.bookmark = null;
		this.bookmarkService.exists({
			type: BookmarkType.Dashboard,
			value: this._queryParamsService.serializeObject(this.indicatorQueryParams),
		},
		[
			nameof<Bookmark>(x => x.id)
		]
		).pipe(takeUntil(this._destroyed)).subscribe((bookmark) =>{
			if(bookmark){
				this.bookmark = bookmark
			}
		})
	}

	public shareDashboard(): void {
		const config: IndicatorPointReportExternalTokenPersist = {
			name: this.indicatorQueryParams.dashboard + this.dashboardConfig.id , //TODO
			lookups: this.buildIndicatorPointReportLookups()
		};
		const data: ShareDialogData = {
			config: config,
			indicatorQueryParams: {
				displayName: this.indicatorQueryParams.displayName,
				dashboard: this.indicatorQueryParams.dashboard,
				keywordFilters: []
			}
		}
		this.dialog.open(ShareDialogComponent, {
  			width: '400px',
			data: data
		})
			.afterClosed()
			.subscribe(() => { });
	}

	private buildIndicatorPointReportLookups(): IndicatorPointChartExternalTokenPersist[] {
		if (this.dashboardConfig == null || this.dashboardConfig.tabs == null) return null;

		const lookups: IndicatorPointChartExternalTokenPersist[] =[];

		for (let i = 0; i < this.dashboardConfig.tabs.length; i++) {
			const tab = this.dashboardConfig.tabs[i];

			if (tab == null || tab.chartGroups == null) continue;

			for (let j = 0; j < tab.chartGroups.length; j++) {
				const chartGroup: TabBlockConfig = tab.chartGroups[j];
				if (chartGroup == null) continue;

				switch (chartGroup.type) {
					case TabBlockType.Gauge: {
						const charts = (chartGroup as GaugesBlock)?.gauges
						if (charts == null) continue;
						for (let k = 0; k < charts.length; k++) {
							const chart = charts[k];
							if (chart.chartId != null && chart.chartId.length > 0) {
								lookups.push({
									chartId: chart.chartId,
									dashboardId: this.indicatorQueryParams.dashboard,
									indicatorId: Guid.parse(chart.indicatorId),
									//lookup: this._buildIndicatorLookup()
									lookup: this.chartHelperService.buildGaugeIndicatorLookup(chart, this.indicatorQueryParams)
								});
							}
						}
						break;
					}
					case TabBlockType.ChartGroup:
					default: {
						const charts = (chartGroup as IndicatorDashboardChartGroupConfig)?.charts
						if (charts == null) continue;
						for (let k = 0; k < charts.length; k++) {
							const chart = charts[k];
							if (chart.chartId != null && chart.chartId.length > 0) {
								lookups.push({
									chartId: chart.chartId,
									dashboardId: this.indicatorQueryParams.dashboard,
									indicatorId: Guid.parse(chart.indicatorId),
									//lookup: this._buildIndicatorLookup()
									lookup: this.chartHelperService.buildChartIndicatorLookup(chart, this.indicatorQueryParams, this.chartHelperService.buildExtraFilterField(chart))
								});
							}
						}
						break;
					}
				}
			}
		}

		return lookups;
	}

}


export interface IndicatorQueryParams{
	dashboard: string;
	keywordFilters: IndicatorPointKeywordFilter[];
	displayName: string;
	groupHash?: string;
}


interface UniqueTag{
	selected: boolean;
	value: string;
}
