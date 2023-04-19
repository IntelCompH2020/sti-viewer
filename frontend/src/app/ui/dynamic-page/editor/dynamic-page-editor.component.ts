import { Component, OnInit } from '@angular/core';
import { FormControl, UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { DynamicPage } from "@app/core/model/dynamic-page/dynamic-page.model";
import { DynamicPageService } from "@app/core/services/http/dynamic-page.service";
import { AuthService } from '@app/core/services/ui/auth.service';
import { DynamicPageContentEditorModel, DynamicPageEditorModel } from "@app/ui/dynamic-page/editor/dynamic-page-editor.model";
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
import { DynamicPageEditorResolver } from "@app/ui/dynamic-page/editor/dynamic-page-editor.resolver";
import { LanguageType } from '@app/core/enum/language-type.enum';
import { LanguageService } from '@user-service/services/language.service';
import { DynamicPageVisibility } from '@app/core/enum/dynamic-page-visibility.enum';
import { DynamicPageType } from '@app/core/enum/dynamic-page-type.enum';
import { DynamicPageProviderService } from '@app/core/services/ui/dynamic-page.service';
import { MatSelectChange } from '@angular/material/select';
import { COMMA, ENTER } from '@angular/cdk/keycodes';

@Component({
	selector: "app-dynamic-page-editor",
	templateUrl: "./dynamic-page-editor.component.html",
	styleUrls: ["./dynamic-page-editor.component.scss"],
})
export class DynamicPageEditorComponent
	extends BaseEditor<DynamicPageEditorModel, DynamicPage>
	implements OnInit
{
	isNew = true;
	isDeleted = false;
	saveClicked = false;
	formGroup: UntypedFormGroup = null;
	languageTypeValues: Array<LanguageType>;
	dynamicPageVisibilityTypeValues: Array<DynamicPageVisibility>;
	dynamicPageTypeValues: Array<DynamicPageType>;
	dynamicPageType = DynamicPageType;
	dynamicPageVisibility = DynamicPageVisibility;
	readonly separatorKeysCodes = [ENTER, COMMA] as const;

	constructor(
		// BaseFormEditor injected dependencies
		protected dialog: MatDialog,
		public languageService: LanguageService,
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
		private dynamicPageService: DynamicPageService,
		private dynamicPageProviderService: DynamicPageProviderService,
		private logger: LoggingService
	) {
		super(
			dialog,
			language,
			formService,
			router,
			uiNotificationService,
			httpErrorHandlingService,
			filterService,
			datePipe,
			route,
			queryParamsService
		);
	}

	ngOnInit(): void {
		super.ngOnInit();
		this.languageTypeValues = this.enumUtils.getEnumValues(LanguageType);
		this.dynamicPageVisibilityTypeValues = this.enumUtils.getEnumValues(DynamicPageVisibility);
		this.dynamicPageTypeValues = this.enumUtils.getEnumValues(DynamicPageType);
	}

	getItem(itemId: Guid, successFunction: (item: DynamicPage) => void): void {
		this.dynamicPageService
			.getSingle(itemId, DynamicPageEditorResolver.lookupFields())
			.pipe(
				map((data) => data as DynamicPage),
				takeUntil(this._destroyed)
			)
			.subscribe(
				(data) => successFunction(data),
				(error) => this.onCallbackError(error)
			);
	}

	prepareForm(data: DynamicPage): void {
		try {
			this.editorModel = data
				? new DynamicPageEditorModel().fromModel(data)
				: new DynamicPageEditorModel();
			if (!data) {
				this.editorModel.defaultLanguage = this.languageService.getLanguageValue(this.languageService.getCurrentLanguage());
			}
			if (!this.editorModel.pageContents) {
				this.editorModel.pageContents = [];
			}
			this.enumUtils.getEnumValues<LanguageType>(LanguageType)?.forEach(x => {
				const lanuageString = this.languageService.getLanguageValue(x);
				const itemFound = this.editorModel.pageContents.find(x => x.language == lanuageString);
				if (!itemFound) {
					const item = new DynamicPageContentEditorModel();
					item.language = this.languageService.getLanguageValue(x);
					this.editorModel.pageContents.push(item);
				}
			});

			this.isDeleted = data ? data.isActive === IsActive.Inactive : false;
			this.buildForm();
		} catch {
			this.logger.error("Could not parse DynamicPage: " + data);
			this.uiNotificationService.snackBarNotification(
				this.language.instant("COMMONS.ERRORS.DEFAULT"),
				SnackBarNotificationLevel.Error
			);
		}
	}

	buildForm() {
		this.formGroup = this.editorModel.buildForm(
			null,
			this.isDeleted ||
				!this.authService.hasPermission(AppPermission.EditDynamicPage)
		);
	}

	refreshData(): void {
		this.getItem(this.editorModel.id, (data: DynamicPage) =>
			this.prepareForm(data)
		);
	}

	refreshOnNavigateToData(id?: Guid): void {
		if (this.isNew) {
			this.formGroup.markAsPristine();
			this.router.navigate(
				["/dynamic-pages/" + (id ? id : this.editorModel.id)],
				{
					queryParams: {
						lookup: this.queryParamsService.serializeLookup(
							this.lookupParams
						),
						lv: ++this.lv,
					},
					replaceUrl: true,
				}
			);
		} else {
			this.internalRefreshData();
		}
	}

	persistEntity(onSuccess?: (response) => void): void {
		const formData = this.formService.getValue(this.formGroup.value);

		this.dynamicPageService
			.persist(formData)
			.pipe(takeUntil(this._destroyed))
			.subscribe(
				(complete) =>
					onSuccess
						? onSuccess(complete)
						: this.onCallbackSuccess(complete),
				(error) => this.onCallbackError(error)
			);
	}

	formSubmit(): void {
		this.formService.touchAllFormFields(this.formGroup);
		if (!this.isFormValid()) {
			return;
		}

		this.persistEntity((complete) => { this.dynamicPageProviderService.refresh(); this.onCallbackSuccess(complete)});
	}

	public delete() {
		const value = this.formGroup.value;
		if (value.id) {
			const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
				maxWidth: "300px",
				data: {
					message: this.language.instant(
						"COMMONS.CONFIRMATION-DIALOG.DELETE-ITEM"
					),
					confirmButton: this.language.instant(
						"COMMONS.CONFIRMATION-DIALOG.ACTIONS.CONFIRM"
					),
					cancelButton: this.language.instant(
						"COMMONS.CONFIRMATION-DIALOG.ACTIONS.CANCEL"
					),
				},
			});
			dialogRef
				.afterClosed()
				.pipe(takeUntil(this._destroyed))
				.subscribe((result) => {
					if (result) {
						this.dynamicPageService
							.delete(value.id)
							.pipe(takeUntil(this._destroyed))
							.subscribe(
								(complete) => this.onCallbackSuccess(),
								(error) => this.onCallbackError(error)
							);
					}
				});
		}
	}

	clearErrorModel() {
		this.editorModel.validationErrorModel.clear();
		this.formService.validateAllFormFields(this.formGroup);
	}

	public onTypeChange(event: MatSelectChange): void {

		if (event.value == DynamicPageType.External) this.formGroup.get('config')?.get('externalUrl')?.enable();
		else this.formGroup.get('config')?.get('externalUrl')?.disable();
	}

	public onvVisibilityChange(event: MatSelectChange): void {

		if (event.value == DynamicPageVisibility.HasRole) this.formGroup.get('config')?.get('allowedRoles')?.enable();
		else this.formGroup.get('config')?.get('allowedRoles')?.disable();
	}

	public addRole(input): void {
		const inputValue = input.value;
        if(!inputValue){
            return;
        }

		const array = this.formGroup.get('config')?.get('allowedRoles')?.value || [];
        array.push(inputValue);
		this.formGroup.get('config')?.get('allowedRoles').setValue([...new Set(array)]);
        input.value = '';
	}

	public removeRole(extrnalRole): void {
		let currentRoles = this.formGroup.get('config')?.get('allowedRoles')?.value || [];
		currentRoles = currentRoles.filter(x=> x !=  extrnalRole);
		this.formGroup.get('config')?.get('allowedRoles').setValue(currentRoles);
	}
}
