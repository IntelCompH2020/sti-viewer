import { Component, Input, OnInit } from '@angular/core';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { IndicatorDashboardConfig, IndicatorDashboardTabConfig } from '../indicator-dashboard-config';
import { IndicatorQueryParams } from '../indicator-dashboard.component';

@Component({
	selector: 'app-indicator-dashboard-tab',
	templateUrl: './indicator-dashboard-tab.component.html',
	styleUrls: ['./indicator-dashboard-tab.component.scss']
})
export class IndicatorDashboardTabComponent extends BaseComponent implements OnInit {

	@Input() tabConfig: IndicatorDashboardTabConfig;
	@Input()
	indicatorQueryParams:IndicatorQueryParams;
	@Input()
	dashboardConfig : IndicatorDashboardConfig;

	constructor(
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService) {
		super();
	}


	ngOnInit(): void {

	}
}
