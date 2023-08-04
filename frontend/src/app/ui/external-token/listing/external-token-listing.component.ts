import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
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
import { ExternalTokenLookup } from '@app/core/query/external-token.lookup';
import { ExternalTokenService } from '@app/core/services/http/external-token.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { ExternalToken } from '@app/core/model/external-token/external-token.model';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { TokenExpirationDialogComponent } from '../token-expiration-dialog/token-expiration-dialog.component';
import { TokenChangeDialogComponent } from '../token-change-dialog/token-change-dialog.component';
@Component({
	templateUrl: './external-token-listing.component.html',
	styleUrls: ['./external-token-listing.component.scss']
})
export class ExternalTokenListingComponent extends BaseListingComponent<ExternalToken, ExternalTokenLookup> implements OnInit {

	@ViewChild('editActionColumnTemplate', { static: true }) editActionColumnTemplate: TemplateRef<any>;

	publish = false;
	userSettingsKey = { key: 'ExternalTokenListingUserSettings' };
	propertiesAvailableForOrder: ColumnDefinition[];

	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		protected language: TranslateService,
		private externalTokenService: ExternalTokenService,
		public authService: AuthService,
		private pipeService: PipeService,
		public enumUtils: AppEnumUtils,
		protected dialog: MatDialog,
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

	protected initializeLookup(): ExternalTokenLookup {
		const lookup = new ExternalTokenLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.isActive = [IsActive.Active];
		lookup.order = { items: [this.toDescSortField(nameof<ExternalToken>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: [
				nameof<ExternalToken>(x => x.id),
				nameof<ExternalToken>(x => x.name),
				nameof<ExternalToken>(x => x.expiresAt),
				nameof<ExternalToken>(x => x.createdAt),
				nameof<ExternalToken>(x => x.hash),
				nameof<ExternalToken>(x => x.isActive)
			]
		};

		if (this.authService.hasPermission(AppPermission.BrowseUser)) {
			lookup.project.fields.push(...[
				nameof<ExternalToken>(x => x.owner.firstName),
				nameof<ExternalToken>(x => x.owner.lastName),
				nameof<ExternalToken>(x => x.owner.subjectId),
			]);
		}

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[{
			prop: nameof<ExternalToken>(x => x.name),
			sortable: true,
			languageName: 'APP.EXTERNAL-TOKEN-LISTING.FIELDS.NAME'
		}, {
			prop: nameof<ExternalToken>(x => x.expiresAt),
			languageName: 'APP.EXTERNAL-TOKEN-LISTING.FIELDS.EXPIRES-AT',
			sortable: true,
			pipe: this.pipeService.getPipe<DataTableDateTimeFormatPipe>(DataTableDateTimeFormatPipe).withFormat('short')
		}, {
			prop: nameof<ExternalToken>(x => x.createdAt),
			sortable: true,
			languageName: 'APP.EXTERNAL-TOKEN-LISTING.FIELDS.CREATED-AT',
			pipe: this.pipeService.getPipe<DataTableDateTimeFormatPipe>(DataTableDateTimeFormatPipe).withFormat('short')
			}]);
		if (this.authService.hasPermission(AppPermission.BrowseUser)) {
			this.gridColumns.push(...[{
				prop: nameof<ExternalToken>(x => x.owner.subjectId),
				sortable: true,
				languageName: 'APP.EXTERNAL-TOKEN-LISTING.FIELDS.SUBJECT-ID'
			}, {
				prop: nameof<ExternalToken>(x => x.owner.firstName),
				sortable: true,
				languageName: 'APP.EXTERNAL-TOKEN-LISTING.FIELDS.FIRST-NAME'
			}, {
				prop: nameof<ExternalToken>(x => x.owner.lastName),
				sortable: true,
				languageName: 'APP.EXTERNAL-TOKEN-LISTING.FIELDS.LAST-NAME'
			}]);
		}

		this.gridColumns.push(...[
			{
				cellTemplate: this.editActionColumnTemplate,
				alwaysShown: true,
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
				nameof<ExternalToken>(x => x.id),
				...columns
			]
		};
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<ExternalToken>> {
		return this.externalTokenService.query(this.lookup);
	}

	public delete(row: ExternalToken) {
		if (row.id) {
			const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
				maxWidth: '300px',
				data: {
					message: this.language.instant('COMMONS.CONFIRMATION-DIALOG.DELETE-ITEM'),
					confirmButton: this.language.instant('COMMONS.CONFIRMATION-DIALOG.ACTIONS.CONFIRM'),
					cancelButton: this.language.instant('COMMONS.CONFIRMATION-DIALOG.ACTIONS.CANCEL')
				}
			});
			dialogRef.afterClosed().pipe(takeUntil(this._destroyed)).subscribe(result => {
				if (result) {
					this.externalTokenService.delete(row.id).pipe(takeUntil(this._destroyed))
						.subscribe(
							complete => this.onCallbackSuccess(),
							error => this.onCallbackError(error)
						);
				}
			});
		}
	}

	public persistExpiration(row: ExternalToken) {
		if (row.id) {
			const dialogRef = this.dialog.open(TokenExpirationDialogComponent, {
				maxWidth: '300px',
				data: {
					id: row.id,
				}
			});
			dialogRef.afterClosed().pipe(takeUntil(this._destroyed)).subscribe(result => {
				if (result) {
					this.onCallbackSuccess()
				}
			});
		}
	}

	public tokenChange(row: ExternalToken) {
		if (row.id) {
			const dialogRef = this.dialog.open(TokenChangeDialogComponent, {
				maxWidth: '800px',
				data: {
					id: row.id,
				}
			});
			dialogRef.afterClosed().pipe(takeUntil(this._destroyed)).subscribe(result => {
				if (result) {
					this.onCallbackSuccess()
				}
			});
		}
	}

	onCallbackSuccess(data?: any): void {
		this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.SNACK-BAR.SUCCESSFUL-UPDATE'), SnackBarNotificationLevel.Success);
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}
}
