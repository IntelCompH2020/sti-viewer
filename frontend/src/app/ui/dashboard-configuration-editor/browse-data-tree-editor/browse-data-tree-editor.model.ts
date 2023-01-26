import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { BrowseDataFieldModel, BrowseDataTreeConfigModel, BrowseDataTreeLevelConfigModel, BrowseDataTreeLevelDashboardOverrideModel, DataTreeLevelDashboardOverrideFieldRequirement } from "@app/core/model/data-tree/browse-data-tree-config.model";
import { AltTextModel, OperatorModel, ValueRangeModel } from "@app/core/model/indicator-config/indicator-report-level-config";
import { ValidationErrorModel } from "@common/forms/validation/error-model/validation-error-model";
import { Validation, ValidationContext } from "@common/forms/validation/validation-context";

export class BrowseDataTreeEditorModel implements BrowseDataTreeConfigModel{

    id: string;
    name: string;
    levelConfigs: BrowseDataTreeLevelConfigModel[] = [];
    order: number;
    goTo: string;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }

	public fromModel(item: BrowseDataTreeEditorModel): BrowseDataTreeEditorModel {
		if (item) {
            this.id = item.id;
            this.name = item.name;
            this.levelConfigs = item.levelConfigs ?? [];
            this.order = item.order;
            this.goTo = item.goTo;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {

		const contextParam = context;

		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			levelConfigs: this.formBuilder.array(
                this.levelConfigs.map(levelConfig => new BrowseDataTreeLevelConfigEditorModel().fromModel(levelConfig).buildForm(contextParam, disabled))
            , context.getValidation('levelConfigs').validators),
			order: [{ value: this.order, disabled: disabled }, context.getValidation('order').validators],
			goTo: [{ value: this.goTo, disabled: disabled }, context.getValidation('goTo').validators],
            
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'id', validators: [] });
		baseValidationArray.push({ key: 'name', validators: [] });
		baseValidationArray.push({ key: 'levelConfigs', validators: [] });
		baseValidationArray.push({ key: 'order', validators: [] });
		baseValidationArray.push({ key: 'goTo', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


export class BrowseDataTreeLevelConfigEditorModel implements BrowseDataTreeLevelConfigModel{


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


	constructor() { }
    field: BrowseDataFieldModel;
    order: number;
    supportSubLevel?: boolean;
    defaultDashboards: string[];
    dashboardOverrides: BrowseDataTreeLevelDashboardOverrideModel[];

	public fromModel(item: BrowseDataTreeLevelConfigModel): BrowseDataTreeLevelConfigEditorModel {
		if (item) {
            this.field = item.field;
            this.order = item.order;
            this.supportSubLevel = !!item.supportSubLevel;
            this.defaultDashboards = item.defaultDashboards;
            this.dashboardOverrides = item.dashboardOverrides;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		const contextParam = context;
		if (context == null) { context = this.createValidationContext(); }

		const fg =  this.formBuilder.group({
			field: new FieldModelEditorModel().fromModel(this.field).buildForm(),
			order: [{ value: this.order, disabled: disabled }, context.getValidation('order').validators],
			supportSubLevel: [{ value: this.supportSubLevel, disabled: disabled }, context.getValidation('supportSubLevel').validators],
			// defaultDashboards: this.formBuilder.array(
            //     this.defaultDashboards.map(
            //         defaultDashboard => this.formBuilder.control(defaultDashboard) // todo validators
            //     )
            // , context.getValidation('defaultDashboards').validators),
			// dashboardOverrides: this.formBuilder.array(
            //     this.dashboardOverrides.map(dashboardOverride => new BrowseDataTreeLevelDashboardOverrideEditorModel().fromModel(dashboardOverride).buildForm(contextParam, disabled))
            // , context.getValidation('dashboardOverrides').validators),
		});

		if(this.defaultDashboards){
			fg.addControl('defaultDashboards', 
				this.formBuilder.array(
					this.defaultDashboards.map(
						defaultDashboard => this.formBuilder.control(defaultDashboard) // todo validators
					)
				, context.getValidation('defaultDashboards').validators
				)
			);
		}

		if(this.dashboardOverrides){
			fg.addControl('dashboardOverrides', 
				this.formBuilder.array(
					this.dashboardOverrides.map(dashboardOverride => new BrowseDataTreeLevelDashboardOverrideEditorModel().fromModel(dashboardOverride).buildForm(contextParam, disabled))
				, context.getValidation('dashboardOverrides').validators
				)
			);
		}


		return fg;
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'field', validators: [] });
		baseValidationArray.push({ key: 'order', validators: [] });
		baseValidationArray.push({ key: 'supportSubLevel', validators: [] });
		baseValidationArray.push({ key: 'defaultDashboards', validators: [] });
		baseValidationArray.push({ key: 'dashboardOverrides', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


export class BrowseDataTreeLevelDashboardOverrideEditorModel implements BrowseDataTreeLevelDashboardOverrideModel{


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


	constructor() { }
	requirements: DataTreeLevelDashboardOverrideFieldRequirement[] = [];
	supportedDashboards: string[] = [];
	supportSubLevel: boolean;
    

	public fromModel(item: BrowseDataTreeLevelDashboardOverrideModel): BrowseDataTreeLevelDashboardOverrideEditorModel {
		if (item) {
			this.requirements = item.requirements ?? [];
			this.supportedDashboards = item.supportedDashboards ?? []
			this.supportSubLevel = !!item.supportSubLevel;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		const paramContext = context;
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			supportSubLevel: [{ value: this.supportSubLevel, disabled: disabled }, context.getValidation('supportSubLevel').validators],
			supportedDashboards: this.formBuilder.array(this.supportedDashboards.map(x => this.formBuilder.control(x)), context.getValidation('supportedDashboards').validators),
            requirements: this.formBuilder.array(
                this.requirements.map(x => new DataTreeLevelDashboardOverrideFieldRequirementEditorModel().fromModel(x).buildForm( paramContext, disabled))
            , context.getValidation('requirements').validators)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'supportSubLevel', validators: [] });
		baseValidationArray.push({ key: 'supportedDashboards', validators: [] });
		baseValidationArray.push({ key: 'requirements', validators: [] });
	
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}



export class DataTreeLevelDashboardOverrideFieldRequirementEditorModel implements DataTreeLevelDashboardOverrideFieldRequirement{


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


	constructor() { }
	field: string;
	value: string;

    

	public fromModel(item: DataTreeLevelDashboardOverrideFieldRequirement): DataTreeLevelDashboardOverrideFieldRequirementEditorModel {
		if (item) {
			this.field = item.field;
			this.value = item.value;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			field: [{ value: this.field, disabled: disabled }, context.getValidation('field').validators],
			value: [{ value: this.value, disabled: disabled }, context.getValidation('value').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'field', validators: [] });
		baseValidationArray.push({ key: 'value', validators: [] });
	
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


export class FieldModelEditorModel implements BrowseDataFieldModel{


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


	constructor() { }
	code: string;
	name: string;
    

	public fromModel(item: BrowseDataFieldModel): FieldModelEditorModel {
		if (item) {
			this.code = item.code;
			this.name = item.name
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		const contextParam = context;
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			code: [{ value: this.code, disabled: disabled }, context.getValidation('code').validators],
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		
		baseValidationArray.push({ key: 'code', validators: [] });
		baseValidationArray.push({ key: 'name', validators: [] });
	
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


export class ValueRangeEditorModel implements ValueRangeModel{


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


	constructor() { }
	min: number;
	max: number;


	public fromModel(item: ValueRangeModel): ValueRangeEditorModel {
		if (item) {
            this.min = item.min;
			this.max = item.max;

		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			min: [{ value: this.min, disabled: disabled }, context.getValidation('min').validators],
			max: [{ value: this.max, disabled: disabled }, context.getValidation('max').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'min', validators: [] });
		baseValidationArray.push({ key: 'max', validators: [] });
	
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}



export class AltTextEditorModel implements AltTextModel{


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


	constructor() { }
	lang: string;
	text: string;

	public fromModel(item: AltTextModel): AltTextEditorModel {
		if (item) {
			this.lang = item.lang;
			this.text = item.text;

		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			lang: [{ value: this.lang, disabled: disabled }, context.getValidation('lang').validators],
			text: [{ value: this.text, disabled: disabled }, context.getValidation('text').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'lang', validators: [] });
		baseValidationArray.push({ key: 'text', validators: [] });
	
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


export class OperatorEditorModel implements OperatorModel{


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


	constructor() { }
	op: string;
	

	public fromModel(item: OperatorModel): OperatorEditorModel {
		if (item) {
			this.op = item.op;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			op: [{ value: this.op, disabled: disabled }, context.getValidation('op').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'op', validators: [] });
	
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}