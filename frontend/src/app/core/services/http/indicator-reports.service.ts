import { Injectable } from '@angular/core';
import { IndicatorConfig } from '@app/core/model/indicator-config/indicator-config.model';
import { IndicatorReportlevelConfig } from '@app/core/model/indicator-config/indicator-report-level-config';
import { IndicatorReportLevelLookup } from '@app/core/query/indicator-report-level-lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

/**
 * @deprecated Use DataTreeService instead
 */
@Injectable({
  providedIn: 'root'
})
export class IndicatorReportsService {
  private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/indicator/report`; }

  constructor(private http: BaseHttpService,
    private installationConfiguration: InstallationConfigurationService) { }

  query(q: IndicatorReportLevelLookup): Observable<QueryResult<IndicatorConfig>> {
    const url = `${this.apiBase}/query`;
    return this.http
      .post<QueryResult<IndicatorConfig>>(url, q).pipe(
        catchError((error: any) => { console.log(error); return throwError(error); }));
  }
  queryLevel(q: IndicatorReportLevelLookup): Observable<QueryResult<IndicatorReportlevelConfig>> {
    const url = `${this.apiBase}/query/level`;
    return this.http
      .post<QueryResult<IndicatorReportlevelConfig>>(url, q).pipe(
        catchError((error: any) => { console.log(error); return throwError(error); }));
  }

}
