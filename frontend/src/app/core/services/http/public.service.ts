import { Injectable } from '@angular/core';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { BaseHttpParams } from '@common/http/base-http-params';
import { DashboardLookup, PublicIndicatorPointReportLookup } from '@app/core/model/shared/dashboard-lookup.model';
import { IndicatorDashboardConfig } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { AggregateResponseModel } from '@app/core/model/aggregate-response/aggregate-reponse.model';

@Injectable()
export class PublicService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/public`; }

	reportPublic(item: PublicIndicatorPointReportLookup): Observable<AggregateResponseModel> {
		const url = `${this.apiBase}/indicator-point/report`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			excludedInterceptors: [
				InterceptorType.ApiKeyInterceptor,
				InterceptorType.AuthToken,
				InterceptorType.UserConsentInterceptor,
				InterceptorType.TenantHeaderInterceptor,
			]
		};

		return this.http
			.post<AggregateResponseModel>(url, item, {
				params: params,
			}).pipe(
				catchError((error: any) => throwError(error)));
	}

	dashboardLookup(item: DashboardLookup): Observable<IndicatorDashboardConfig> {
		const url = `${this.apiBase}/dashboard/lookup`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			excludedInterceptors: [
				InterceptorType.ApiKeyInterceptor,
				InterceptorType.AuthToken,
				InterceptorType.UserConsentInterceptor,
				InterceptorType.TenantHeaderInterceptor,
			]
		};

		return this.http
			.post<IndicatorDashboardConfig>(url, item, {
				params: params,
			}).pipe(
				catchError((error: any) => throwError(error)));
	}
}
