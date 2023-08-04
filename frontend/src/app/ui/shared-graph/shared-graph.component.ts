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
import { BaseIndicatorDashboardChartConfig, BaseIndicatorDashboardGaugeConfig, GaugesBlock, IndicatorDashboardChart, IndicatorDashboardChartGroupConfig, IndicatorDashboardConfig, TabBlockConfig, TabBlockType } from '../indicator-dashboard/indicator-dashboard-config';
import { DashboardUITagsService } from '../indicator-dashboard/ui-services/dashboard-tags.service';
import { USE_CACHE } from '@app/core/services/tokens/caching.token';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { ChartLockZoomService } from '../indicator-dashboard/ui-services/chart-lock-zoom.service';
import { IndicatorQueryParams } from '../indicator-dashboard/indicator-dashboard.component';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';

@Component({
	selector: 'app-shared-graph',
	templateUrl: './shared-graph.component.html',
	styleUrls: ['./shared-graph.component.scss'],
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
export class SharedGraphComponent extends BaseComponent implements OnInit {

	public chart: BaseIndicatorDashboardChartConfig = null;
	public gauge: BaseIndicatorDashboardGaugeConfig = null;
	public chartGroup: TabBlockConfig = null;
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
		this.chart = null;
		this.activaterRoute.queryParamMap.subscribe(params => {
			this.token = params.get('token');
			const dashboardId = params.get('dashboardId');
			const chartId = params.get('chartId');
			if (!this.token || !dashboardId || !chartId || this.token.length == 0 || chartId.length == 0 || dashboardId.length == 0) return;
			if(params.get('params')){
				this.indicatorQueryParams = this._queryParamsService.deserializeObject<IndicatorQueryParams>(params.get('params'));
			}

			this.publicService.dashboardLookup({
				dashboardId: dashboardId,
				token: this.token
			}).pipe(takeUntil(this._destroyed)).subscribe(
				dashboardConfig => {
					this.setChart(dashboardConfig, chartId);
				},
				error => this.onCallbackError(error)
			);
		});

	}
	onCallbackError(errorResponse: HttpErrorResponse) {
		const error: HttpError = this.httpErrorHandlingService.getError(errorResponse);
		this.uiNotificationService.snackBarNotification(error.getMessagesString(), SnackBarNotificationLevel.Warning);

	}

	private setChart(dashboardConfig: IndicatorDashboardConfig, chartId: string) {
		if (dashboardConfig == null || dashboardConfig.tabs == null) return null;

		for (let i = 0; i < dashboardConfig.tabs.length; i++) {
			const tab = dashboardConfig.tabs[i];
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
							if (chart.chartId == chartId) {
								chart.chartShare = null;
								this.gauge = chart;
								this.chartGroup = chartGroup;
								return;
							}
						}
					}
					case TabBlockType.ChartGroup:
					default: {
						const charts = (chartGroup as IndicatorDashboardChartGroupConfig)?.charts
						if (charts == null) continue;
						for (let k = 0; k < charts.length; k++) {
							const chart = charts[k];
							if (chart.chartId == chartId) {
								chart.chartDownloadData = null;
								chart.chartDownloadImage = null;
								chart.chartDownloadJson = null;
								chart.chartShare = null;
								chart.filters = null;
								this.chart = chart;
								this.chartGroup = chartGroup;
								return;
							}
						}
					}
				}

			}

		}
	}
}
