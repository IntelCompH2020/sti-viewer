import { Injectable } from '@angular/core';
import { Tenant, TenantPersist } from '@app/core/model/tenant/tenant.model';
import { UserSettings, UserSettingsPersist } from '@app/core/model/user/user-settings.model';
import { UserSettingsLookup } from '@app/core/query/user-settings.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { BaseHttpParams } from '@common/http/base-http-params';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class UserSettingsService {

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/user-settings`; }

	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService) { }

	query(q: UserSettingsLookup): Observable<QueryResult<UserSettings>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<UserSettings>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<UserSettings> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<UserSettings>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persist(item: UserSettingsPersist, f: string[] = []): Observable<UserSettings> {
		const url = `${this.apiBase}/persist`;
		return this.http
			.post<UserSettings>(url, item, { params: {f} }).pipe(
				catchError((error: any) => throwError(error)));
	}
}
