import { Component, Input, OnInit } from '@angular/core';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { takeUntil } from 'rxjs/operators';
import { DashboardUITagsService } from '../ui-services/dashboard-tags.service';
import { IndicatorDashboardChart, IndicatorDashboardChartGroupConfig, IndicatorDashboardConfig, IndicatorDashboardTabConfig, TabBlockType } from '../indicator-dashboard-config';
import { IndicatorQueryParams } from '../indicator-dashboard.component';

@Component({
	selector: 'app-indicator-dashboard-tab',
	templateUrl: './indicator-dashboard-tab.component.html',
	styleUrls: ['./indicator-dashboard-tab.component.scss']
})
export class IndicatorDashboardTabComponent extends BaseComponent implements OnInit {

	@Input()  tabConfig: IndicatorDashboardTabConfig;

	@Input() indicatorQueryParams:IndicatorQueryParams;

	@Input() dashboardConfig : IndicatorDashboardConfig;

	@Input() token: string = null;


	activeChartTags;

	TabBlockType = TabBlockType;

	constructor(
		protected uiNotificationService: UiNotificationService,
		private dashboardUiTagsService : DashboardUITagsService,
		protected httpErrorHandlingService: HttpErrorHandlingService) {
		super();
	}


	ngOnInit(): void {
		this.dashboardUiTagsService.activeChartTagsChanges$.pipe(
			takeUntil(this._destroyed)
		)
		.subscribe(tags => {
			this.activeChartTags = tags;
		})
	}
}
