import { Component, Inject, OnInit } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from "@angular/material/dialog";
import { ExternalToken } from "@app/core/model/external-token/external-token.model";
import { ExternalTokenService } from "@app/core/services/http/external-token.service";
import { BaseComponent } from "@common/base/base.component";
import { map, takeUntil } from 'rxjs/operators';
import { Guid } from "@common/types/guid";
import { TokenExpirationDialogResolver } from "./token-expiration-dialog..resolver";
import { ExternalTokenExpirationDialogEditorModel } from "./token-expiration-dialog.model";
import { ActivatedRoute, Router } from "@angular/router";
import { DatePipe } from "@angular/common";
import { FilterService } from "@common/modules/text-filter/filter-service";
import { HttpError, HttpErrorHandlingService } from "@common/modules/errors/error-handling/http-error-handling.service";
import { SnackBarNotificationLevel, UiNotificationService } from "@common/modules/notification/ui-notification-service";
import { FormService } from "@common/forms/form-service";
import { TranslateService } from "@ngx-translate/core";
import { IsActive } from "@app/core/enum/is-active.enum";
import { LoggingService } from "@common/logging/logging-service";
import { AuthService } from "@app/core/services/ui/auth.service";
import { AppPermission } from "@app/core/enum/permission.enum";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
    templateUrl: './token-expiration-dialog.component.html'
})
export class TokenExpirationDialogComponent extends BaseComponent implements OnInit{
    formGroup: FormGroup
	isNew = true;
	isDeleted = false;
	lookupParams: any;
	editorModel: ExternalTokenExpirationDialogEditorModel;

	constructor(
		@Inject(MAT_DIALOG_DATA) private data: TokenExpirationData,
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
		// Rest dependencies. Inject any other needed deps here:
		public authService: AuthService,
		private dialogRef: MatDialogRef<TokenExpirationDialogComponent>,
		private externalTokenService: ExternalTokenService,
		private logger: LoggingService
	) {
		super();
	}

	getItem(itemId: Guid): void {
		this.externalTokenService.getSingle(itemId, TokenExpirationDialogResolver.lookupFields())
			.pipe(map(data => data as ExternalToken), takeUntil(this._destroyed))
			.subscribe(
				data => this.prepareForm(data),
				error => this.onCallbackError(error)
			);
	}


	prepareForm(data: ExternalToken): void {
		try {

			this.editorModel = data ? new ExternalTokenExpirationDialogEditorModel().fromModel(data) : new ExternalTokenExpirationDialogEditorModel();
			this.isDeleted = data ? data.isActive === IsActive.Inactive : false;
			this.buildForm();
		} catch (e){
			console.error(e, data);
            this.logger.error("Could not parse  Channel: " + JSON.stringify(data));
			this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.ERRORS.DEFAULT'), SnackBarNotificationLevel.Error);
		}
	}

	ngOnInit(): void {
		if (this.data.id) {
			this.getItem(this.data.id)
		} else {
			this.prepareForm(null);
		}
	}

	buildForm() {
		this.formGroup = this.editorModel.buildForm(null, this.isDeleted || !this.authService.hasPermission(AppPermission.EditExternalToken));
	}


	persistEntity(): void {

		const formData = this.formService.getValue(this.formGroup.value);

		this.externalTokenService.persistExpiration(formData)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => this.dialogRef.close(true),
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

	public save() {
		this.clearErrorModel();
	}

	public isFormValid() {
		return this.formGroup.valid;
	}

	onCallbackError(errorResponse: HttpErrorResponse) {
		const error: HttpError = this.httpErrorHandlingService.getError(errorResponse);
		if (error.statusCode === 400) {
			this.editorModel.validationErrorModel.fromJSONObject(errorResponse.error);
			this.formService.validateAllFormFields(this.formGroup);
		} else {
			this.uiNotificationService.snackBarNotification(error.getMessagesString(), SnackBarNotificationLevel.Warning);
		}
	}

	clearErrorModel() {
		this.editorModel.validationErrorModel.clear();
		this.formService.validateAllFormFields(this.formGroup);
	}

    cancel(): void{
        this.dialogRef.close(false);
	}

}

export class TokenExpirationData{
	id: Guid;
}
