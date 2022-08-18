import { Injectable } from '@angular/core';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DataGroupRequestLookup } from '@app/core/query/data-group-request.lookup';
import { DataGroupRequest, DataGroupRequestPersist } from '@app/core/model/data-group-request/data-group-request.model';
import { Guid } from '@common/types/guid';

@Injectable()
export class DataGroupRequestService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/data-group-request`; }

	query(q: DataGroupRequestLookup): Observable<QueryResult<DataGroupRequest>> {
		const url = `${this.apiBase}/query`;
		return this.http
			.post<QueryResult<DataGroupRequest>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}


	persist(item: DataGroupRequestPersist,f: string[] = []): Observable<DataGroupRequest> {
		const url = `${this.apiBase}/persist`;
		
		return this.http
		.post<DataGroupRequest>(url, item, {params:{f}}).pipe(
			catchError((error: any) => throwError(error)));
	}
		
	delete(id: Guid): Observable<void>{
			
		const url = `${this.apiBase}/${id.toString()}`;
		return this.http
			.delete<void>(url).pipe(
				catchError((error: any) => throwError(error)));
	}
}
