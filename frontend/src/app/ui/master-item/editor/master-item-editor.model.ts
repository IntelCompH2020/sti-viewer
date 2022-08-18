import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Dataset } from '@app/core/model/dataset/dataset.model';
import { DetailItem, DetailItemPersist } from '@app/core/model/master-item/detail-item.model';
import { MasterItem, MasterItemPersist } from '@app/core/model/master-item/master-item.model';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { Guid } from '@common/types/guid';
import { BaseEditorModel } from '@common/base/base-editor.model';

export class DetailItemEditorModel extends BaseEditorModel implements DetailItemPersist {
	title: string;
	decimal: number;
	dateTime: Date;
	time: Date;
	date: Date;
	dataset: Dataset;
	datasetId: Guid;

	public validationErrorModel;
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { super(); }

	public fromModel(item: DetailItem): DetailItemEditorModel {
		if (item) {
			super.fromModel(item);
			this.title = item.title;
			this.decimal = item.decimal;
			this.dateTime = item.dateTime;
			this.time = item.time;
			this.date = item.date;
			this.dataset = item.dataset;
			if (item.dataset) { this.datasetId = item.dataset.id; }
		}
		return this;
	}

	buildForm(context: ValidationContext = null, baseProperty: string = null, disabled: boolean = false, validationErrorModel: ValidationErrorModel): UntypedFormGroup {
		this.validationErrorModel = validationErrorModel;
		if (context == null) { context = this.createValidationContext(baseProperty); }
		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			title: [{ value: this.title, disabled: disabled }, context.getValidation('title').validators],
			decimal: [{ value: this.decimal, disabled: disabled }, context.getValidation('decimal').validators],
			dateTime: [{ value: this.dateTime, disabled: disabled }, context.getValidation('dateTime').validators],
			time: [{ value: this.time, disabled: disabled }, context.getValidation('time').validators],
			date: [{ value: this.date, disabled: disabled }, context.getValidation('date').validators],
			dataset: [{ value: this.dataset, disabled: disabled }, context.getValidation('dataset').validators],
			isActive: [{ value: this.isActive, disabled: true }, context.getValidation('isActive').validators],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
		});
	}

	createValidationContext(baseProperty?: string): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Id'))] });
		baseValidationArray.push({ key: 'title', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Title'))] });
		baseValidationArray.push({ key: 'decimal', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Decimal'))] });
		baseValidationArray.push({ key: 'dateTime', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'DateTime'))] });
		baseValidationArray.push({ key: 'time', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Time'))] });
		baseValidationArray.push({ key: 'date', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'Date'))] });
		baseValidationArray.push({ key: 'dataset', validators: [BackendErrorValidator(this.validationErrorModel, this.helperGetValidation(baseProperty, 'DatasetId'))] });
		baseValidationArray.push({ key: 'isActive', validators: [] });
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

export class MasterItemEditorModel extends BaseEditorModel implements MasterItemPersist {
	title: string;
	notes: string;
	details: DetailItemEditorModel[] = [];

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { super(); }

	public fromModel(item: MasterItem): MasterItemEditorModel {
		if (item) {
			this.id = item.id;
			this.title = item.title;
			this.notes = item.notes;
			this.isActive = item.isActive;
			this.hash = item.hash;
			if (item.details) { this.details = item.details.map(x => new DetailItemEditorModel().fromModel(x)); }
			if (item.createdAt) { this.createdAt = item.createdAt; }
			if (item.updatedAt) { this.updatedAt = item.updatedAt; }
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const detailsFormArray = new Array<UntypedFormGroup>();
		const inactiveDetailsFormArray = new Array<UntypedFormGroup>();
		if (this.details) {
			this.details.forEach((element, index) => {
				if (element.isActive === IsActive.Active) {
					detailsFormArray.push(this.buildDetailForm(element, index, disabled));
				} else {
					inactiveDetailsFormArray.push(this.buildDetailForm(element, index, true));
				}
			});
		}

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			title: [{ value: this.title, disabled: disabled }, context.getValidation('title').validators],
			notes: [{ value: this.notes, disabled: disabled }, context.getValidation('notes').validators],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
			details: this.formBuilder.array(detailsFormArray),
			inactiveDetails: this.formBuilder.array(inactiveDetailsFormArray),
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, 'Id')] });
		baseValidationArray.push({ key: 'title', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Title')] });
		baseValidationArray.push({ key: 'notes', validators: [BackendErrorValidator(this.validationErrorModel, 'Notes')] });
		baseValidationArray.push({ key: 'hash', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

	buildDetailForm(detail: DetailItemEditorModel, index: number, disabled: boolean = false): UntypedFormGroup {
		return detail.buildForm(null, `Details[${index}]`, detail.isActive === IsActive.Inactive ? true : disabled, this.validationErrorModel);
	}

	helperReapplyValidators(details: UntypedFormArray) {
		this.validationErrorModel.clear();
		if (!Array.isArray(details.controls)) { return; }
		details.controls.forEach((element, index) => {
			const detailItemEditorModel = new DetailItemEditorModel();
			detailItemEditorModel.validationErrorModel = this.validationErrorModel;
			const context = detailItemEditorModel.createValidationContext(`Details[${index}]`);
			const formGroup = element as UntypedFormGroup;
			Object.keys(formGroup.controls).forEach(key => {
				formGroup.get(key).setValidators(context.getValidation(key).validators);
				formGroup.get(key).updateValueAndValidity();
			});
		});
	}
}
