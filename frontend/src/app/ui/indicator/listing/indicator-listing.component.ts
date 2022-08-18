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
import { Indicator } from '@app/core/model/indicator/indicator.model';
import { IndicatorLookup } from '@app/core/query/indicator.lookup';
import { IndicatorService } from '@app/core/services/http/indicator.service';
@Component({
	templateUrl: './indicator-listing.component.html',
	styleUrls: ['./indicator-listing.component.scss']
})
export class IndicatorListingComponent extends BaseListingComponent<Indicator, IndicatorLookup> implements OnInit {

	publish = false;
	userSettingsKey = { key: 'IndicatorListingUserSettings' };
	propertiesAvailableForOrder: ColumnDefinition[];

	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		protected language: TranslateService,
		private indicatorService: IndicatorService,
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

	protected initializeLookup(): IndicatorLookup {
		const lookup = new IndicatorLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.isActive = [IsActive.Active];
		lookup.order = { items: [this.toDescSortField(nameof<Indicator>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<Indicator>(x => x.id),
				nameof<Indicator>(x => x.code),
				nameof<Indicator>(x => x.name),
				nameof<Indicator>(x => x.description),
				nameof<Indicator>(x => x.updatedAt),
				nameof<Indicator>(x => x.createdAt),
				nameof<Indicator>(x => x.hash),
				nameof<Indicator>(x => x.isActive)
			]
		};

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[{
			prop: nameof<Indicator>(x => x.code),
			sortable: true,
			languageName: 'APP.INDICATOR-LISTING.FIELDS.CODE'
		}, {
			prop: nameof<Indicator>(x => x.name),
			languageName: 'APP.INDICATOR-LISTING.FIELDS.NAME'
		}, {
			prop: nameof<Indicator>(x => x.description),
			languageName: 'APP.INDICATOR-LISTING.FIELDS.DESCRIPTION'
		}, {
			prop: nameof<Indicator>(x => x.createdAt),
			sortable: true,
			languageName: 'APP.INDICATOR-LISTING.FIELDS.CREATED-AT',
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
				nameof<Indicator>(x => x.id),
				...columns
			]
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<Indicator>> {
		return this.indicatorService.query(this.lookup);
	}

	public createBlueprintRequest() {
		this.indicatorService.generateFile(this.lookup)
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
		this.indicatorService.generateAndDownloadFile(this.lookup)
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
