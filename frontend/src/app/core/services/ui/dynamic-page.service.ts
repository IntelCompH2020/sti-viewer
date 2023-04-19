
import { Injectable } from '@angular/core';
import { DynamicPageContentData } from '@app/core/model/dynamic-page/dynamic-page-content.model';
import { DynamicPageMenuItem } from '@app/core/model/dynamic-page/dynamic-page.model';
import { BaseService } from '@common/base/base.service';
import { Guid } from '@common/types/guid';
import { LanguageService } from '@user-service/services/language.service';
import { Observable, of, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { DynamicPageService } from '../http/dynamic-page.service';
import {  AuthService, LoginStatus } from './auth.service';


@Injectable()
export class DynamicPageProviderService extends BaseService {
	public menuItemsChangedSubject: Subject<DynamicPageMenuItem[]>;
	public serviceResetSubject: Subject<boolean>;
	menuItems: DynamicPageMenuItem[] = null;
	contents: DynamicPageContentData[] = null;

	constructor(
		private authService: AuthService,
		private languageService: LanguageService,
		private pageService: DynamicPageService
	) {
		super();
		this.menuItemsChangedSubject = new Subject<DynamicPageMenuItem[]>();
		this.serviceResetSubject = new Subject<boolean>();
		this.setupChangeListeners();
	}

	setupChangeListeners() {
		this.languageService
			.getLanguageChangeObservable()
			.pipe(takeUntil(this._destroyed))
			.subscribe((newLanguage) => {
				this.clear();
				if (this.authService.isLoggedIn()) {
					this.getAllowedPageMenuItems()
						.pipe(takeUntil(this._destroyed))
						.subscribe((x) => x);
				}
			});

		this.authService
			.getAuthenticationStateObservable()
			.pipe(takeUntil(this._destroyed))
			.subscribe((authenticationState) => {
				this.clear();
				if (authenticationState.loginStatus === LoginStatus.LoggedIn) {
					this.getAllowedPageMenuItems()
						.pipe(takeUntil(this._destroyed))
						.subscribe((x) => x);
				}
			});
	}

	public getAllowedPageMenuItems(): Observable<DynamicPageMenuItem[]> {
		if (this.menuItems !== null) {
			return of(this.menuItems);
		} else {
			return this.pageService
				.getAllowedPageMenuItems(
					this.languageService.getLanguageValue(
						this.languageService.getCurrentLanguage()
					)
				)
				.pipe(
					map((x) => {
						this.menuItems = x;
						this.menuItemsChangedSubject.next(this.menuItems);
						return this.menuItems;
					})
				);
		}
	}
	public getPageContent(pageId: Guid): Observable<DynamicPageContentData> {
		const pageContentData =
			this.contents != null
				? this.contents.find((x) => x.id == pageId)
				: null;
		if (pageContentData != null) {
			return of(pageContentData);
		} else {
			return this.pageService
				.getPageContent({
					id: pageId,
					language: this.languageService.getLanguageValue(
						this.languageService.getCurrentLanguage()
					),
				})
				.pipe(
					map((x) => {
						if (this.contents == null) this.contents = [];
						this.contents.push(x);
						return x;
					})
				);
		}
	}

	public getMenuItemsChangedObservable(): Observable<DynamicPageMenuItem[]> {
		return this.menuItemsChangedSubject.asObservable();
	}

	public getServiceResetObservable(): Observable<boolean> {
		return this.serviceResetSubject.asObservable();
	}

	public clear(): void {
		this.menuItems = null;
		this.contents = null;
		this.menuItemsChangedSubject.next([]);
		this.serviceResetSubject.next(true);
	}

	public refresh(): void {
		this.clear();
		this.getAllowedPageMenuItems()
						.pipe(takeUntil(this._destroyed))
						.subscribe((x) => x);
	}
}
