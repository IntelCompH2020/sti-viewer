import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BackendErrorValidator } from '@common/forms/validation/custom-validator';
import { ValidationErrorModel } from '@common/forms/validation/error-model/validation-error-model';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { BaseEditorModel } from '@common/base/base-editor.model';
import { DataAccessFilterColumn, DataAccessFilterColumnPersist, DataAccessRequest, DataAccessRequestConfig, DataAccessRequestConfigPersist, DataAccessRequestIndicatorConfig, DataAccessRequestIndicatorConfigPersist, DataAccessRequestIndicatorGroupConfigPersist, DataAccessRequestPersist } from '@app/core/model/data-access-request/data-access-request.model';
import { DataAccessRequestStatus } from '@app/core/enum/data-access-request-status.enum';

import { User } from '@app/core/model/user/user.model';
import { Indicator } from '@app/core/model/indicator/indicator.model';
import { Guid } from '@common/types/guid';




export class DataAccessRequestEditorModel extends BaseEditorModel implements DataAccessRequestPersist {
	user: User;
	status: DataAccessRequestStatus = DataAccessRequestStatus.NEW;
	indicatorsConfigArray: DataAccessRequestIndicatorConfigEditorModel[] = [];
	indicatorGroupConfigs: DataAccessRequestIndicatorGroupConfigPersist[] = [];
	config: DataAccessRequestConfigPersist;


	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	private _groupIdToIndicatorIds: Record<string, Guid[]> = {};

	constructor() { super(); }


	public fromModel(item: DataAccessRequest): DataAccessRequestEditorModel {
		if (item) {
			super.fromModel(item);

			this.indicatorsConfigArray = item?.config?.indicators?.map(config => {
				return new DataAccessRequestIndicatorConfigEditorModel().fromModel(config);
			}) ?? []


			if(item.config?.indicatorGroups?.length){
				this.indicatorGroupConfigs = item.config.indicatorGroups.map(group =>{ 
				
					this._groupIdToIndicatorIds[group.indicatorGroup.id.toString()]  = group.indicatorGroup.indicators?.map(x => x.id);
					
					return {
						filterColumns: group.filterColumns,
						groupId: group.indicatorGroup.id
					}
				})
			}
			this.status = item.status;
		}

		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		let tmp: UntypedFormGroup[] = [];
		this.indicatorsConfigArray.forEach(config => {
			tmp.push(config.buildForm());
		})

		// if (tmp.length == 0) {
		// 	tmp.push(new DataAccessRequestIndicatorConfigEditorModel().buildForm());
		// }
		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
			status: [{ value: this.status, disabled: disabled }, context.getValidation('status').validators],
			indicatorsConfigArray: this.formBuilder.array(tmp),
			indicatorGroupConfigs: this.formBuilder.array(
				this.indicatorGroupConfigs?.map(groupConfig => this.formBuilder.group({
					groupId: [{value:groupConfig.groupId, disabled}],
					indicatorIds: [{value: this._groupIdToIndicatorIds[groupConfig.groupId.toString()], disabled}],
					filterColumns: this.formBuilder.array(
						groupConfig?.filterColumns?.map(fc => this.formBuilder.group({column: [{value:fc.column, disabled}], values: [{value: fc.values, disabled}]})) ?? []
					)
				}))?? []
			)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, 'Id')] });
		baseValidationArray.push({ key: 'hash', validators: [] });
		baseValidationArray.push({ key: 'status', validators: [Validators.required, BackendErrorValidator(this.validationErrorModel, 'Status')] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}



export class DataAccessRequestIndicatorConfigEditorModel implements DataAccessRequestIndicatorConfig {
	indicator: Indicator;
	filterColumns: DataAccessFilterColumnEditorModel[];

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { }

	public fromModel(item: DataAccessRequestIndicatorConfig): DataAccessRequestIndicatorConfigEditorModel {
		if (item) {
			this.indicator = item.indicator;
			if (item.filterColumns) {
				this.filterColumns = item.filterColumns.map(column => {
					return new DataAccessFilterColumnEditorModel().fromModel(column);
				});
			}
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			indicator: [{ value: this.indicator, disabled: disabled }, context.getValidation('indicator').validators],
			filterColumns: this.filterColumns != null ? this.formBuilder.array(this.filterColumns.map(column => column.buildForm())) : this.formBuilder.array([new DataAccessFilterColumnEditorModel().buildForm()])
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'indicator', validators: [BackendErrorValidator(this.validationErrorModel, 'indicator')] });
		baseValidationArray.push({ key: 'filterColumns', validators: [BackendErrorValidator(this.validationErrorModel, 'filterColumns')] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

export class DataAccessFilterColumnEditorModel implements DataAccessFilterColumn {
	column: string = '';
	values: string[] = [];

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { }

	public fromModel(item: DataAccessFilterColumn): DataAccessFilterColumnEditorModel {

		if (item) {

			this.column = item.column;
			this.values = item.values;
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			column: [{ value: this.column, disabled: disabled }, context.getValidation('column').validators],
			values: [{ value: this.values, disabled: disabled }, context.getValidation('values').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'column', validators: [BackendErrorValidator(this.validationErrorModel, 'column')] });
		baseValidationArray.push({ key: 'values', validators: [BackendErrorValidator(this.validationErrorModel, 'values')] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}