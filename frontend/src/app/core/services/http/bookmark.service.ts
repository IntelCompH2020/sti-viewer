import { Injectable } from '@angular/core';
import { BookmarkExistParams as GetBookmarkExistParams } from '@app/core/model/bookmark/bookmark-exists.mode';
import { Bookmark, MyBookmarkPersist } from '@app/core/model/bookmark/bookmark.model';
import { BookmarkLookup } from '@app/core/query/bookmark.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Guid } from '@common/types/guid';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class BookmarkService {
	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/bookmark`; }

	query(q: BookmarkLookup): Observable<QueryResult<Bookmark>> {
		const url = `${this.apiBase}/query/mine`;
		return this.http
			.post<QueryResult<Bookmark>>(url, q).pipe(
				catchError((error: any) => throwError(error)));
	}

    getSingle(id: Guid, reqFields: string[] = []): Observable<Bookmark> {
		const url = `${this.apiBase}/${id}/mine`;
		const options = { params: { f: reqFields } };
		
		return this.http
		.get<Bookmark>(url, options).pipe(
			catchError((error: any) => throwError(error)));
		}
		
		
	exists(params: GetBookmarkExistParams, f: string[]): Observable<Bookmark>{
		const url = `${this.apiBase}/get-by-hash/mine`;

		return this.http.post<Bookmark>(url, params, {params:{f}} ).pipe(
			catchError((error: any) => throwError(error)));;
	}
    persist(item: MyBookmarkPersist, f: string[] = []): Observable<Bookmark> {
		const url = `${this.apiBase}/persist/mine`;
		
		
		return this.http
			.post<Bookmark>(url, item, {params: {f}}).pipe(
				catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<void> {
		const url = `${this.apiBase}/${id}`;
		return this.http
			.delete<void>(url).pipe(
				catchError((error: any) => throwError(error)));
	}
}
