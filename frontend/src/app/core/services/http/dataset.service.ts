import { Injectable } from '@angular/core';
import { Dataset, DatasetPersist } from '@app/core/model/dataset/dataset.model';
import { DatasetLookup } from '@app/core/query/dataset.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { BaseHttpParams } from '@common/http/base-http-params';

@Injectable()
export class DatasetService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/dataset`; }

	query(q: DatasetLookup): Observable<QueryResult<Dataset>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<Dataset>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<Dataset> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<Dataset>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persist(item: DatasetPersist): Observable<Dataset> {
		const url = `${this.apiBase}/persist`;
		
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
			.post<Dataset>(url, item, {
				params: params
			}).pipe(
				catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<Dataset> {
		const url = `${this.apiBase}/${id}`;
		return this.http
			.delete<Dataset>(url).pipe(
				catchError((error: any) => throwError(error)));
	}

	generateFile(q: DatasetLookup): Observable<Boolean> {
		const url = `${this.apiBase}/generate-report`;
		return this.http
			.post<Boolean>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	generateAndDownloadFile(q: DatasetLookup): Observable<Blob> {
		const url = `${this.apiBase}/generate-and-download-report`;

		return this.http
			.post<Blob>(url, q, { responseType: 'blob' }).pipe(
				catchError((error: any) => throwError(error)));
	}
}
