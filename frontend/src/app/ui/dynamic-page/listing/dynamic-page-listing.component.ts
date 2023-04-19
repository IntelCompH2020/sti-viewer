import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { DynamicPage } from "@app/core/model/dynamic-page/dynamic-page.model";
import { DynamicPageLookup } from "@app/core/query/dynamic-page.lookup";
import { DynamicPageService } from "@app/core/services/http/dynamic-page.service";
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
import { DynamicPageContent } from '@app/core/model/dynamic-page/dynamic-page-content.model';
@Component({
	templateUrl: "./dynamic-page-listing.component.html",
	styleUrls: ["./dynamic-page-listing.component.scss"],
})
export class DynamicPageListingComponent
	extends BaseListingComponent<DynamicPage, DynamicPageLookup>
	implements OnInit
{
	publish = false;
	userSettingsKey = { key: "DynamicPageListingUserSettings" };
	propertiesAvailableForOrder: ColumnDefinition[];

	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		protected language: TranslateService,
		private dynamicPageService: DynamicPageService,
		public authService: AuthService,
		private pipeService: PipeService,
		public enumUtils: AppEnumUtils // private language: TranslateService, // private dialog: MatDialog
	) {
		super(
			router,
			route,
			uiNotificationService,
			httpErrorHandlingService,
			queryParamsService
		);
		// Lookup setup
		// Default lookup values are defined in the user settings class.
		this.lookup = this.initializeLookup();
	}

	ngOnInit() {
		super.ngOnInit();
	}

	protected initializeLookup(): DynamicPageLookup {
		const lookup = new DynamicPageLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.isActive = [IsActive.Active];
		lookup.order = {
			items: [
				this.toDescSortField(nameof<DynamicPage>((x) => x.createdAt)),
			],
		};
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<DynamicPage>((x) => x.id),
				nameof<DynamicPage>((x) => x.pageContents) + "." + nameof<DynamicPageContent>((x) => x.title),
				nameof<DynamicPage>((x) => x.updatedAt),
				nameof<DynamicPage>((x) => x.createdAt),
				nameof<DynamicPage>((x) => x.hash),
				nameof<DynamicPage>((x) => x.isActive),
			],
		};

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(
			...[
				{
					prop: nameof<DynamicPage>((x) => x.pageContents) + "." + nameof<DynamicPageContent>((x) => x.title),
					sortable: false,
					languageName: "APP.DYNAMIC-PAGE-LISTING.FIELDS.TITLE",
					valueFunction: (item: DynamicPage) => this.getTitle(item),
				},
				{
					prop: nameof<DynamicPage>((x) => x.createdAt),
					sortable: true,
					languageName: "APP.DYNAMIC-PAGE-LISTING.FIELDS.CREATED-AT",
					pipe: this.pipeService
						.getPipe<DataTableDateTimeFormatPipe>(
							DataTableDateTimeFormatPipe
						)
						.withFormat("short"),
				},
			]
		);
		this.propertiesAvailableForOrder = this.gridColumns.filter(
			(x) => x.sortable
		);
	}

	private getTitle(page: DynamicPage): string {
		if (!page || !Array.isArray(page.pageContents)) {
			return null;
		}
		return page.pageContents.map(x=> x.title).join(', ');
	}
	//
	// Listing Component functions
	//
	onColumnsChanged(event: ColumnsChangedEvent) {
		this.onColumnsChangedInternal(
			event.properties.map((x) => x.toString())
		);
	}

	private onColumnsChangedInternal(columns: string[]) {
		// Here are defined the projection fields that always requested from the api.
		this.lookup.project = {
			fields: [nameof<DynamicPage>((x) => x.id), ...columns],
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<DynamicPage>> {
		return this.dynamicPageService.query(this.lookup);
	}

	private saveFile(data: any) {
		FileSaver.saveAs(data);
	}
}
