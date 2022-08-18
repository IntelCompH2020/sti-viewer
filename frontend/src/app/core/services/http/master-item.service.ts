import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MasterItem, MasterItemPersist } from '@app/core/model/master-item/master-item.model';
import { MasterItemLookup } from '@app/core/query/master-item.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class MasterItemService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/master-item`; }

	query(q: MasterItemLookup): Observable<QueryResult<MasterItem>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<MasterItem>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<MasterItem> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<MasterItem>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persist(item: MasterItemPersist): Observable<MasterItem> {
		const url = `${this.apiBase}/persist`;

		return this.http
			.post<MasterItem>(url, item).pipe(
				catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<MasterItem> {
		const url = `${this.apiBase}/${id}`;

		let headers = new HttpHeaders();

		return this.http
			.delete<MasterItem>(url, headers ? { headers: headers } : undefined).pipe(
				catchError((error: any) => throwError(error)));
	}
}
