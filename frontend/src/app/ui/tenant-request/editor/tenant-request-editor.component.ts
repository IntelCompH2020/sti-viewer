import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { FormService } from '@common/forms/form-service';
import { LoggingService } from '@common/logging/logging-service';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { Guid } from '@common/types/guid';
import { TranslateService } from '@ngx-translate/core';
import { map, takeUntil } from 'rxjs/operators';
import { BaseEditor } from '@common/base/base-editor';
import { FilterService } from '@common/modules/text-filter/filter-service';
import { DatePipe } from '@angular/common';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { TenantRequestEditorResolver } from './tenant-request-editor.resolver';
import { TenantRequestEditorModel } from './tenant-request-editor.model';
import { TenantRequestService } from '@app/core/services/http/tenant-request.service';
import { TenantRequest, TenantRequestStatusPersist } from '@app/core/model/tenant-request/tenant.request.model';
import { TenantRequestStatus } from '@app/core/enum/teanant-request.enum';
import { ApproveDialogComponent } from './approve-dialog/approve-dialog.component';


@Component({
	selector: 'app-tenant-request-editor',
	templateUrl: './tenant-request-editor.component.html',
	styleUrls: ['./tenant-request-editor.component.scss']
})
export class TenantRequestEditorComponent extends BaseEditor<TenantRequestEditorModel, TenantRequest> implements OnInit {

	isNew = true;
	canEdit = false;
	canApprove = false;
	canReject = false;
	canWithdrawn = false;
	canSubmit = false;
	canInProgress = false;
	isDeleted = false;

	tenantRequestStatusEnum = TenantRequestStatus;

	saveClicked = false;
	formGroup: UntypedFormGroup = null;

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
		private tenantRequestService: TenantRequestService,
		private logger: LoggingService
	) {
		super(dialog, language, formService, router, uiNotificationService, httpErrorHandlingService, filterService, datePipe, route, queryParamsService);
	}

	ngOnInit(): void {
		super.ngOnInit();
	}

	getItem(itemId: Guid, successFunction: (item: TenantRequest) => void): void {
		this.tenantRequestService.getSingle(itemId, TenantRequestEditorResolver.lookupFields())
			.pipe(map(data => data as TenantRequest), takeUntil(this._destroyed))
			.subscribe(
				data => successFunction(data),
				error => this.onCallbackError(error)
			);
	}

	prepareForm(data: TenantRequest): void {
		try {
			this.editorModel = data ? new TenantRequestEditorModel().fromModel(data) : new TenantRequestEditorModel();
			this.isDeleted = data ? data.isActive === IsActive.Inactive : false;
			this.isNew = data == null;
			if (this.isNew) {
				this.canEdit = this.authService.hasPermission(this.authService.permissionEnum.CreateTenantRequest);
				this.canSubmit = this.authService.hasPermission(this.authService.permissionEnum.CreateTenantRequest);
				this.canWithdrawn = false;
				this.canApprove = false;
				this.canReject = false;
				this.canInProgress = false;
			} else {
				switch (this.editorModel.status) {
					case TenantRequestStatus.NEW:
						this.canEdit =  this.authService.userId() === data.forUser.id && this.authService.hasPermission(this.authService.permissionEnum.CreateTenantRequest);
						this.canWithdrawn =  this.authService.userId() === data.forUser.id && this.authService.hasPermission(this.authService.permissionEnum.CreateTenantRequest);
						this.canSubmit = this.authService.userId() === data.forUser.id && this.authService.hasPermission(this.authService.permissionEnum.CreateTenantRequest);
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case TenantRequestStatus.APPROVED:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case TenantRequestStatus.DELETED:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case TenantRequestStatus.REJECTED:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case TenantRequestStatus.WITHDRAWN:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case TenantRequestStatus.SUBMITTED:
						this.canEdit = false;
						this.canWithdrawn = this.authService.userId() === data.forUser.id;
						this.canSubmit = false;
						this.canApprove = this.authService.hasPermission(this.authService.permissionEnum.ApproveTenantRequest);
						this.canReject = this.authService.hasPermission(this.authService.permissionEnum.RejectTenantRequest);
						this.canInProgress = this.authService.hasPermission(this.authService.permissionEnum.EditTenantRequest);
						break;
					case TenantRequestStatus.IN_PROCESS:
						this.canEdit = false;
						this.canWithdrawn = this.authService.userId() === data.forUser.id;
						this.canSubmit = false;
						this.canApprove = this.authService.hasPermission(this.authService.permissionEnum.ApproveTenantRequest);
						this.canReject = this.authService.hasPermission(this.authService.permissionEnum.RejectTenantRequest);
						this.canInProgress = this.authService.hasPermission(this.authService.permissionEnum.EditTenantRequest);
					 	break;
				}
			}

			this.buildForm();
		} catch {
			this.logger.error('Could not parse Dataset: ' + data);
			this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.ERRORS.DEFAULT'), SnackBarNotificationLevel.Error);
		}
	}

	public submit() {
		this.clearErrorModel();
		this.formService.touchAllFormFields(this.formGroup);
		if (!this.isFormValid()) {
			return;
		}

		const formData = this.formService.getValue(this.formGroup.value);
		formData.status = TenantRequestStatus.SUBMITTED;
		this.tenantRequestService.persist(formData)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => this.onCallbackSuccess(complete),
				error => this.onCallbackError(error)
			);
	}

	public setStatus(status: TenantRequestStatus) {
		const formData = this.formService.getValue(this.formGroup.value);
		const persist: TenantRequestStatusPersist = {
			id: formData.id,
			hash: formData.hash,
			status: status
		};

		if (status === TenantRequestStatus.APPROVED) {
			const dialogRef = this.dialog.open(ApproveDialogComponent, {
				maxWidth: '700px',
			});
			dialogRef.afterClosed().pipe(takeUntil(this._destroyed)).subscribe(result => {
				if (result) {
					persist.tenantCode = result.tenantCode;
					persist.tenantName = result.tenantName;
					this.tenantRequestService.status(persist)
						.pipe(takeUntil(this._destroyed)).subscribe(
							complete => this.onCallbackSuccess(complete),
							error => this.onCallbackError(error)
						);
				}
			});
		} else {
			this.tenantRequestService.status(persist)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => this.onCallbackSuccess(complete),
				error => this.onCallbackError(error)
		);
		}
	}

	buildForm() {
		this.formGroup = this.editorModel.buildForm(null, this.isDeleted || !this.canEdit);
	}

	refreshData(): void {
		this.getItem(this.editorModel.id, (data: TenantRequest) => this.prepareForm(data));
	}

	refreshOnNavigateToData(id?: Guid): void {
		if (this.isNew) {
			this.formGroup.markAsPristine();
			this.router.navigate(['/tenant-requests/' + (id ? id : this.editorModel.id)], { queryParams: { 'lookup': this.queryParamsService.serializeLookup(this.lookupParams), 'lv': ++this.lv }, replaceUrl: true });
		} else { this.internalRefreshData(); }
	}

	persistEntity(onSuccess?: (response) => void): void {
		const formData = this.formService.getValue(this.formGroup.value);

		this.tenantRequestService.persist(formData)
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
					this.tenantRequestService.delete(value.id).pipe(takeUntil(this._destroyed))
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
}
