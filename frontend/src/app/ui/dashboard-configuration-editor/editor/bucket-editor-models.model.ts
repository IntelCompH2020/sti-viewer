//* Base editor

import { AbstractControl, FormBuilder, FormGroup, UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { BucketAggregateType } from "@app/core/enum/bucket-aggregate-type.enum";
import { DateInterval } from "@app/core/enum/date-interval.enum";
import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";
import { AggregationMetricHaving, AggregationMetricHavingOperator, AggregationMetricHavingType, AggregationMetricSort, Bucket, CompositeBucket, CompositeSource, DataHistogramBucket, NestedBucket, TermsBucket } from "@app/core/model/bucket/bucket.model";
import { Metric } from "@app/core/model/metic/metric.model";
import { ValidationErrorModel } from "@common/forms/validation/error-model/validation-error-model";
import { Validation, ValidationContext } from "@common/forms/validation/validation-context";
import { Moment } from "moment";
import { nameof } from "ts-simple-nameof";


const FORM_BUILDER = new FormBuilder();
interface AdditionalFieldBuilder{
	name: string;
	build: (parms: any, context?: ValidationContext) => AbstractControl
}

//  * Base bucket
export class BaseBucketConfigEditorModel implements Bucket{
	bucketSort: AggregationMetricSort;// TODO SORT FIELD
    type: BucketAggregateType;
    field: string;
    metrics: Metric[];
    having: AggregationMetricHaving;
    

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: Bucket): BaseBucketConfigEditorModel {
		if (item) {
           this.type = item.type;
           this.field = item.field;
           this.metrics = item.metrics;
           this.having = item.having;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const form =  this.formBuilder.group({

			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			field: [{ value: this.field, disabled: disabled }, context.getValidation('field').validators],
			// having: new AggregationMetricHavingConfigEditorModel().fromModel(this.having).buildForm(),
            // metrics: this.formBuilder.array(
            //     this.metrics.map(metric => new MetricConfigEditorModel().fromModel(metric).buildForm())
            // )
		});

		// * metrics
		if(this.metrics){
			form.addControl(
				'metrics',
				this.formBuilder.array(
					this.metrics.map(metric => new MetricConfigEditorModel().fromModel(metric).buildForm())
				)
			)
		}

		// * having
		if(this.having){
			form.addControl(
				'having', 
				new AggregationMetricHavingConfigEditorModel().fromModel(this.having).buildForm()
			)
		}

		return form;
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'type', validators: [] });
		baseValidationArray.push({ key: 'field', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}
// *AGGREGATION HAVING 
export class AggregationMetricHavingConfigEditorModel implements AggregationMetricHaving{
    field: string;
    metricAggregateType: MetricAggregateType;
    type: AggregationMetricHavingType;
    operator: AggregationMetricHavingOperator;
    value: number;
    

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item : AggregationMetricHaving): AggregationMetricHavingConfigEditorModel {
		if (item) {
            this.field = item.field;
            this.metricAggregateType = item.metricAggregateType;
            this.type = item.type;
            this.operator = item.operator;
            this.value = item.value;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({

			field: [{ value: this.field, disabled: disabled }, context.getValidation('field').validators],
			metricAggregateType: [{ value: this.metricAggregateType, disabled: disabled }, context.getValidation('metricAggregateType').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			operator: [{ value: this.operator, disabled: disabled }, context.getValidation('operator').validators],
			value: [{ value: this.value, disabled: disabled }, context.getValidation('value').validators],
			
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'field', validators: [] });
		baseValidationArray.push({ key: 'metricAggregateType', validators: [] });
		baseValidationArray.push({ key: 'type', validators: [] });
		baseValidationArray.push({ key: 'operator', validators: [] });
		baseValidationArray.push({ key: 'value', validators: [] });
		
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}


// * METRIC
export class MetricConfigEditorModel implements Metric{
    type: MetricAggregateType;
    field: string;

    

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item : Metric): MetricConfigEditorModel {
		if (item) {
            this.type = item.type;
            this.field = item.field;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			field: [{ value: this.field, disabled: disabled }, context.getValidation('field').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'field', validators: [] });
		baseValidationArray.push({ key: 'type', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}



// * TERMS BUCKET
export class TermsBucketConfigEditorModel extends BaseBucketConfigEditorModel implements TermsBucket{
	bucketSort: AggregationMetricSort;// TODO SORT FIELD
    order: "ASC" | "DESC";

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<TermsBucketConfigEditorModel>(x => x.order),
			build: (x: 'ASC' | 'DESC', context: ValidationContext) =>  FORM_BUILDER.control(x, context.getValidation(nameof<TermsBucketConfigEditorModel>(x => x.order)))
		},
    ];

	public fromModel(item: TermsBucket): TermsBucketConfigEditorModel {
        super.fromModel(item);
		if(item){
            this.order = item.order;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {

		if(!context){
			context = super.createValidationContext();
			const thisContext = TermsBucketConfigEditorModel.createValidationContext();
			context.validation = [...context.validation, ...thisContext.validation ]
		}


		const formGroup = super.buildForm(context, disabled);
		TermsBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name], context))
		})

		return formGroup;
	}

	// ** STATIC FUNCTIONS

	public static appendAdditionalFields(formGroup: FormGroup, context?: ValidationContext): void{
		if(!formGroup){
			return;
		}

		if(!context){
			context = TermsBucketConfigEditorModel.createValidationContext();
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		TermsBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			if(!valueKeys.includes(field.name)){
				formGroup.addControl(field.name, field.build(null, context));
			}
		})

	}

	public static cleanUpAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		TermsBucketConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
	public static createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: nameof<TermsBucketConfigEditorModel>(x => x.order) , validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


// * NESTED BUCKET
export class NestedBucketConfigEditorModel extends BaseBucketConfigEditorModel implements NestedBucket{
	bucketSort: AggregationMetricSort; // TODO SORT FIELD
    bucket: Bucket;

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<NestedBucketConfigEditorModel>(x => x.bucket),
			build: (x: Bucket, context: ValidationContext) =>  {
                switch(x?.type){
                    case BucketAggregateType.Composite:
                        return new CompositeBucketConfigEditorModel().fromModel(x as CompositeBucket ).buildForm();
                    case BucketAggregateType.DateHistogram:
                        return new DataHistogramBucketConfigEditorModel().fromModel(x as DataHistogramBucket ).buildForm();
                    case BucketAggregateType.Terms:
                        return new TermsBucketConfigEditorModel().fromModel(x as TermsBucket ).buildForm();
                    case BucketAggregateType.Nested:
                        return new NestedBucketConfigEditorModel().fromModel(x as NestedBucket ).buildForm();
                    default:
                        return new BaseBucketConfigEditorModel().fromModel(x).buildForm()
                }
            }
		},
    ];

	public fromModel(item: NestedBucket): NestedBucketConfigEditorModel {
        super.fromModel(item);
		if(item){
            this.bucket = item.bucket;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);
		NestedBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name]))
		})

		return formGroup;
	}


	// * STATIC
	public static appendAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		NestedBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			if(!valueKeys.includes(field.name)){
				formGroup.addControl(field.name, field.build(null));
			}
		})

	}

	public static cleanUpAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		NestedBucketConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
}
// * DataHistogramBucket
export class DataHistogramBucketConfigEditorModel extends BaseBucketConfigEditorModel implements DataHistogramBucket{
	bucketSort: AggregationMetricSort; // TODO SORT FIELD
    order: "ASC" | "DESC";
    interval: DateInterval;
    timezone: Moment;


	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<DataHistogramBucketConfigEditorModel>(x => x.order),
			build: (x: 'ASC' | 'DESC', context) =>  FORM_BUILDER.control(x, context?.getValidation(nameof<DataHistogramBucketConfigEditorModel>(x => x.order)))
		},
		{
			name: nameof<DataHistogramBucketConfigEditorModel>(x => x.interval),
			build: (x: DateInterval, context) =>  FORM_BUILDER.control(x, context?.getValidation(nameof<DataHistogramBucketConfigEditorModel>(x => x.order)))
		},
		{
			name: nameof<DataHistogramBucketConfigEditorModel>(x => x.timezone),
			build: (x: Moment, context) =>  FORM_BUILDER.control(x, context?.getValidation(nameof<DataHistogramBucketConfigEditorModel>(x => x.timezone)))
		},
    ];

	public fromModel(item: DataHistogramBucket): DataHistogramBucketConfigEditorModel {
        super.fromModel(item);
		if(item){
            this.order = item.order;
            this.interval = item.interval;
            this.timezone = item.timezone;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {


		if(!context){
			context = super.createValidationContext();
			const thisContext = DataHistogramBucketConfigEditorModel.createValidationContext();
			context.validation = [...context.validation, ...thisContext.validation ]
		}

		const formGroup = super.buildForm(context, disabled);
		DataHistogramBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name], context))
		})

		return formGroup;
	}


	// * STATIC

	public static createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: nameof<DataHistogramBucketConfigEditorModel>(x => x.order) , validators: [] });
		baseValidationArray.push({ key: nameof<DataHistogramBucketConfigEditorModel>(x => x.interval) , validators: [] });
		baseValidationArray.push({ key: nameof<DataHistogramBucketConfigEditorModel>(x => x.timezone) , validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

	public static appendAdditionalFields(formGroup: FormGroup, context?: ValidationContext): void{
		if(!formGroup){
			return;
		}

		if(!context){
			context = DataHistogramBucketConfigEditorModel.createValidationContext();
		}
		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		DataHistogramBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			if(!valueKeys.includes(field.name)){
				formGroup.addControl(field.name, field.build(null, context));
			}
		})

	}

	public static cleanUpAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		DataHistogramBucketConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
}

