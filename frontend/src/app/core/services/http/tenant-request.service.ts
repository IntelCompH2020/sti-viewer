import { Injectable } from '@angular/core';
import { TenantRequest, TenantRequestPersist, TenantRequestStatusPersist } from '@app/core/model/tenant-request/tenant.request.model';
import { TenantRequestLookup } from '@app/core/query/tenant-request.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { BaseHttpParams } from '@common/http/base-http-params';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class TenantRequestService {

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/tenant-request`; }

	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService) { }

	query(q: TenantRequestLookup): Observable<QueryResult<TenantRequest>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<TenantRequest>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<TenantRequest> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<TenantRequest>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persist(item: TenantRequestPersist): Observable<TenantRequest> {
		const url = `${this.apiBase}/persist`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			interceptorParams: [
				{
					type: InterceptorType.ErrorHandlerInterceptor,
					overrideErrorCodes: [101],
					serviceEndpoints: [this.installationConfiguration.appServiceAddress]
				}
			]
		};

		return this.http
			.post<TenantRequest>(url, item, { params: params }).pipe(
				catchError((error: any) => throwError(error)));
	}

	status(item: TenantRequestStatusPersist): Observable<TenantRequest> {
		const url = `${this.apiBase}/status`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			interceptorParams: [
				{
					type: InterceptorType.ErrorHandlerInterceptor,
					overrideErrorCodes: [101],
					serviceEndpoints: [this.installationConfiguration.appServiceAddress]
				}
			]
		};

		return this.http
			.post<TenantRequest>(url, item, { params: params }).pipe(
				catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<TenantRequest> {
		const url = `${this.apiBase}/${id}`;

		return this.http
			.delete<TenantRequest>(url).pipe(
				catchError((error: any) => throwError(error)));
	}

	generateFile(q: TenantRequestLookup): Observable<Boolean> {
		const url = `${this.apiBase}/generate-report`;
		return this.http
			.post<Boolean>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	generateAndDownloadFile(q: TenantRequestLookup): Observable<Blob> {
		const url = `${this.apiBase}/generate-and-download-report`;

		return this.http
			.post<Blob>(url, q, { responseType: 'blob' }).pipe(
				catchError((error: any) => throwError(error)));
	}
}
