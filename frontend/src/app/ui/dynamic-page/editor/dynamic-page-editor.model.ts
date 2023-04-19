import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { DynamicPage, DynamicPageConfig, DynamicPageConfigPersist, DynamicPagePersist } from '@app/core/model/dynamic-page/dynamic-page.model';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { BaseEditorModel } from '@common/base/base-editor.model';
import { DynamicPageType } from '@app/core/enum/dynamic-page-type.enum';
import { DynamicPageVisibility } from '@app/core/enum/dynamic-page-visibility.enum';
import { DynamicPageContent, DynamicPageContentPersist } from '@app/core/model/dynamic-page/dynamic-page-content.model';
import { Guid } from '@common/types/guid';
import { IsActive } from '@idp-service/core/enum/is-active.enum';

export class DynamicPageEditorModel extends BaseEditorModel implements DynamicPagePersist {
	order: number;
	type: DynamicPageType;
	visibility: DynamicPageVisibility;
	defaultLanguage: string;
	config: DynamicPageConfigEditorModel;
	pageContents: DynamicPageContentEditorModel[];
	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() {
		super();
		this.config = new DynamicPageConfigEditorModel();
	}

	public fromModel(item: DynamicPage): DynamicPageEditorModel {
		if (item) {
			super.fromModel(item);
			this.order = item.order;
			this.type = item.type;
			this.visibility = item.visibility;
			this.defaultLanguage = item.defaultLanguage;
			if (item.pageContents) { this.pageContents = item.pageContents.filter(x=> x.isActive === IsActive.Active).map(x => new DynamicPageContentEditorModel().fromModel(x)); }
			this.config =  new DynamicPageConfigEditorModel().fromModel(item.config);
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const pageContentsFormArray = new Array<UntypedFormGroup>();
		if (this.pageContents) {
			this.pageContents.forEach((element, index) => {
				pageContentsFormArray.push(this.buildPageContentsForm(element, index, disabled));
			});
		}

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			order: [{ value: this.order, disabled: disabled }, context.getValidation('order').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			visibility: [{ value: this.visibility, disabled: disabled }, context.getValidation('visibility').validators],
			defaultLanguage: [{ value: this.defaultLanguage, disabled: disabled }, context.getValidation('defaultLanguage').validators],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
			pageContents: this.formBuilder.array(pageContentsFormArray),
			config: this.config ? this.buildPageConfigsForm(this.config, this.type, this.visibility, disabled) : null,
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, 'Id')] });
		baseValidationArray.push({ key: 'order', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Order')] });
		baseValidationArray.push({ key: 'type', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Type')] });
		baseValidationArray.push({ key: 'visibility', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Visibility')] });
		baseValidationArray.push({ key: 'defaultLanguage', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'DefaultLanguage')] });
		baseValidationArray.push({ key: 'hash', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

	buildPageConfigsForm(detail: DynamicPageConfigEditorModel, pageType: DynamicPageType, visibility: DynamicPageVisibility, disabled: boolean = false): UntypedFormGroup {
		const form = detail.buildForm(null, `Config`, disabled, this.validationErrorModel);
		if (pageType !== DynamicPageType.External) form.get('externalUrl').disable();
		if (visibility !== DynamicPageVisibility.HasRole) form.get('allowedRoles').disable();
		return form;
	}

	buildPageContentsForm(detail: DynamicPageContentEditorModel, index: number, disabled: boolean = false): UntypedFormGroup {
		return detail.buildForm(null, `PageContents[${index}]`, detail.isActive === IsActive.Inactive ? true : disabled, this.validationErrorModel);
	}

	helperReapplyPageContentValidators(details: UntypedFormArray) {
		this.validationErrorModel.clear();
		if (!Array.isArray(details.controls)) { return; }
		details.controls.forEach((element, index) => {
			const detailItemEditorModel = new DynamicPageContentEditorModel();
			detailItemEditorModel.validationErrorModel = this.validationErrorModel;
			const context = detailItemEditorModel.createValidationContext(`PageContents[${index}]`);
			const formGroup = element as UntypedFormGroup;
			Object.keys(formGroup.controls).forEach(key => {
				formGroup.get(key).setValidators(context.getValidation(key).validators);
				formGroup.get(key).updateValueAndValidity();
			});
		});
	}
}

export class DynamicPageContentEditorModel extends BaseEditorModel implements DynamicPageContentPersist {
	pageId: Guid;
	page: DynamicPage;
	title: string;
	content: string;
	language: string;

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { super(); }

	public fromModel(item: DynamicPageContent): DynamicPageContentEditorModel {
		if (item) {
			super.fromModel(item);
			this.title = item.title;
			this.content = item.content;
			if (item.page) { this.pageId = item.page.id; }
			this.language = item.language;
		}
		return this;
	}

	buildForm(context: ValidationContext = null, baseProperty: string = null, disabled: boolean = false, validationErrorModel: ValidationErrorModel): UntypedFormGroup {
		this.validationErrorModel = validationErrorModel;
		if (context == null) { context = this.createValidationContext(baseProperty); }

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			title: [{ value: this.title, disabled: disabled }, context.getValidation('title').validators],
			content: [{ value: this.content, disabled: disabled }, context.getValidation('content').validators],
			language: [{ value: this.language, disabled: disabled }, context.getValidation('language').validators],
			page: [{ value: this.page, disabled: disabled }, context.getValidation('page').validators],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
		});
	}

	createValidationContext(baseProperty?: string): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, this.helperGetValidation(baseProperty, 'Id')))] });
		baseValidationArray.push({ key: 'title', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Title'))] });
		baseValidationArray.push({ key: 'content', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Content'))] });
		baseValidationArray.push({ key: 'language', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Language'))] });
		baseValidationArray.push({ key: 'page', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'PageId'))] });
		baseValidationArray.push({ key: 'hash', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}


	helperGetValidation(baseProperty: string, property: string) {
		if (baseProperty) {
			return `${baseProperty}.${property}`;
		} else {
			return property;
		}
	}
}

export class DynamicPageConfigEditorModel  implements DynamicPageConfigPersist {

	allowedRoles: string[];
	externalUrl: string;
	matIcon: string;

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { }

	public fromModel(item: DynamicPageConfig): DynamicPageConfigEditorModel {
		if (item) {
			this.allowedRoles = item.allowedRoles;
			this.externalUrl = item.externalUrl;
			this.matIcon = item.matIcon;
		}
		return this;
	}

	buildForm(context: ValidationContext = null, baseProperty: string = null, disabled: boolean = false, validationErrorModel: ValidationErrorModel): UntypedFormGroup {
		this.validationErrorModel = validationErrorModel;
		if (context == null) { context = this.createValidationContext(baseProperty); }

		return this.formBuilder.group({
			matIcon: [{ value: this.matIcon, disabled: disabled }, context.getValidation('matIcon').validators],
			externalUrl: [{ value: this.externalUrl, disabled: disabled }, context.getValidation('externalUrl').validators],
			allowedRoles: [{ value: this.allowedRoles, disabled: disabled }, context.getValidation('allowedRoles').validators],
		});
	}

	createValidationContext(baseProperty?: string): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'matIcon', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, this.helperGetValidation(baseProperty, 'MatIcon')))] });
		baseValidationArray.push({ key: 'externalUrl', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, this.helperGetValidation(baseProperty, 'ExternalUrl')))] });
		baseValidationArray.push({ key: 'allowedRoles', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, this.helperGetValidation(baseProperty, 'AllowedRoles')))] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}


	helperGetValidation(baseProperty: string, property: string) {
		if (baseProperty) {
			return `${baseProperty}.${property}`;
		} else {
			return property;
		}
	}
}
