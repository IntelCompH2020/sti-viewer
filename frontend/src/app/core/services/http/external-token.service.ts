import { Injectable } from '@angular/core';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { BaseHttpParams } from '@common/http/base-http-params';
import { ExternalTokenLookup } from '@app/core/query/external-token.lookup';
import { ExternalToken, ExternalTokenChangePersist, ExternalTokenCreateResponse, ExternalTokenExpirationPersist, IndicatorPointReportExternalTokenPersist } from '@app/core/model/external-token/external-token.model';

@Injectable()
export class ExternalTokenService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/external-token`; }

	query(q: ExternalTokenLookup): Observable<QueryResult<ExternalToken>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<ExternalToken>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<ExternalToken> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<ExternalToken>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persistExpiration(item: ExternalTokenExpirationPersist): Observable<ExternalTokenCreateResponse> {
		const url = `${this.apiBase}/persist-expiration`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			interceptorParams:[
				{
					type: InterceptorType.ErrorHandlerInterceptor,
					overrideErrorCodes: [101],
					serviceEndpoints:[this.installationConfiguration.appServiceAddress]
				}
			]
		};

		return this.http
			.post<ExternalTokenCreateResponse>(url, item, {
				params: params
			}).pipe(
				catchError((error: any) => throwError(error)));
	}

	persistTokenChange(item: ExternalTokenChangePersist): Observable<string> {
		const url = `${this.apiBase}/token-change`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			interceptorParams:[
				{
					type: InterceptorType.ErrorHandlerInterceptor,
					overrideErrorCodes: [101],
					serviceEndpoints:[this.installationConfiguration.appServiceAddress]
				}
			]
		};

		return this.http
			.post<string>(url, item, {
				params: params, responseType: 'text'
			}).pipe(
				catchError((error: any) => throwError(error)));
	}

	persistIndicatorPointReport(item: IndicatorPointReportExternalTokenPersist): Observable<ExternalTokenCreateResponse> {
		const url = `${this.apiBase}/persist/for-indicator-point-report`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			interceptorParams:[
				{
					type: InterceptorType.ErrorHandlerInterceptor,
					overrideErrorCodes: [101],
					serviceEndpoints:[this.installationConfiguration.appServiceAddress]
				}
			]
		};

		return this.http
			.post<ExternalTokenCreateResponse>(url, item, {
				params: params
			}).pipe(
				catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<ExternalToken> {
		const url = `${this.apiBase}/${id}`;
		return this.http
			.delete<ExternalToken>(url).pipe(
				catchError((error: any) => throwError(error)));
	}
}
