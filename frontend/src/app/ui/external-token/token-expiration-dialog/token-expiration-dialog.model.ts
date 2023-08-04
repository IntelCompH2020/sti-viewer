import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { BaseEditorModel } from '@common/base/base-editor.model';
import { ExternalToken, ExternalTokenExpirationPersist } from '@app/core/model/external-token/external-token.model';

export class ExternalTokenExpirationDialogEditorModel extends BaseEditorModel implements ExternalTokenExpirationPersist {
	expiresAt: Date;
	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { super(); }


	public fromModel(item: ExternalToken): ExternalTokenExpirationDialogEditorModel {

		if (item) {
			super.fromModel(item);
			this.expiresAt = item.expiresAt;
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }


		return this.formBuilder.group({
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			expiresAt: [{ value: this.expiresAt, disabled: disabled }, context.getValidation('expiresAt').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, 'Id')] });
		baseValidationArray.push({ key: 'expiresAt', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'ExpiresAt')] });
		baseValidationArray.push({ key: 'hash', validators: [BackendErrorValidator(this.validationErrorModel, 'hash')] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

