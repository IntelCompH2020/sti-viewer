import { HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AggregateResponseModel } from "@app/core/model/aggregate-response/aggregate-reponse.model";
import { IndicatorPoint } from "@app/core/model/indicator-point/indicator-point.model";
import { IndicatorPointReportLookup } from "@app/core/query/indicator-point-report.lookup";
import { IndicatorPointLookup } from "@app/core/query/indicator-point.lookup";
import { IndicatorPointDistinctLookup } from "@app/core/query/IndicatorPointDistinctLookup";
import { BaseHttpService } from "@common/base/base-http.service";
import { BaseHttpParams } from "@common/http/base-http-params";
import { InterceptorType } from "@common/http/interceptors/interceptor-type";
import { InstallationConfigurationService } from "@common/installation-configuration/installation-configuration.service";
import { ElasticValuesResponse } from "@common/model/elastic-values-response";
import { QueryResult } from "@common/model/query-result";
import { Guid } from "@common/types/guid";
import { Observable, of, throwError } from "rxjs";
import { catchError } from "rxjs/operators";

@Injectable()
export class IndicatorPointService {
    constructor(
        private installationConfiguration: InstallationConfigurationService,
        private http: BaseHttpService
    ) { }

    private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/indicator-point`; }



    public getIndicatorPointReport(id: Guid | string, lookup: IndicatorPointReportLookup, skipLoader?: boolean,): Observable<AggregateResponseModel> {
        let _id = id;

        if (_id === 'inherited') {
            return of({
                items: [],
                total: 0,
            })
        }

        const url = `${this.apiBase}/${_id}/report`;

        const params = new BaseHttpParams();
        if (skipLoader) {
            params.interceptorContext = {
                excludedInterceptors: [InterceptorType.ProgressIndication]
            };
        }

        return this.http.post<AggregateResponseModel>(url, lookup, { params }).pipe(
            catchError((error: any) => throwError(error)));;
    }


    public exportXlsx(id: Guid | string, lookup: IndicatorPointReportLookup, skipLoader?: boolean,): Observable<HttpResponse<Blob>> {
        const url = `${this.apiBase}/${id}/export-xlsx`;
        const params = new BaseHttpParams();
        if (skipLoader) {
            params.interceptorContext = {
                excludedInterceptors: [InterceptorType.ProgressIndication],
            };
        }
        return this.http.post<HttpResponse<Blob>>(url, lookup, { params, responseType: 'blob', observe: 'response'   }).pipe(
            catchError((error: any) => throwError(error)));;
    }

    public getIndicatorPointQueryDistinct(lookup: IndicatorPointDistinctLookup): Observable<ElasticValuesResponse<string>> {
        const url = `${this.apiBase}/query-distinct`;
        return this.http
            .post<ElasticValuesResponse<string>>(url, lookup).pipe(
                catchError((error: any) => throwError(error)));

    }

    public query(lookup: IndicatorPointLookup):Observable<QueryResult<Record<string, string>>>{
        const url = `${this.apiBase}/query`;

        return this.http.post<QueryResult<Record<string, string>>>(url, lookup).pipe(
            catchError((error: any) => throwError(error)));;
    }

    public getGlobalSearchConfig(key: string):Observable<string>{
        const url = `${this.apiBase}/global-search-config-by-key/${key}`;

        return this.http.get<string>(url).pipe(
            catchError((error: any) => throwError(error)));;
    }
}
