import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseListingComponent } from '@common/base/base-listing-component';
import { PipeService } from '@common/formatting/pipe.service';
import { DataTableDateTimeFormatPipe } from '@common/formatting/pipes/date-time-format.pipe';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { ColumnDefinition, ColumnsChangedEvent, PageLoadEvent } from '@common/modules/listing/listing.component';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { IsActive } from '@idp-service/core/enum/is-active.enum';
import { TranslateService } from '@ngx-translate/core';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import * as FileSaver from 'file-saver';
import { QueryResult } from '@common/model/query-result';
import { Observable } from 'rxjs';
import { TenantRequestLookup } from '@app/core/query/tenant-request.lookup';
import { TenantRequest } from '@app/core/model/tenant-request/tenant.request.model';
import { TenantRequestService } from '@app/core/services/http/tenant-request.service';
import { AppPermission } from '@app/core/enum/permission.enum';
@Component({
	templateUrl: './tenant-request-listing.component.html',
	styleUrls: ['./tenant-request-listing.component.scss']
})
export class TenantRequestListingComponent extends BaseListingComponent<TenantRequest, TenantRequestLookup> implements OnInit {

	publish = false;
	userSettingsKey = { key: 'TenantRequestListingUserSettings' };
	propertiesAvailableForOrder: ColumnDefinition[];

	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		protected language: TranslateService,
		private tenantRequestService: TenantRequestService,
		public authService: AuthService,
		private pipeService: PipeService,
		public enumUtils: AppEnumUtils,
		// private language: TranslateService,
		// private dialog: MatDialog
	) {
		super(router, route, uiNotificationService, httpErrorHandlingService, queryParamsService);
		// Lookup setup
		// Default lookup values are defined in the user settings class.
		this.lookup = this.initializeLookup();
	}

	ngOnInit() {
		super.ngOnInit();
	}

	protected initializeLookup(): TenantRequestLookup {
		const lookup = new TenantRequestLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.isActive = [IsActive.Active];
		lookup.order = { items: [this.toDescSortField(nameof<TenantRequest>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<TenantRequest>(x => x.id),
				nameof<TenantRequest>(x => x.message),
				nameof<TenantRequest>(x => x.status),
				nameof<TenantRequest>(x => x.email),
				nameof<TenantRequest>(x => x.updatedAt),
				nameof<TenantRequest>(x => x.createdAt),
				nameof<TenantRequest>(x => x.hash),
				nameof<TenantRequest>(x => x.isActive)
			]
		};

		if (this.authService.hasPermission(AppPermission.BrowseUser)) {
			lookup.project.fields.push(...[
				nameof<TenantRequest>(x => x.forUser.firstName),
				nameof<TenantRequest>(x => x.forUser.lastName),
				nameof<TenantRequest>(x => x.forUser.subjectId),
			]);
		}

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[{
			prop: nameof<TenantRequest>(x => x.message),
			languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.MESSAGE'
		}, {
			prop: nameof<TenantRequest>(x => x.status),
			languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.STATUS'
		},  {
			prop: nameof<TenantRequest>(x => x.email),
			sortable: true,
			languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.EMAIL'
		},  {
			prop: nameof<TenantRequest>(x => x.createdAt),
			sortable: true,
			languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.CREATED-AT',
			pipe: this.pipeService.getPipe<DataTableDateTimeFormatPipe>(DataTableDateTimeFormatPipe).withFormat('short')
			}]);
		if (this.authService.hasPermission(AppPermission.BrowseUser)) {
			this.gridColumns.push(...[{
				prop: nameof<TenantRequest>(x => x.forUser.subjectId),
				sortable: true,
				languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.SUBJECT-ID'
			}, {
				prop: nameof<TenantRequest>(x => x.forUser.firstName),
				sortable: true,
				languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.FIRST-NAME'
			}, {
				prop: nameof<TenantRequest>(x => x.forUser.lastName),
				sortable: true,
				languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.LAST-NAME'
			}]);
		}

		this.gridColumns.push(...[{
				prop: nameof<TenantRequest>(x => x.createdAt),
				sortable: true,
				languageName: 'APP.TENANT-REQUEST-LISTING.FIELDS.CREATED-AT',
				pipe: this.pipeService.getPipe<DataTableDateTimeFormatPipe>(DataTableDateTimeFormatPipe).withFormat('short')
			}]);

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
				nameof<TenantRequest>(x => x.id),
				...columns
			]
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<TenantRequest>> {
		return this.tenantRequestService.query(this.lookup);
	}

	public createBlueprintRequest() {
		this.tenantRequestService.generateFile(this.lookup)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => {
					if (complete) {
						this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.SNACK-BAR.SUCCESSFUL-CREATION'), SnackBarNotificationLevel.Success);
					}
				},
				error => this.onCallbackError(error)
			);
	}

	public createAndDownloadBlueprintRequest() {
		this.tenantRequestService.generateAndDownloadFile(this.lookup)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => {
					if (complete) {
						this.saveFile(complete);
					}
				},
				error => this.onCallbackError(error)
			);
	}

	private saveFile(data: any) {
		FileSaver.saveAs(data);
	}
}
