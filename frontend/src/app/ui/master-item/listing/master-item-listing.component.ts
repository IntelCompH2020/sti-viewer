import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { MasterItem } from '@app/core/model/master-item/master-item.model';
import { MasterItemLookup } from '@app/core/query/master-item.lookup';
import { MasterItemService } from '@app/core/services/http/master-item.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseListingComponent } from '@common/base/base-listing-component';
import { PipeService } from '@common/formatting/pipe.service';
import { DataTableDateTimeFormatPipe } from '@common/formatting/pipes/date-time-format.pipe';
import { QueryResult } from '@common/model/query-result';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { ColumnDefinition, ColumnsChangedEvent, PageLoadEvent } from '@common/modules/listing/listing.component';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { IsActive } from '@idp-service/core/enum/is-active.enum';
import { Observable } from 'rxjs';
import { nameof } from 'ts-simple-nameof';

@Component({
	templateUrl: './master-item-listing.component.html',
	styleUrls: ['./master-item-listing.component.scss']
})
export class MasterItemListingComponent extends BaseListingComponent<MasterItem, MasterItemLookup> implements OnInit {
	publish = false;
	userSettingsKey = { key: 'MasterItemListingUserSettingss' };
	propertiesAvailableForOrder: ColumnDefinition[];
	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		private masterItemService: MasterItemService,
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

	protected initializeLookup(): MasterItemLookup {
		const lookup = new MasterItemLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.isActive = [IsActive.Active];
		lookup.order = { items: [this.toDescSortField(nameof<MasterItem>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<MasterItem>(x => x.id),
				nameof<MasterItem>(x => x.title),
				nameof<MasterItem>(x => x.notes),
				nameof<MasterItem>(x => x.updatedAt),
				nameof<MasterItem>(x => x.createdAt),
				nameof<MasterItem>(x => x.hash),
				nameof<MasterItem>(x => x.isActive)
			]
		};

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[{
			prop: nameof<MasterItem>(x => x.title),
			sortable: true,
			languageName: 'APP.MASTER-ITEM-LISTING.FIELDS.TITLE'
		}, {
			prop: nameof<MasterItem>(x => x.notes),
			languageName: 'APP.MASTER-ITEM-LISTING.FIELDS.NOTES'
		}, {
			prop: nameof<MasterItem>(x => x.createdAt),
			sortable: true,
			languageName: 'APP.MASTER-ITEM-LISTING.FIELDS.CREATED-AT',
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
				nameof<MasterItem>(x => x.id),
				...columns
			]
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<MasterItem>> {
		return this.masterItemService.query(this.lookup);
	}
}
