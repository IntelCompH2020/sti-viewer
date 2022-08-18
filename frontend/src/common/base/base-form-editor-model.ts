import { UntypedFormBuilder } from '@angular/forms';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';

export abstract class BaseFormEditorModel {

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
}
