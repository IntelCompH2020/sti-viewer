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
import { TranslateService } from '@ngx-translate/core';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import * as FileSaver from 'file-saver';
import { QueryResult } from '@common/model/query-result';
import { Observable } from 'rxjs';
import { DataAccessRequest, DataAccessRequestConfig, DataAccessRequestIndicatorConfig } from '@app/core/model/data-access-request/data-access-request.model';
import { DataAccessRequestLookup } from '@app/core/query/data-access-request.lookup';
import { DataAccessRequestService } from '@app/core/services/http/data-access-request.service';
import { User } from '@app/core/model/user/user.model';
import { Indicator } from '@app/core/model/indicator/indicator.model';

@Component({
	templateUrl: './data-access-request-listing.component.html',
	styleUrls: ['./data-access-request-listing.component.scss']
})
export class DataAccessRequestListingComponent extends BaseListingComponent<DataAccessRequest, DataAccessRequestLookup> implements OnInit {

	publish = false;
	userSettingsKey = { key: 'DataAccessRequestListingUserSettings' };
	propertiesAvailableForOrder: ColumnDefinition[];

	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		protected language: TranslateService,
		private dataAccessRequestService: DataAccessRequestService,
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

	protected initializeLookup(): DataAccessRequestLookup {
		const lookup = new DataAccessRequestLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.order = { items: [this.toDescSortField(nameof<DataAccessRequest>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<DataAccessRequest>(x => x.id),
				nameof<DataAccessRequest>(x => x.status),
				nameof<DataAccessRequest>(x => x.updatedAt),
				nameof<DataAccessRequest>(x => x.createdAt),
				nameof<DataAccessRequest>(x => x.hash),
				nameof<DataAccessRequest>(x => x.user),
				[nameof<DataAccessRequest>(x => x.user), nameof<User>(x => x.firstName)].join('.'),
				[nameof<DataAccessRequest>(x => x.user), nameof<User>(x => x.lastName)].join('.'),
				[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.indicator), nameof<Indicator>(x => x.name)].join('.'),
			]
		};

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[{
			prop: nameof<DataAccessRequest>(x => x.createdAt),
			sortable: true,
			languageName: 'APP.DATA-ACCESS-REQUEST-LISTING.FIELDS.CREATED-AT',
			pipe: this.pipeService.getPipe<DataTableDateTimeFormatPipe>(DataTableDateTimeFormatPipe).withFormat('short')
		},
		{
			prop: nameof<DataAccessRequest>(x => x.status),
			sortable: true,
			languageName: 'APP.DATA-ACCESS-REQUEST-LISTING.FIELDS.STATUS'
		},
		{
			prop: nameof<DataAccessRequest>(x => x.user.firstName),
			sortable: true,
			languageName: 'APP.DATA-ACCESS-REQUEST-LISTING.FIELDS.FIRST-NAME'
		},
		{
			prop: nameof<DataAccessRequest>(x => x.user.lastName),
			sortable: true,
			languageName: 'APP.DATA-ACCESS-REQUEST-LISTING.FIELDS.LAST-NAME'
		},
		{
			alwaysShown: true,
			sortable: false,
			languageName: 'APP.DATA-ACCESS-REQUEST-LISTING.FIELDS.INDICATORS',
			valueFunction: (item: DataAccessRequest) => item?.config?.indicators?.map(indicator => indicator?.indicator?.name)?.filter(x => x).join(', ')
		},
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
				nameof<DataAccessRequest>(x => x.id),
				...columns
			]
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<DataAccessRequest>> {
		return this.dataAccessRequestService.query(this.lookup);
	}

	public createBlueprintRequest() {
		this.dataAccessRequestService.generateFile(this.lookup)
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
		this.dataAccessRequestService.generateAndDownloadFile(this.lookup)
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
