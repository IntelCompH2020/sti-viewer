import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { BaseEditorModel } from '@common/base/base-editor.model';
import { AccessRequestConfig, AccessRequestConfigPersist, FilterColumnConfigPersist, Indicator, IndicatorConfig, IndicatorConfigPersist, IndicatorPersist } from '@app/core/model/indicator/indicator.model';
import { IndicatorElastic } from '@app/core/model/indicator-elastic/indicator-elastic';
import { IndicatorElasticBaseType } from '@app/core/enum/indicator-elastic-base-type.enum';

export class IndicatorEditorModel extends BaseEditorModel implements IndicatorPersist {
	code: string;
	name: string;
	description: string;
	config: IndicatorConfigEditorModel = new IndicatorConfigEditorModel();
	indicatorElastic: IndicatorElastic

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { super(); }


	public fromModel(item: Indicator): IndicatorEditorModel {

		if (item) {
			super.fromModel(item);
			this.code = item.code;
			this.name = item.name;
			this.description = item.description;
			this.config = new IndicatorConfigEditorModel().fromModel(item.config);
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }


		return this.formBuilder.group({
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			code: [{ value: this.code, disabled: disabled }, context.getValidation('code').validators],
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			description: [{ value: this.description, disabled: disabled }, context.getValidation('description').validators],
			config: this.config.buildForm()
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, 'Id')] });
		baseValidationArray.push({ key: 'code', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Code')] });
		baseValidationArray.push({ key: 'name', validators: [BackendErrorValidator(this.validationErrorModel, 'Name')] });
		baseValidationArray.push({ key: 'description', validators: [BackendErrorValidator(this.validationErrorModel, 'Description')] });
		baseValidationArray.push({ key: 'config', validators: [BackendErrorValidator(this.validationErrorModel, 'config')] });
		baseValidationArray.push({ key: 'hash', validators: [BackendErrorValidator(this.validationErrorModel, 'hash')] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


export class IndicatorConfigEditorModel implements IndicatorConfigPersist {


	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	accessRequestConfig: AccessRequestConfigEditorModel;

	constructor() { }



	public fromModel(item: IndicatorConfig): IndicatorConfigEditorModel {
		this.accessRequestConfig = new AccessRequestConfigEditorModel().fromModel(item?.accessRequestConfig);
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			accessRequestConfig: this.accessRequestConfig.buildForm()
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'accessRequestConfig', validators: [BackendErrorValidator(this.validationErrorModel, 'AccessRequestConfig')] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}



export class AccessRequestConfigEditorModel implements AccessRequestConfigPersist {


	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	filterColumns: FilterColumnConfigPersist[] = [];

	constructor() { }



	public fromModel(item: AccessRequestConfig): AccessRequestConfigEditorModel {
		if (item) {
			if (item.filterColumns) {
				this.filterColumns = item.filterColumns.map(x => ({ code: x.code } as FilterColumnConfigPersist))
			}
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const filterColumnsFormArray: UntypedFormGroup[] = [];
		if (this.filterColumns) {
			this.filterColumns.forEach(element => {
				filterColumnsFormArray.push(this.formBuilder.group({ code: element.code }));
			});
		}
		return this.formBuilder.group({
			filterColumns: this.formBuilder.array(filterColumnsFormArray)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'filterColumns', validators: [BackendErrorValidator(this.validationErrorModel, 'filterColumns')] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

