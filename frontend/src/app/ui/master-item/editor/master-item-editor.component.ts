import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute,Router } from '@angular/router';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { BaseEditor } from '@common/base/base-editor';
import { MasterItem } from '@app/core/model/master-item/master-item.model';
import { MasterItemService } from '@app/core/services/http/master-item.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { DetailItemEditorModel, MasterItemEditorModel } from '@app/ui/master-item/editor/master-item-editor.model';
import { FormService } from '@common/forms/form-service';
import { LoggingService } from '@common/logging/logging-service';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { FilterService } from '@common/modules/text-filter/filter-service';
import { Guid } from '@common/types/guid';
import { TranslateService } from '@ngx-translate/core';
import { map, takeUntil } from 'rxjs/operators';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { MasterItemEditorResolver } from '@app/ui/master-item/editor/master-item-editor.resolver';

@Component({
	selector: 'app-master-item-editor',
	templateUrl: './master-item-editor.component.html',
	styleUrls: ['./master-item-editor.component.scss']
})
export class MasterItemEditorComponent extends BaseEditor<MasterItemEditorModel, MasterItem> implements OnInit {

	isNew = true;
	isDeleted = false;
	formGroup: UntypedFormGroup = null;
	showInactiveDetails = false;

	constructor(
		// BaseFormEditor injected dependencies
		protected dialog: MatDialog,
		protected language: TranslateService,
		protected formService: FormService,
		protected router: Router,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected filterService: FilterService,
		protected datePipe: DatePipe,
		protected route: ActivatedRoute,
		protected queryParamsService: QueryParamsService,
		// Rest dependencies. Inject any other needed deps here:
		public authService: AuthService,
		public enumUtils: AppEnumUtils,
		private masterItemService: MasterItemService,
		private logger: LoggingService,
	) {
		super(dialog, language, formService, router, uiNotificationService, httpErrorHandlingService, filterService, datePipe, route, queryParamsService);
	}

	ngOnInit(): void {
		super.ngOnInit();
	}

	getItem(itemId: Guid, successFunction: (item: MasterItem) => void) {
		this.masterItemService.getSingle(itemId, MasterItemEditorResolver.lookupFields())
			.pipe(map(data => data as MasterItem), takeUntil(this._destroyed))
			.subscribe(
				data => successFunction(data),
				error => this.onCallbackError(error)
			);
	}

	prepareForm(data: MasterItem) {
		try {
			this.editorModel = data ? new MasterItemEditorModel().fromModel(data) : new MasterItemEditorModel();
			this.isDeleted = data ? data.isActive === IsActive.Inactive : false;
			this.buildForm();
		} catch {
			this.logger.error('Could not parse Master Item: ' + data);
			this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.ERRORS.DEFAULT'), SnackBarNotificationLevel.Error);
		}
	}

	buildForm() {
		this.formGroup = this.editorModel.buildForm(null, this.isDeleted || !this.authService.hasPermission(AppPermission.EditMasterItem));
	}

	refreshData(): void {
		this.getItem(this.editorModel.id, (data: MasterItem) => this.prepareForm(data));
	}

	refreshOnNavigateToData(id?: Guid): void {
		if (this.isNew) {
			this.formGroup.markAsPristine();
			this.router.navigate(['/master-items/' + (id ? id : this.editorModel.id)], { queryParams: { 'lookup': this.queryParamsService.serializeLookup(this.lookupParams), 'lv': ++this.lv }, replaceUrl: true });
		} else { this.internalRefreshData(); }
	}

	persistEntity(onSuccess?: (response) => void): void {
		const formData = this.formService.getValue(this.formGroup.value);

		// Transformations
		if (Array.isArray(formData.details)) {
			formData.details.forEach(element => {
				if (element.dataset && element.dataset.id > 0) {
					element.datasetId = element.dataset.id;
				} else {
					element.datasetId = undefined;
				}
			});
		} else {
			formData.details = [];
		}

		this.masterItemService.persist(formData)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => onSuccess ? onSuccess(complete) : this.onCallbackSuccess(complete),
				error => this.onCallbackError(error)
			);
	}

	formSubmit(): void {
		this.formService.touchAllFormFields(this.formGroup);
		if (!this.isFormValid()) {
			return;
		}

		this.persistEntity();
	}

	public delete() {
		const value = this.formGroup.value;
		if (value.id) {
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
						this.masterItemService.delete(value.id).pipe(takeUntil(this._destroyed))
							.subscribe(
								complete => this.onCallbackSuccess(),
								error => this.onCallbackError(error)
							);
				}
			});
		}
	}

	clearErrorModel() {
		this.editorModel.validationErrorModel.clear();
		this.formService.validateAllFormFields(this.formGroup);
	}

	addDetailItem() {
		const detailsArray: UntypedFormArray = this.formGroup.get('details') as UntypedFormArray;
		detailsArray.push(this.editorModel.buildDetailForm(new DetailItemEditorModel(), detailsArray.length, false));
	}

	removeItemAt(index: number) {
		const detailsArray: UntypedFormArray = this.formGroup.get('details') as UntypedFormArray;
		const inactiveDetailsArray: UntypedFormArray = this.formGroup.get('inactiveDetails') as UntypedFormArray;
		const itemToBeDeleted = detailsArray.controls[index];
		itemToBeDeleted.disable();
		inactiveDetailsArray.push(itemToBeDeleted);
		detailsArray.controls.splice(index, 1);
		this.clearErrorModel();
		this.editorModel.helperReapplyValidators(detailsArray);
		this.formGroup.updateValueAndValidity();
	}
}