// * COMPOSITE
export class CompositeBucketConfigEditorModel extends BaseBucketConfigEditorModel implements CompositeBucket{
	bucketSort: AggregationMetricSort; // TODO SORT FIELD
    sources: CompositeSource[];
    dateHistogramSource: DataHistogramBucket;
    afterKey: Record<string, any>;

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<CompositeBucketConfigEditorModel>(x => x.sources),
			build: (x: CompositeSource[]) =>  FORM_BUILDER.array(
				x?.map(item => new CompositeSourceEditorModel().fromModel(item).buildForm() ) ?? []
			)
		},
		{
			name: nameof<CompositeBucketConfigEditorModel>(x => x.dateHistogramSource),
			build: (x: DataHistogramBucket) =>  new DataHistogramBucketConfigEditorModel().fromModel(x).buildForm()
		},
		{
			name: nameof<CompositeBucketConfigEditorModel>(x => x.afterKey),
			build: (x: Moment, context) =>  FORM_BUILDER.control( x ?? {},context?.getValidation(nameof<CompositeBucketConfigEditorModel>(x => x.afterKey)) )
		},
    ];

	public fromModel(item: CompositeBucket): CompositeBucketConfigEditorModel {
        super.fromModel(item);
		if(item){
            this.sources = item.sources;
            this.dateHistogramSource = item.dateHistogramSource;
            this.afterKey = item.afterKey;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {

		if(!context){
			context = super.createValidationContext();
			const thisContext = CompositeBucketConfigEditorModel.createValidationContext();
			context.validation = [...context.validation, ...thisContext.validation ]
		}

		const formGroup = super.buildForm(context, disabled);
		CompositeBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{

			if(!this[field.name]){
				return;
			}
			formGroup.addControl(field.name, field.build(this[field.name], context))
		})

		return formGroup;
	}


    // * STATIC 
	public static createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: nameof<CompositeBucketConfigEditorModel>(x => x.afterKey) , validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
	public static appendAdditionalFields(formGroup: FormGroup, context?: ValidationContext): void{
		if(!formGroup){
			return;
		}

		if(!context){
			context = CompositeBucketConfigEditorModel.createValidationContext();
		}
		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		CompositeBucketConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			if(!valueKeys.includes(field.name)){
				formGroup.addControl(field.name, field.build(null, context));
			}
		})

	}

	public static cleanUpAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		CompositeBucketConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
}

// * COMPOSITE source
export class CompositeSourceEditorModel  implements CompositeSource{
    field: string;
    order: "ASC" | "DESC";


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: CompositeSource): CompositeSourceEditorModel {
		if (item) {
            this.field = item.field;
            this.order = item.order;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({

			field: [{ value: this.field, disabled: disabled }, context.getValidation('field').validators],
			order: [{ value: this.order, disabled: disabled }, context.getValidation('order').validators],

		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'field', validators: [] });
		baseValidationArray.push({ key: 'order', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}

