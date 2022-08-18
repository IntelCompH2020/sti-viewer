import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { BaseEditorModel } from '@common/base/base-editor.model';
import { TenantRequest, TenantRequestPersist } from '@app/core/model/tenant-request/tenant.request.model';
import { TenantRequestStatus } from '@app/core/enum/teanant-request.enum';

export class TenantRequestEditorModel extends BaseEditorModel implements TenantRequestPersist {
	message: string;
	status: TenantRequestStatus = TenantRequestStatus.NEW;
	subjectId: string;
	email: string;
	firstName: string;
	lastName: string;

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { super(); }

	public fromModel(item: TenantRequest): TenantRequestEditorModel {
		if (item) {
			super.fromModel(item);
			this.message = item.message;
			this.status = item.status;
			this.email = item.email;
			if (item.forUser) {
				this.subjectId = item.forUser.subjectId;
				this.firstName = item.forUser.firstName;
				this.lastName = item.forUser.lastName;
			}
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			message: [{ value: this.message, disabled: disabled }, context.getValidation('message').validators],
			status: [{ value: this.status, disabled: disabled }, context.getValidation('status').validators],
			subjectId: [{ value: this.subjectId, disabled: true }],
			email: [{ value: this.email, disabled: disabled }, context.getValidation('email').validators],
			firstName: [{ value: this.firstName, disabled: true }],
			lastName: [{ value: this.lastName, disabled: true }],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, 'Id')] });
		baseValidationArray.push({ key: 'message', validators: [BackendErrorValidator(this.validationErrorModel, 'Message')] });
		baseValidationArray.push({ key: 'status', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Status')] });
		baseValidationArray.push({ key: 'email', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Email')] });
		baseValidationArray.push({ key: 'hash', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}
