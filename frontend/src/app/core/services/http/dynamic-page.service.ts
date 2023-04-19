import { Injectable } from '@angular/core';
import { DynamicPage, DynamicPageMenuItem, DynamicPagePersist } from '@app/core/model/dynamic-page/dynamic-page.model';
import { DynamicPageLookup } from '@app/core/query/dynamic-page.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { BaseHttpParams } from '@common/http/base-http-params';
import { DynamicPageContentData, DynamicPageContentRequest } from '@app/core/model/dynamic-page/dynamic-page-content.model';

@Injectable()
export class DynamicPageService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) {}

	private get apiBase(): string {
		return `${this.installationConfiguration.appServiceAddress}api/dynamic-page`;
	}

	query(q: DynamicPageLookup): Observable<QueryResult<DynamicPage>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<DynamicPage>>(url, q)
			.pipe(catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<DynamicPage> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<DynamicPage>(url, options)
			.pipe(catchError((error: any) => throwError(error)));
	}

	getAllowedPageMenuItems(language: string, reqFields: string[] = []): Observable<DynamicPageMenuItem[]> {
		const url = `${this.apiBase}/allowed-menu/${language}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<DynamicPageMenuItem[]>(url, options)
			.pipe(catchError((error: any) => throwError(error)));
	}

	getPageContent(q: DynamicPageContentRequest): Observable<DynamicPageContentData> {
		const url = `${this.apiBase}/content`;

		return this.http
			.post<DynamicPageContentData>(url, q)
			.pipe(catchError((error: any) => throwError(error)));
	}

	persist(item: DynamicPagePersist): Observable<DynamicPage> {
		const url = `${this.apiBase}/persist`;

		const params = new BaseHttpParams();
		params.interceptorContext = {
			interceptorParams: [
				{
					type: InterceptorType.ErrorHandlerInterceptor,
					overrideErrorCodes: [101],
					serviceEndpoints: [
						this.installationConfiguration.appServiceAddress,
					],
				},
			],
		};

		return this.http
			.post<DynamicPage>(url, item, {
				params: params,
			})
			.pipe(catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<DynamicPage> {
		const url = `${this.apiBase}/${id}`;
		return this.http
			.delete<DynamicPage>(url)
			.pipe(catchError((error: any) => throwError(error)));
	}
}
