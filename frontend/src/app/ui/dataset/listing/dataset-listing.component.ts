import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { Dataset } from '@app/core/model/dataset/dataset.model';
import { DatasetLookup } from '@app/core/query/dataset.lookup';
import { DatasetService } from '@app/core/services/http/dataset.service';
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
@Component({
	templateUrl: './dataset-listing.component.html',
	styleUrls: ['./dataset-listing.component.scss']
})
export class DatasetListingComponent extends BaseListingComponent<Dataset, DatasetLookup> implements OnInit {

	publish = false;
	userSettingsKey = { key: 'DatasetListingUserSettings' };
	propertiesAvailableForOrder: ColumnDefinition[];

	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		protected language: TranslateService,
		private datasetService: DatasetService,
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

	protected initializeLookup(): DatasetLookup {
		const lookup = new DatasetLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.isActive = [IsActive.Active];
		lookup.order = { items: [this.toDescSortField(nameof<Dataset>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<Dataset>(x => x.id),
				nameof<Dataset>(x => x.title),
				nameof<Dataset>(x => x.notes),
				nameof<Dataset>(x => x.updatedAt),
				nameof<Dataset>(x => x.createdAt),
				nameof<Dataset>(x => x.hash),
				nameof<Dataset>(x => x.isActive)
			]
		};

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[{
			prop: nameof<Dataset>(x => x.title),
			sortable: true,
			languageName: 'APP.DATASET-LISTING.FIELDS.TITLE'
		}, {
			prop: nameof<Dataset>(x => x.notes),
			languageName: 'APP.DATASET-LISTING.FIELDS.NOTES'
		}, {
			prop: nameof<Dataset>(x => x.createdAt),
			sortable: true,
			languageName: 'APP.DATASET-LISTING.FIELDS.CREATED-AT',
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
				nameof<Dataset>(x => x.id),
				...columns
			]
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<Dataset>> {
		return this.datasetService.query(this.lookup);
	}

	public createBlueprintRequest() {
		this.datasetService.generateFile(this.lookup)
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
		this.datasetService.generateAndDownloadFile(this.lookup)
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
