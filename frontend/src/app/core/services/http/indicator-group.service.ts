import { Injectable } from '@angular/core';
import { IndicatorGroup } from '@app/core/model/indicator-group/indicator-group.model';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { Guid } from '@common/types/guid';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
@Injectable()
export class IndicatorGroupService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/indicator-group`; }


    public getAll(fields: string[]): Observable<IndicatorGroup[]>{
        const url = `${this.apiBase}/get-all`;

        return this.http.get<IndicatorGroup[]>(url, {params: {f: fields}})
            .pipe(
                catchError((error: any) => throwError(error))
            );
    }


    public getById(id: Guid, fields: string[]): Observable<IndicatorGroup>{
        if(!id){
            console.warn('no id provided for IndicatorGroup');
            return of(null);
        }
        const url = `${this.apiBase}/${id}`;

        return this.http.get<IndicatorGroup>(url, {params: {f: fields}})
            .pipe(
                catchError((error: any) => throwError(error))
        );
    }

    public getByCode(code: string, fields: string[]): Observable<IndicatorGroup>{
        if(!code){
            console.warn('no code provided for IndicatorGroup');
            return of(null);
        }
        const url = `${this.apiBase}/by-code/${code}`;

        return this.http.get<IndicatorGroup>(url, {params: {f: fields}})
            .pipe(
                catchError((error: any) => throwError(error))
        );
    }
}
