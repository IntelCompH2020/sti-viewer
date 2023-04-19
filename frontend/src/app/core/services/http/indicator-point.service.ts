import { HttpResponse } from "@angular/common/http";
import { Inject, Injectable, OnDestroy, Optional } from "@angular/core";
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
import { catchError, map, tap } from "rxjs/operators";
import { USE_CACHE } from "../tokens/caching.token";

@Injectable()
export class IndicatorPointService implements OnDestroy {
    constructor(
        private installationConfiguration: InstallationConfigurationService,
        private http: BaseHttpService,
        @Optional()
        @Inject(USE_CACHE) 
        private useCaching: boolean
    ) {
        if(useCaching){
            this._cache = new ServiceCache();
        }
     }


    ngOnDestroy(): void {
        this._cache?.cleanCache();
    }

    private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/indicator-point`; }

    private _cache: ServiceCache;


    public getIndicatorPointReport(id: Guid | string, lookup: IndicatorPointReportLookup, skipLoader?: boolean,): Observable<AggregateResponseModel> {

        if(this.useCaching){
            const response = this._cache?.getItem(id.toString(), lookup);
            if(response){
                return of(response);
            }
        }

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

        return this.http.post<AggregateResponseModel>(url, lookup, { params })
        .pipe(
            tap((response) =>{
                if(this.useCaching){
                    this._cache?.cacheResponse(id.toString(), lookup, response )
                }
            } ),
            catchError((error: any) => throwError(error))
        );
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
    public exportJSON(id: Guid | string, lookup: IndicatorPointReportLookup, skipLoader?: boolean,): Observable<HttpResponse<Blob>> {
        const url = `${this.apiBase}/${id}/export-json`;
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



class ServiceCache {
    private _cache = new Map<string, Map<string, CacheRecord>>();
    private _routine;

    private cacheTime = 3 * 10 * 1000; //3 mins



    constructor(){
        this._routine = setInterval(() =>{

            for(let value of this._cache.values()){
                const invalidRecords: string[] = [];
                for(let [lookup, record] of value){
                    if(Date.now() - record.time > this.cacheTime){
                        invalidRecords.push(lookup);
                    }
                }
                
                invalidRecords.forEach(record =>{
                    value.delete(record);
                })

            }


        }, 2* 60 * 1000); // every two minutes 
    }

    public cacheResponse(id: string, lookup: IndicatorPointReportLookup, response: any): void{
        try {
            let idRecord = this._cache.get(id);

            if(!idRecord){
                idRecord = this._cache.set(id, new Map<string, CacheRecord>()).get(id);
            }

            idRecord.set(
                JSON.stringify(lookup),
                 {
                            value: JSON.stringify(response),
                            time: Date.now()
                        });

        } catch (error) {
            
        }
    }

    public getItem(id: string, lookup: IndicatorPointReportLookup): any{
        try{
            const record = this._cache.get(id)?.get(JSON.stringify(lookup));

            if(!record){
                return null;
            }

            if( (Date.now() - record.time ) > this.cacheTime ){ // invalid
                this._cache.get(id).delete(JSON.stringify(lookup));
                return null;
            }
            
            return JSON.parse(record.value);
        }catch{
            return null;
        }

    }


    public cleanCache(): void{
        clearInterval(this._routine);
    }
}

interface CacheRecord{
    time: number;
    value: string;
}