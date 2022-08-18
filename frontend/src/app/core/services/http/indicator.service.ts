import { Injectable } from '@angular/core';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { BaseHttpParams } from '@common/http/base-http-params';
import { IndicatorLookup } from '@app/core/query/indicator.lookup';
import { Indicator, IndicatorPersist } from '@app/core/model/indicator/indicator.model';
import { IndicatorElasticLookup } from '@app/core/query/indicator-elastic.lookup';
import { IndicatorElastic } from '@app/core/model/indicator-elastic/indicator-elastic';

@Injectable()
export class IndicatorService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/indicator`; }

	query(q: IndicatorLookup): Observable<QueryResult<Indicator>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<Indicator>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}
	queryElastic(q: IndicatorElasticLookup): Observable<QueryResult<IndicatorElastic>> {
		const url = `${this.apiBase}/query/es`;
		return this.http
			.post<QueryResult<IndicatorElastic>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<Indicator> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<Indicator>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persist(item: IndicatorPersist): Observable<Indicator> {
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
			.post<Indicator>(url, item, {
				params: params
			}).pipe(
				catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<Indicator> {
		const url = `${this.apiBase}/${id}`;
		return this.http
			.delete<Indicator>(url).pipe(
				catchError((error: any) => throwError(error)));
	}

	generateFile(q: IndicatorLookup): Observable<Boolean> {
		const url = `${this.apiBase}/generate-report`;
		return this.http
			.post<Boolean>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	generateAndDownloadFile(q: IndicatorLookup): Observable<Blob> {
		const url = `${this.apiBase}/generate-and-download-report`;

		return this.http
			.post<Blob>(url, q, { responseType: 'blob' }).pipe(
				catchError((error: any) => throwError(error)));
	}
}
