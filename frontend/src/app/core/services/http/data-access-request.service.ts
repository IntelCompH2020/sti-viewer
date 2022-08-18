import { Injectable } from '@angular/core';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { BaseHttpParams } from '@common/http/base-http-params';
import { DataAccessRequestLookup } from '@app/core/query/data-access-request.lookup';
import { DataAccessRequest, DataAccessRequestPersist, DataAccessRequestStatusPersist } from '@app/core/model/data-access-request/data-access-request.model';

@Injectable()
export class DataAccessRequestService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/data-access-request`; }

	query(q: DataAccessRequestLookup): Observable<QueryResult<DataAccessRequest>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<DataAccessRequest>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<DataAccessRequest> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<DataAccessRequest>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persist(item: DataAccessRequestPersist): Observable<DataAccessRequest> {
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
			.post<DataAccessRequest>(url, item, {
				params: params
			}).pipe(
				catchError((error: any) => throwError(error)));
	}

	status(item: DataAccessRequestStatusPersist): Observable<DataAccessRequest> {
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
			.post<DataAccessRequest>(url, item, { params: params }).pipe(
				catchError((error: any) => throwError(error)));
	}
	generateFile(q: DataAccessRequestLookup): Observable<Boolean> {
		const url = `${this.apiBase}/generate-report`;
		return this.http
			.post<Boolean>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	generateAndDownloadFile(q: DataAccessRequestLookup): Observable<Blob> {
		const url = `${this.apiBase}/generate-and-download-report`;

		return this.http
			.post<Blob>(url, q, { responseType: 'blob' }).pipe(
				catchError((error: any) => throwError(error)));
	}
}
