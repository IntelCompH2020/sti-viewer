import { Injectable } from '@angular/core';
import { IndicatorDashboardConfig } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { Observable } from 'rxjs';

@Injectable()
export class IndicatorDashboardService {

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/dashboard`; }

	constructor(
		private http: BaseHttpService,
		private installationConfiguration: InstallationConfigurationService
	) { }

	public getDashboard(dashboardKey: string): Observable<IndicatorDashboardConfig> {
		const url = `${this.apiBase}/by-key/${dashboardKey}`;

		// const params = new BaseHttpParams();
		// params.interceptorContext = {
		// 	excludedInterceptors: [InterceptorType.ProgressIndication]
		// };

		return this.http
			.get<IndicatorDashboardConfig>(url);
	}
}
