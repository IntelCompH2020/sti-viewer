import { Injectable } from '@angular/core';
import { IndicatorGroup } from '@app/core/model/indicator-group/indicator-group.model';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { Observable, throwError } from 'rxjs';
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
}
