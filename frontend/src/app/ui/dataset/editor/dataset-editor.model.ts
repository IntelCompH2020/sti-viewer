import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Dataset, DatasetPersist } from '@app/core/model/dataset/dataset.model';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { BaseEditorModel } from '@common/base/base-editor.model';

export class DatasetEditorModel extends BaseEditorModel implements DatasetPersist {
	title: string;
	notes: string;

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { super(); }

	public fromModel(item: Dataset): DatasetEditorModel {
		if (item) {
			super.fromModel(item);
			this.title = item.title;
			this.notes = item.notes;
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			title: [{ value: this.title, disabled: disabled }, context.getValidation('title').validators],
			notes: [{ value: this.notes, disabled: disabled }, context.getValidation('notes').validators],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
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
}
