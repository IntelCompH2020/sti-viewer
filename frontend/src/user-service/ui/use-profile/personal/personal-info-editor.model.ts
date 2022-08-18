import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { UserServiceNamePatch, UserServiceUser } from '@user-service/core/model/user.model';

export class UserProfilePersonalInfoEditorModel implements UserServiceNamePatch {
	firstName: string;
	lastName: string;
	hash: string;
	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { }

	public fromModel(item: UserServiceUser): UserProfilePersonalInfoEditorModel {
		this.firstName = item.firstName;
		this.lastName = item.lastName;
		this.hash = item.hash;
		return this;
	}

	buildForm(disabled: boolean = false): UntypedFormGroup {
		return this.formBuilder.group({
			hash: [{ value: this.hash, disabled: disabled }, [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Hash')]],
			firstName: [{ value: this.firstName, disabled: disabled }, [Validators.required]],
			lastName: [{ value: this.lastName, disabled: disabled }, [Validators.required]]
		});
	}
}
