import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { SearchConfiguration, SearchConfigurationDashboardFilters, SearchConfigurationDashboardStaticFilters, SearchConfigurationDashboardStaticKeywordFilter, SearchConfigurationStaticFilter, SearchConfigurationStaticKeywordFilter, SearchViewConfig, SearchViewConfigType, SearchViewFieldConfig } from "@app/ui/search/search-config.model";
import { ValidationErrorModel } from "@common/forms/validation/error-model/validation-error-model";
import { Validation, ValidationContext } from "@common/forms/validation/validation-context";
import { Guid } from "@common/types/guid";

export class SearchConfigurationEditorModel implements SearchConfiguration{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }

    id: string;
    dictinctField: string;
    searchFields: string[] = [];
    indicatorIds: Guid[] = [];
    staticFilters: SearchConfigurationStaticFilter;
    viewConfig: SearchViewConfig;
    supportedDashboards: string[] = [];
    dashboardFilters: SearchConfigurationDashboardFilters;
    

	public fromModel(item: SearchConfiguration): SearchConfigurationEditorModel {
		if (item) {
            this.id = item.id;
            this.dictinctField = item.dictinctField;
            this.searchFields = item.searchFields ?? [];
            this.indicatorIds = item.indicatorIds ?? [];
            this.staticFilters = item.staticFilters;
            this.viewConfig = item.viewConfig;
            this.supportedDashboards = item.supportedDashboards ?? [];
            this.dashboardFilters = item.dashboardFilters;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {

        const paramsContext = context;

		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			dictinctField: [{ value: this.dictinctField, disabled: disabled }, context.getValidation('dictinctField').validators],
			searchFields: this.formBuilder.array(
                this.searchFields.map(sf => this.formBuilder.control(sf, [])) // todo validators /disabeld
            , context.getValidation('searchFields').validators),
			indicatorIds: this.formBuilder.array(
                this.indicatorIds.map(indicatorId => this.formBuilder.control(indicatorId, [])) // todo validators
            , context.getValidation('indicatorIds').validators),
			staticFilters: new SearchConfigurationStaticFilterEditorModel().fromModel(this.staticFilters).buildForm(paramsContext, disabled),
			viewConfig: new SearchViewConfigEditorModel().fromModel(this.viewConfig).buildForm(paramsContext, disabled),
			supportedDashboards: this.formBuilder.array(
                this.supportedDashboards.map(sd => this.formBuilder.control(sd)) // todo validators, disabled
            , context.getValidation('supportedDashboards').validators),
			dashboardFilters: new SearchConfigurationDashboardFiltersEditorModel().fromModel(this.dashboardFilters).buildForm(paramsContext, disabled)
                    
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		baseValidationArray.push({ key: 'id', validators: [ ] });
		baseValidationArray.push({ key: 'dictinctField', validators: [] });
		baseValidationArray.push({ key: 'searchFields', validators: [] });
		baseValidationArray.push({ key: 'indicatorIds', validators: [] });
		// baseValidationArray.push({ key: 'staticFilters', validators: [] });
		// baseValidationArray.push({ key: 'viewConfig', validators: [] });
		baseValidationArray.push({ key: 'supportedDashboards', validators: [] });
		baseValidationArray.push({ key: 'dashboardFilters', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


//  * STATIC FILTERS
export class SearchConfigurationStaticFilterEditorModel implements SearchConfigurationStaticFilter{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }
    keywordsFilters: SearchConfigurationStaticKeywordFilter[] = [];

    

	public fromModel(item: SearchConfigurationStaticFilter): SearchConfigurationStaticFilterEditorModel {
		if (item) {
            this.keywordsFilters = item.keywordsFilters ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
        const paramsContext = context;
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			keywordsFilters: this.formBuilder.array(
                this.keywordsFilters.map( kf => new SearchConfigurationStaticKeywordFilterEditorModel().fromModel(kf).buildForm(paramsContext, disabled))
            , context.getValidation('keywordsFilters').validators),
            
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		baseValidationArray.push({ key: 'keywordsFilters', validators: [ ] });


		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

// * STATIC KEYWORD FILTER
export class SearchConfigurationStaticKeywordFilterEditorModel implements SearchConfigurationStaticKeywordFilter{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }

    field: string;
    value: string[] = [];
    

    

	public fromModel(item: SearchConfigurationStaticKeywordFilter): SearchConfigurationStaticKeywordFilterEditorModel {
		if (item) {
            this.field = item.field;
            this.value = item.value ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			field: [{ value: this.field, disabled: disabled }, context.getValidation('field').validators],
			value: this.formBuilder.array(
                this.value.map(val => this.formBuilder.control(val, []))//  todo validators
            , context.getValidation('value').validators),    
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		baseValidationArray.push({ key: 'field', validators: [ ] });
		baseValidationArray.push({ key: 'value', validators: [ ] });


		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

// * VIEW CONFIG
export class SearchViewConfigEditorModel implements SearchViewConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }
    fields: SearchViewFieldConfig[] = [];

    

	public fromModel(item: SearchViewConfig): SearchViewConfigEditorModel {
		if (item) {
            this.fields = item.fields ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
        const paramsContext = context;
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			fields: this.formBuilder.array(
                this.fields.map(field => new SearchViewFieldConfigEditorModel().fromModel(field).buildForm(paramsContext, disabled))
            , context.getValidation('fields').validators)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		baseValidationArray.push({ key: 'fields', validators: [ ] });


		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

// * ViewConfig
export class SearchViewFieldConfigEditorModel implements SearchViewFieldConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }

    title: string;
    code: string;
    type: SearchViewConfigType;
    dashboardFilterCode?: string;
    

    

	public fromModel(item: SearchViewFieldConfig): SearchViewFieldConfigEditorModel {
		if (item) {
            this.title = item.title;
            this.code = item.code;
            this.type = item.type;
            this.dashboardFilterCode = item.dashboardFilterCode;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			title: [{ value: this.title, disabled: disabled }, context.getValidation('title').validators],
			code: [{ value: this.code, disabled: disabled }, context.getValidation('code').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			dashboardFilterCode: [{ value: this.dashboardFilterCode, disabled: disabled }, context.getValidation('dashboardFilterCode').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		baseValidationArray.push({ key: 'title', validators: [ ] });
		baseValidationArray.push({ key: 'code', validators: [ ] });
		baseValidationArray.push({ key: 'type', validators: [ ] });
		baseValidationArray.push({ key: 'dashboardFilterCode', validators: [ ] });


		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

//* DASBOARD FILTERS
export class SearchConfigurationDashboardFiltersEditorModel implements SearchConfigurationDashboardFilters{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }    
    staticFilters: SearchConfigurationDashboardStaticFilters;

    

	public fromModel(item: SearchConfigurationDashboardFilters): SearchConfigurationDashboardFiltersEditorModel {
		if (item) {
            this.staticFilters = item.staticFilters;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
        const paramsContext = context;
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			staticFilters: new SearchConfigurationDashboardStaticFiltersEditorModel().fromModel(this.staticFilters).buildForm(paramsContext, disabled)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		// baseValidationArray.push({ key: 'staticFilters', validators: [ ] });


		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

//* STATIC KEYWORD FILTERS
export class SearchConfigurationDashboardStaticFiltersEditorModel implements SearchConfigurationDashboardStaticFilters{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }    
    keywordsFilters: SearchConfigurationDashboardStaticKeywordFilter[] = [];
    

    

	public fromModel(item: SearchConfigurationDashboardStaticFilters): SearchConfigurationDashboardStaticFiltersEditorModel {
		if (item) {
            this.keywordsFilters = item.keywordsFilters ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
        const paramsContext = context;
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			keywordsFilters: this.formBuilder.array(
                this.keywordsFilters.map(kf => new SearchConfigurationDashboardStaticKeywordFilterEditorModel().fromModel(kf).buildForm(paramsContext, disabled))
            , context.getValidation('keywordsFilters').validators)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		baseValidationArray.push({ key: 'keywordsFilters', validators: [ ] });


		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

//* KEYWORD FILTER
export class SearchConfigurationDashboardStaticKeywordFilterEditorModel implements SearchConfigurationDashboardStaticKeywordFilter{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }    

    field: string;
    value: string[] = [];
    

    

	public fromModel(item: SearchConfigurationDashboardStaticKeywordFilter): SearchConfigurationDashboardStaticKeywordFilterEditorModel {
		if (item) {
            this.field = item.field;
            this.value = item.value ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			field: [{ value: this.field, disabled: disabled }, context.getValidation('field').validators],
			value: this.formBuilder.array(
                this.value.map(val => this.formBuilder.control(val, [])) // todo validators , disabled
            ,context.getValidation('value').validators),
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        
		baseValidationArray.push({ key: 'field', validators: [ ] });
		baseValidationArray.push({ key: 'value', validators: [ ] });


		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}
