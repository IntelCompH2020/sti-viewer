import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseListingComponent } from '@common/base/base-listing-component';
import { PipeService } from '@common/formatting/pipe.service';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { ColumnDefinition, ColumnsChangedEvent, PageLoadEvent, RowActivateEvent } from '@common/modules/listing/listing.component';
import {UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { TranslateService } from '@ngx-translate/core';
import { nameof } from 'ts-simple-nameof';
import { QueryResult } from '@common/model/query-result';
import { Observable } from 'rxjs';
import { Indicator } from '@app/core/model/indicator/indicator.model';
import { UserSettingsService } from '@app/core/services/http/user-settings.service';
import { UserSettingsLookup } from '@app/core/query/user-settings.lookup';
import { UserSettings, UserSettingsType } from '@app/core/model/user/user-settings.model';
@Component({
	templateUrl: './dashboard-configuration-listing.component.html',
	styleUrls: ['./dashboard-configuration-listing.component.scss']
})
export class DashboardConfigurationListingComponent extends BaseListingComponent<UserSettings, UserSettingsLookup> implements OnInit {

	publish = false;
	userSettingsKey = { key: 'DashboardConfigurationUserSettings' };
	propertiesAvailableForOrder: ColumnDefinition[];

	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		protected language: TranslateService,
		public authService: AuthService,
		private pipeService: PipeService,
		public enumUtils: AppEnumUtils,
		private userSettingsService: UserSettingsService
	) {
		super(router, route, uiNotificationService, httpErrorHandlingService, queryParamsService);
		this.lookup = this.initializeLookup();
	}

	ngOnInit() {
		super.ngOnInit();
	}

	protected initializeLookup(): UserSettingsLookup {
		const lookup = new UserSettingsLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.order = { items: [this.toDescSortField(nameof<Indicator>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<UserSettings>(x => x.id),
				nameof<UserSettings>(x => x.type),
				nameof<UserSettings>(x => x.entityType),
				nameof<UserSettings>(x => x.key),
			]
		};

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[
			{
				prop: nameof<UserSettings>(x => x.key),
				languageName: 'APP.DASHBOARD-CONFIGURATION.LISTING-CONFIGURATION.KEY'
			},
			{
				prop: nameof<UserSettings>(x => x.type),
				languageName: 'APP.DASHBOARD-CONFIGURATION.LISTING-CONFIGURATION.TYPE'
			},
			{
				prop: nameof<UserSettings>(x => x.entityType),
				languageName: 'APP.DASHBOARD-CONFIGURATION.LISTING-CONFIGURATION.ENTITY-TYPE'
			}
		]);
		this.propertiesAvailableForOrder = this.gridColumns.filter(x => x.sortable);
	}

	//
	// Listing Component functions
	//
	onColumnsChanged(event: ColumnsChangedEvent) {
		this.onColumnsChangedInternal(event.properties.map(x => x.toString()));
	}

	private onColumnsChangedInternal(columns: string[]) {
		// Here are defined the projection fields that always requested from the api.
		this.lookup.project = {
			fields: [
				nameof<Indicator>(x => x.id),
				...columns
			]
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<UserSettings>> {
		return this.userSettingsService.query(this.lookup);
	}


	onRowActivated(event: RowActivateEvent) {
		if (event && event.type === 'click' && event.row && event.row.id) {
			if(event.row?.type === UserSettingsType.Dashboard){
				this.router.navigate(['dashboard',event.row.id], { relativeTo: this.route});
				return;
			}
			if(event.row?.type === UserSettingsType.BrowseDataTree){
				this.router.navigate(['browse-data-tree',event.row.id], { relativeTo: this.route});
				return;
			}
			if(event.row?.type === UserSettingsType.GlobalSearch){
				this.router.navigate(['search-configuration',event.row.id], { relativeTo: this.route});
				return;
			}
		}
	}


	protected navigateToNewDashboardConfig():void{
		this.router.navigate(['dashboard','new'], { relativeTo: this.route});
	}

	protected navigateToNewSearchConfig():void{
		this.router.navigate(['search-configuration','new'], { relativeTo: this.route});
	}
	protected navigateToBrowseTreeConfig():void{
		this.router.navigate(['browse-data-tree','new'], { relativeTo: this.route});
	}
}
