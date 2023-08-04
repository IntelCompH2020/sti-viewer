import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { PublicService } from '@app/core/services/http/public.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { BaseComponent } from '@common/base/base.component';
import { HttpError, HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { map, takeUntil } from 'rxjs/operators';
import { BaseIndicatorDashboardChartConfig, GaugesBlock, IndicatorDashboardChart, IndicatorDashboardChartGroupConfig, IndicatorDashboardConfig, IndicatorDashboardTabConfig, TabBlockConfig, TabBlockType } from '../indicator-dashboard/indicator-dashboard-config';
import { DashboardUITagsService } from '../indicator-dashboard/ui-services/dashboard-tags.service';
import { USE_CACHE } from '@app/core/services/tokens/caching.token';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { ChartLockZoomService } from '../indicator-dashboard/ui-services/chart-lock-zoom.service';
import { IndicatorQueryParams } from '../indicator-dashboard/indicator-dashboard.component';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';

@Component({
	selector: 'app-shared-dashboard',
	templateUrl: './shared-dashboard.component.html',
	styleUrls: ['./shared-dashboard.component.scss'],
	animations: GENERAL_ANIMATIONS,
	providers:[
		DashboardUITagsService,
		{
			provide: USE_CACHE, useValue: true
		},
		IndicatorPointService,
		ChartLockZoomService
	]
})
export class SharedDashboardComponent extends BaseComponent implements OnInit {

	public dashboardConfig: IndicatorDashboardConfig = null;
	public token: string = null;
	public indicatorQueryParams: IndicatorQueryParams;

	constructor(
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private _queryParamsService: QueryParamsService,
		private activaterRoute: ActivatedRoute,
		protected authService: AuthService,
		private publicService: PublicService

	) {
		super();
	}
	ngOnInit(): void {
		this.dashboardConfig = null;
		this.activaterRoute.queryParamMap.subscribe(params => {
			this.token = params.get('token');
			const dashboardId = params.get('dashboardId');
			if (!this.token || !dashboardId || this.token.length == 0 || dashboardId.length == 0) return;
			if(params.get('params')){
				this.indicatorQueryParams = this._queryParamsService.deserializeObject<IndicatorQueryParams>(params.get('params'));
			}

			this.publicService.dashboardLookup({
				dashboardId: dashboardId,
				token: this.token
			}).pipe(takeUntil(this._destroyed)).subscribe(
				dashboardConfig => {
					this.dashboardConfig =  this.buildPublicDashboardConfig(dashboardConfig);
				},
				error => this.onCallbackError(error)
			);
		});

	}
	onCallbackError(errorResponse: HttpErrorResponse) {
		const error: HttpError = this.httpErrorHandlingService.getError(errorResponse);
		this.uiNotificationService.snackBarNotification(error.getMessagesString(), SnackBarNotificationLevel.Warning);

	}

	private buildPublicDashboardConfig(dashboardConfig: IndicatorDashboardConfig): IndicatorDashboardConfig {
		if (dashboardConfig == null || dashboardConfig.tabs == null) return null;

		const dashboardConfigClean: IndicatorDashboardConfig = {
			id: dashboardConfig.id,
			tabs: []
		};

		for (let i = 0; i < dashboardConfig.tabs.length; i++) {
			const tab = dashboardConfig.tabs[i];

			if (tab == null || tab.chartGroups == null) continue;
			const tabClean: IndicatorDashboardTabConfig = {name: tab.name, chartGroups: []};

			for (let j = 0; j < tab.chartGroups.length; j++) {
				const chartGroup: TabBlockConfig = tab.chartGroups[j];
				if (chartGroup == null) continue;

				switch (chartGroup.type) {

					case TabBlockType.Gauge: {
						const chartGroupClean: GaugesBlock = { name: chartGroup.name, type: TabBlockType.Gauge, gauges: []};
						const charts = (chartGroup as GaugesBlock)?.gauges
						if (charts == null) continue;
						for (let k = 0; k < charts.length; k++) {
							const chart = charts[k];
							if (chart.chartId != null && chart.chartId.length > 0) {
								chart.chartShare = null;
								chartGroupClean.gauges.push(chart);
							}
						}
						if (chartGroupClean.gauges.length > 0) tabClean.chartGroups.push(chartGroupClean);
						break;
					}
					case TabBlockType.ChartGroup:
					default: {
						const chartGroupClean: IndicatorDashboardChartGroupConfig = { name: chartGroup.name, type: TabBlockType.ChartGroup, charts: []};
						const charts = (chartGroup as IndicatorDashboardChartGroupConfig)?.charts
						if (charts == null) continue;
						for (let k = 0; k < charts.length; k++) {
							const chart = charts[k];
							if (chart.chartId != null && chart.chartId.length > 0) {
								chart.chartDownloadData = null;
								chart.chartDownloadImage = null;
								chart.chartDownloadJson = null;
								chart.chartShare = null;
								chart.filters = null;

								chartGroupClean.charts.push(chart);
							}
						}
						if (chartGroupClean.charts.length > 0) tabClean.chartGroups.push(chartGroupClean);

						break;
					}
				}

			}
			if (tabClean.chartGroups.length > 0) dashboardConfigClean.tabs.push(tabClean);

		}

		return dashboardConfigClean;
	}
}
