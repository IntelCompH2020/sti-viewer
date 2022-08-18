import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BlueprintRequest } from '@app/core/model/blueprint-request/blueprint-request.model';
import { BlueprintRequestLookup } from '@app/core/query/blueprint-request.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class DownloadReportService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/app/download-report`; }

	downloadReport(id: Guid): Observable<Blob> {
		const url = `${this.apiBase}/${id}`;

		return this.http
			.get<Blob>(url, { responseType: 'blob' }).pipe(
				catchError((error: any) => throwError(error)));
	}
}
