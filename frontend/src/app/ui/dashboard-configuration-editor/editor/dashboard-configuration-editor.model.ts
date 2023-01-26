import { AbstractControl, FormBuilder, FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { BucketAggregateType } from "@app/core/enum/bucket-aggregate-type.enum";
import { CompositeBucket, DataHistogramBucket, NestedBucket, TermsBucket } from "@app/core/model/bucket/bucket.model";
import { RawDataRequest } from "@app/core/query/indicator-point-report.lookup";
import { AreaZoom, AxisLabel, BaseIndicatorDashboardChartAxisConfig, BaseIndicatorDashboardChartConfig, BaseIndicatorDashboardGaugeConfig, ChartDownloadDataConfig, ChartDownloadImageConfig, ChartFilter, ChartFilterBase, ChartFilterType, ChartFilterValue, CommonDashboardItemConfiguration, ConnectionExtractor, ConnectionLimit, ConnectionLimitOrder, ConnectionLimitType, DashboardChartTagConfig, DashBoardSerieConfiguration, DashBoardSerieConfigurationNested, DashBoardSerieConfigurationNestedType, DashBoardSerieLabel, DashBoardSerieSplitSerie, DashBoardSerieValues, DataZoom, FieldFormatterConfig, FieldFormatterType, GaugesBlock, GaugeType, IndicatorConfigBucket, IndicatorConfigMetric, IndicatorDashboardBarChartConfig, IndicatorDashboardChart, IndicatorDashboardChartGroupConfig, IndicatorDashboardChartType, IndicatorDashboardChartXAxisConfig, IndicatorDashboardChartYAxisConfig, IndicatorDashboardConfig, IndicatorDashboardLineChartConfig, IndicatorDashboardMapChartConfig, IndicatorDashboardPieChartConfig, IndicatorDashboardPolarBarChartConfig, IndicatorDashboardSankeyChartConfig, IndicatorDashboardScatterChartConfig, IndicatorDashboardStaticFilters, IndicatorDashboardTabConfig, IndicatorDashboardTreeMapChartConfig, IndicatorFilterType, IndicatorKeywordFilter, LegendConfig, MapConfig, MapConfigLegentItem, PolarBarRadiusAxis, SeriesValueFormatter, SeriesValueFormatterType, SeriesValueNumberFormatter, TabBlockConfig, TabBlockType, TreeMapToolTip } from "@app/ui/indicator-dashboard/indicator-dashboard-config";
import { ValidationErrorModel } from "@common/forms/validation/error-model/validation-error-model";
import { ValidationContext, Validation } from "@common/forms/validation/validation-context";
import { Lookup } from "@common/model/lookup";
import { nameof } from "ts-simple-nameof";
import { BaseBucketConfigEditorModel, CompositeBucketConfigEditorModel, DataHistogramBucketConfigEditorModel, MetricConfigEditorModel, NestedBucketConfigEditorModel, TermsBucketConfigEditorModel } from "./bucket-editor-models.model";



interface AdditionalFieldBuilder{
	name: string;
	build: (parms: any) => AbstractControl
}

const FORM_BUILDER = new FormBuilder();

// * DASHBOARD - ROOT

export class DasboardConfigurationEditorModel implements IndicatorDashboardConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	constructor() { }
    id: string;
    tabs: IndicatorDashboardTabConfig[] = [];

	public fromModel(item: IndicatorDashboardConfig): DasboardConfigurationEditorModel {
		if (item) {
            this.id = item.id;
            this.tabs = item.tabs ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			tabs: this.formBuilder.array(
                this.tabs.map(tab => new IndicatorDashboardTabConfigModelEditorModel().fromModel(tab).buildForm())  // todo validation
            )
            
            
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [ Validators.required] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}



// * DASHBOARD - TAB GROUP


export class IndicatorDashboardTabConfigModelEditorModel implements IndicatorDashboardTabConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    name: string;
    chartGroups: TabBlockConfig[] = [];



    public fromModel(item: IndicatorDashboardTabConfig): IndicatorDashboardTabConfigModelEditorModel {
		if (item) {


            this.name = item.name;
            this.chartGroups = item.chartGroups ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			chartGroups:this.formBuilder.array(
                this.chartGroups.map( chartgroup => {

					switch(chartgroup.type){
						case TabBlockType.ChartGroup:
							return new IndicatorDashboardChartGroupConfigEditorModel().fromModel(chartgroup).buildForm()
						case TabBlockType.Gauge:
							return new IndicatorDashboardGaugeGroupConfigEditorModel().fromModel(chartgroup).buildForm()
						default:
							return null;

					}
				}).filter(x => !!x)
            )        
            
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'name', validators: [Validators.required] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// * DASHBOARD - GAUGE GROUP

export class IndicatorDashboardGaugeGroupConfigEditorModel implements GaugesBlock{
    name: string;
	type: TabBlockType.Gauge = TabBlockType.Gauge;
    gauges: BaseIndicatorDashboardGaugeConfig[] = [];

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: GaugesBlock): IndicatorDashboardGaugeGroupConfigEditorModel {
		if (item) {
            this.name = item.name;
            this.gauges = item.gauges ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			gauges: this.formBuilder.array(
                this.gauges.map(chart => {
					switch(chart.type){
						case GaugeType.ValueCard:
						default:
							return new BaseIndicatorDashboardGaugeConfigEditorModel().fromModel(chart).buildForm();
					}
				})
            )
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'name', validators: [] });
		baseValidationArray.push({ key: 'type', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}



// * DASHBOARD - GAUGE GROUP

export class BaseIndicatorDashboardGaugeConfigEditorModel implements BaseIndicatorDashboardGaugeConfig{
	type: GaugeType;
	name: string;
	description: string;


	series?: DashBoardSerieConfiguration[] = [];
	indicatorId: string;
	metrics?: IndicatorConfigMetric[];
	bucket?: IndicatorConfigBucket;
	staticFilters?: IndicatorDashboardStaticFilters;
	rawDataRequest?: RawDataRequest;
	tags?: DashboardChartTagConfig;
	labelSortKey: string;
	labelsTransform?: FieldFormatterConfig;
	reverseValues?: boolean;
	labelOverride?: string;


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: BaseIndicatorDashboardGaugeConfig): BaseIndicatorDashboardGaugeConfigEditorModel {
		if (item) {
			
			this.series = item.series ?? []; // done
            this.indicatorId = item.indicatorId;// done
			this.metrics = item.metrics; // done
			this.bucket = item.bucket;// done
			this.staticFilters = item.staticFilters; // done
			this.rawDataRequest = item.rawDataRequest; // done
			this.tags = item.tags;
			this.labelSortKey = item.labelSortKey; // done
			this.labelsTransform = item.labelsTransform; // done //todo check
			this.reverseValues = !!item.reverseValues; // done

			this.type = item.type; 
			this.name = item.name;
			this.labelOverride = item.labelOverride;
			this.description = item.description
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const form =  this.formBuilder.group({
			// name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			description: [{ value: this.description, disabled: disabled }, context.getValidation('description').validators],
			labelOverride: [{ value: this.labelOverride, disabled: disabled }, context.getValidation('labelOverride').validators],

			labelSortKey: [{ value: this.labelSortKey, disabled: disabled }, context.getValidation('labelSortKey').validators],
			reverseValues: [{ value: this.reverseValues, disabled: disabled }, context.getValidation('reverseValues').validators],
			labelsTransform: new BaseIndicatorDashboardFieldFormatterChartConfigEditorModel().fromModel(this.labelsTransform).buildForm(),
			indicatorId: [{ value: this.indicatorId, disabled: disabled }, context.getValidation('indicatorId').validators],
			series: this.formBuilder.array(
				this.series.map(serie => new BaseIndicatorDashboardSerieEditorModel().fromModel(serie).buildForm())
			)
		});


		// * metrics
		if(this.metrics){
			form.addControl('metrics', 
				this.formBuilder.array(
					this.metrics.map(metric => new MetricConfigEditorModel().fromModel(metric).buildForm())
				)
			);
		}
		// * bucket
		if(this.bucket){
			form.addControl(
				'bucket',
				(() => {
					const x = this.bucket;
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
				})()
			);
		}
		// * static filters
		if(this.staticFilters){
			form.addControl(
				'staticFilters', 
				new BaseIndicatorDashboardChartStaticFilterConfigEditorModel().fromModel(this.staticFilters).buildForm()
			)
		}
		// * static filters
		if(this.rawDataRequest){
			form.addControl(
				'rawDataRequest',
				new BaseIndicatorDashboardChartRawDataRequestConfigEditorModel().fromModel(this.rawDataRequest).buildForm()
			)
		}

		return form;
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'labelSortKey', validators: [] });
		baseValidationArray.push({ key: 'indicatorId', validators: [] });
		baseValidationArray.push({ key: 'reverseValues', validators: [] });
		baseValidationArray.push({ key: 'labelsTransform', validators: [] });
		baseValidationArray.push({ key: 'type', validators: [Validators.required] });
		baseValidationArray.push({ key: 'description', validators: [] });
		baseValidationArray.push({ key: 'name', validators: [] });
		baseValidationArray.push({ key: 'labelOverride', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}


export class DashboardChartTagConfigEditorModel implements DashboardChartTagConfig{

	attachedTags?:string[] = [];


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: DashboardChartTagConfig): DashboardChartTagConfigEditorModel {
		if (item) {
			this.attachedTags = item.attachedTags ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const form =  this.formBuilder.group({
			attachedTags: this.formBuilder.array(
				this.attachedTags.map(		
					tag => this.formBuilder.control(tag)
				)
			)
		});

		return form;
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'attachedTags', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}



// * DASHBOARD - CHART GROUP

export class IndicatorDashboardChartGroupConfigEditorModel implements IndicatorDashboardChartGroupConfig{
    name: string;
	type: TabBlockType.ChartGroup = TabBlockType.ChartGroup;
    charts: IndicatorDashboardChart[] = [];

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: IndicatorDashboardChartGroupConfig): IndicatorDashboardChartGroupConfigEditorModel {
		if (item) {
            this.name = item.name;
            this.charts = item.charts ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			charts: this.formBuilder.array(
                this.charts.map(chart => {
					switch(chart.type){
						case IndicatorDashboardChartType.Line:
							return new IndicatorDashboardLineChartConfigEditorModel().fromModel(chart as IndicatorDashboardLineChartConfig).buildForm();
						case IndicatorDashboardChartType.Bar:
							return new IndicatorDashboardBarChartConfigEditorModel().fromModel(chart as IndicatorDashboardBarChartConfig).buildForm();
						case IndicatorDashboardChartType.Pie:
							return new IndicatorDashboardPieChartConfigEditorModel().fromModel(chart as IndicatorDashboardPieChartConfig).buildForm();
						case IndicatorDashboardChartType.PolarBar:
							return new IndicatorDashboardPolarBarChartConfigEditorModel().fromModel(chart as IndicatorDashboardPolarBarChartConfig).buildForm();
						case IndicatorDashboardChartType.TreeMap:
							return new IndicatorDashboardPolarTreeMapChartConfigEditorModel().fromModel(chart as IndicatorDashboardTreeMapChartConfig).buildForm();
						case IndicatorDashboardChartType.Map:
							return new IndicatorDashboardPolarMapChartConfigEditorModel().fromModel(chart as IndicatorDashboardMapChartConfig).buildForm();
						case IndicatorDashboardChartType.Sankey:
							return new IndicatorDashboardSankeyChartConfigEditorModel().fromModel(chart as IndicatorDashboardSankeyChartConfig ).buildForm();
						default: 
							return new BaseIndicatorDashboardChartConfigEditorModel().fromModel(chart).buildForm();
					}
				})
            )
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'name', validators: [] });
		baseValidationArray.push({ key: 'type', validators: [] });
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}


// * CHARTS EDITORS -------     ------ - ---- -- -- 



//* Base editor


export class BaseIndicatorDashboardChartConfigEditorModel implements BaseIndicatorDashboardChartConfig{
    indicatorId: string;
    type: IndicatorDashboardChartType;
    chartName?: string;
    chartSubtitle?: string;
    reverseValues?: boolean;
    description: string;
    metrics?: IndicatorConfigMetric[];
    bucket?: IndicatorConfigBucket;
    legend?: LegendConfig;
    filters?: ChartFilter[] = [];
    chartDownloadImage?: ChartDownloadImageConfig;
    chartDownloadData?: ChartDownloadDataConfig;
    staticFilters?: IndicatorDashboardStaticFilters;
    rawDataRequest?: RawDataRequest;
    labelSortKey: string;
    labelsTransform?: FieldFormatterConfig;
	series?: DashBoardSerieConfiguration[] = [];
	tags?: DashboardChartTagConfig;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: BaseIndicatorDashboardChartConfig): BaseIndicatorDashboardChartConfigEditorModel {
		if (item) {
            this.indicatorId = item.indicatorId;
            this.type = item.type;
            this.chartName = item.chartName;
            this.chartSubtitle = item.chartSubtitle;
            this.reverseValues = item.reverseValues;
            this.description = item.description;
            this.metrics = item.metrics;
            this.bucket = item.bucket;
            this.legend = item.legend ;
            this.filters = item.filters ?? [];
            this.chartDownloadImage = item.chartDownloadImage;
            this.chartDownloadData = item.chartDownloadData;
            this.staticFilters = item.staticFilters;
            this.rawDataRequest = item.rawDataRequest;
            this.labelSortKey = item.labelSortKey;
            this.labelsTransform = item.labelsTransform;
			this.series = item.series ?? [];
			this.tags = item.tags;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const form = this.formBuilder.group({


			indicatorId: [{ value: this.indicatorId, disabled: disabled }, context.getValidation('indicatorId').validators],
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			chartName: [{ value: this.chartName, disabled: disabled }, context.getValidation('chartName').validators],
			chartSubtitle: [{ value: this.chartSubtitle, disabled: disabled }, context.getValidation('chartSubtitle').validators],
			reverseValues: [{ value: this.reverseValues, disabled: disabled }, context.getValidation('reverseValues').validators],
			description: [{ value: this.description, disabled: disabled }, context.getValidation('description').validators],
			// metrics: this.formBuilder.array(
			// 	this.metrics.map(metric => new MetricConfigEditorModel().fromModel(metric).buildForm())
			// ),
			// bucket: (() => {
			// 	const x = this.bucket;
			// 	switch(x?.type){
            //         case BucketAggregateType.Composite:
            //             return new CompositeBucketConfigEditorModel().fromModel(x as CompositeBucket ).buildForm();
            //         case BucketAggregateType.DateHistogram:
            //             return new DataHistogramBucketConfigEditorModel().fromModel(x as DataHistogramBucket ).buildForm();
            //         case BucketAggregateType.Terms:
            //             return new TermsBucketConfigEditorModel().fromModel(x as TermsBucket ).buildForm();
            //         case BucketAggregateType.Nested:
            //             return new NestedBucketConfigEditorModel().fromModel(x as NestedBucket ).buildForm();
            //         default:
            //             return new BaseBucketConfigEditorModel().fromModel(x).buildForm()
            //     }
			// })(),
			legend: new BaseIndicatorDashboardLegendChartConfigEditorModel().fromModel(this.legend).buildForm(),
			tags: new DashboardChartTagConfigEditorModel().fromModel(this.tags).buildForm(),
			filters: this.formBuilder.array(
				this.filters.map(filter => new BaseIndicatorDashboardFilterChartConfigEditorModel().fromModel(filter).buildForm()),
			),
			chartDownloadImage: [{ value: this.chartDownloadImage, disabled: disabled }, context.getValidation('chartDownloadImage').validators],
			chartDownloadData: [{ value: this.chartDownloadData, disabled: disabled }, context.getValidation('chartDownloadData').validators],
			
			// staticFilters: new BaseIndicatorDashboardChartStaticFilterConfigEditorModel().fromModel(this.staticFilters).buildForm(),
			// rawDataRequest: new BaseIndicatorDashboardChartRawDataRequestConfigEditorModel().fromModel(this.rawDataRequest).buildForm(),
			labelSortKey: [{ value: this.labelSortKey, disabled: disabled }, context.getValidation('labelSortKey').validators],
			labelsTransform: [{ value: this.labelsTransform, disabled: disabled }, context.getValidation('labelsTransform').validators],
			series: this.formBuilder.array(
				this.series.map(serie => new BaseIndicatorDashboardSerieEditorModel().fromModel(serie).buildForm())
			)
		});

		// * metrics
		if(this.metrics){
			form.addControl('metrics', 
				this.formBuilder.array(
					this.metrics.map(metric => new MetricConfigEditorModel().fromModel(metric).buildForm())
				)
			);
		}
		// * bucket
		if(this.bucket){
			form.addControl(
				'bucket',
				(() => {
					const x = this.bucket;
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
				})()
			);
		}
		// * static filters
		if(this.staticFilters){
			form.addControl(
				'staticFilters', 
				new BaseIndicatorDashboardChartStaticFilterConfigEditorModel().fromModel(this.staticFilters).buildForm()
			)
		}
		// * static filters
		if(this.rawDataRequest){
			form.addControl(
				'rawDataRequest',
				new BaseIndicatorDashboardChartRawDataRequestConfigEditorModel().fromModel(this.rawDataRequest).buildForm()
			)
		}
		




		return form;

	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'indicatorId', validators: [Validators.required] });
		baseValidationArray.push({ key: 'type', validators: [Validators.required] });
		baseValidationArray.push({ key: 'chartName', validators: [] });
		baseValidationArray.push({ key: 'chartSubtitle', validators: [] });
		baseValidationArray.push({ key: 'reverseValues', validators: [] });
		baseValidationArray.push({ key: 'description', validators: [] });
		baseValidationArray.push({ key: 'chartDownloadImage', validators: [] });
		baseValidationArray.push({ key: 'chartDownloadData', validators: [] });
		baseValidationArray.push({ key: 'chartDownloadData', validators: [] });
		baseValidationArray.push({ key: 'labelSortKey', validators: [] });
		baseValidationArray.push({ key: 'labelsTransform', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// * Static filter
export class BaseIndicatorDashboardChartStaticFilterConfigEditorModel implements IndicatorDashboardStaticFilters{

	keywordsFilters?: IndicatorKeywordFilter[] = [];

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: IndicatorDashboardStaticFilters): BaseIndicatorDashboardChartStaticFilterConfigEditorModel {
		if (item) {
			this.keywordsFilters = item.keywordsFilters ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			keywordsFilters: this.formBuilder.array(
				this.keywordsFilters.map(keywordFilter => new BaseIndicatorDashboardChartKeywordFilterConfigEditorModel().fromModel(keywordFilter).buildForm())
			)
		
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// * KeywordFilter
export class BaseIndicatorDashboardChartKeywordFilterConfigEditorModel implements IndicatorKeywordFilter{

	field: string;
	value: string[] = [];


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: IndicatorKeywordFilter): BaseIndicatorDashboardChartKeywordFilterConfigEditorModel {
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
				this.value.map(val => this.formBuilder.control(val)),
				context.getValidation('value')
			)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'field', validators: [Validators.required,] });
		baseValidationArray.push({ key: 'value', validators: [Validators.minLength(1) ] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}
// * rawDataRequest
export class BaseIndicatorDashboardChartRawDataRequestConfigEditorModel implements RawDataRequest{
	keyField: string;
	valueField: string;
	page?: Lookup.Paging;
	order: Lookup.Ordering;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: RawDataRequest): BaseIndicatorDashboardChartRawDataRequestConfigEditorModel {
		if (item) {
			this.keyField = item.keyField;
			this.valueField = item.valueField;
			this.page = item.page;
			this.order = item.order;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({

			keyField: [{ value: this.keyField, disabled: disabled }, context.getValidation('keyField').validators],
			valueField: [{ value: this.valueField, disabled: disabled }, context.getValidation('valueField').validators],
			page: new PagingConfigEditorModel().fromModel(this.page).buildForm(),
			order: new OrderingConfigEditorModel().fromModel(this.order).buildForm()

		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'keyField', validators: [] });
		baseValidationArray.push({ key: 'valueField', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// * PAGING
export class PagingConfigEditorModel implements Lookup.Paging{
	offset: number;
	size: number;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: Lookup.Paging): PagingConfigEditorModel {
		if (item) {
			this.offset = item.offset;
			this.size = item.size;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({

			offset: [{ value: this.offset, disabled: disabled }, context.getValidation('offset').validators],
			size: [{ value: this.size, disabled: disabled }, context.getValidation('size').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'offset', validators: [] });
		baseValidationArray.push({ key: 'size', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

//  * ORDERING
export class OrderingConfigEditorModel implements Lookup.Ordering{
	items: string[] = [];

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: Lookup.Ordering): OrderingConfigEditorModel {
		if (item) {
			this.items = item.items ?? [];	
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			items: this.formBuilder.array(
				this.items?.map(item => this.formBuilder.control(item))
			)
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// * LINE CHART
export class IndicatorDashboardLineChartConfigEditorModel extends BaseIndicatorDashboardChartConfigEditorModel implements IndicatorDashboardLineChartConfig{
	xAxis: IndicatorDashboardChartXAxisConfig;
	yAxis: IndicatorDashboardChartYAxisConfig;
	stack?: boolean;
	horizontal?: boolean;
	areaStyle?: object;
	dataZoom?: DataZoom;

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<IndicatorDashboardLineChartConfigEditorModel>(x => x.xAxis),
			build: (xAxis: IndicatorDashboardChartXAxisConfig) =>  new BaseIndicatorDashboardXAxisChartConfigEditorModel().fromModel(xAxis).buildForm()
		},
		{
			name: nameof<IndicatorDashboardLineChartConfigEditorModel>(x => x.yAxis),
			build: (yAxis: IndicatorDashboardChartYAxisConfig) => new BaseIndicatorDashboardYAxisChartConfigEditorModel().fromModel(yAxis).buildForm()
		},
		{
			name: nameof<IndicatorDashboardLineChartConfigEditorModel>(x => x.stack),
			build: (stack: boolean) => FORM_BUILDER.control(stack)
		},
		{
			name: nameof<IndicatorDashboardLineChartConfigEditorModel>(x => x.horizontal),
			build: (horizontal: boolean) => FORM_BUILDER.control(horizontal)
		},
		{
			name: nameof<IndicatorDashboardLineChartConfigEditorModel>(x => x.areaStyle),
			build: (areaStyle: any) => FORM_BUILDER.control(areaStyle)
		},
		{
			name: nameof<IndicatorDashboardLineChartConfigEditorModel>(x => x.dataZoom),
			build: (dataZoom: DataZoom) => new BaseIndicatorDashboardDataZoomChartConfigEditorModel().fromModel(dataZoom).buildForm()
		}];

	public fromModel(item: IndicatorDashboardLineChartConfig): IndicatorDashboardLineChartConfigEditorModel {
		if(item){
			super.fromModel(item);
			this.xAxis = item.xAxis;
			this.yAxis = item.yAxis;
			this.stack = !!item.stack;
			this.horizontal = !!item.horizontal;
			this.areaStyle = item.areaStyle;
			this.dataZoom = item.dataZoom;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);

		IndicatorDashboardLineChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name]))
		})

		return formGroup;
	}



	public static appendAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		IndicatorDashboardLineChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
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

		IndicatorDashboardLineChartConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
}
	

// * SANKEY CHART
export class IndicatorDashboardSankeyChartConfigEditorModel extends BaseIndicatorDashboardChartConfigEditorModel implements IndicatorDashboardSankeyChartConfig{
	connectionExtractor: ConnectionExtractor;

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<IndicatorDashboardSankeyChartConfigEditorModel>(x => x.connectionExtractor),
			build: (x: ConnectionExtractor) => new ConnectionExtractorEditorModel().fromModel(x).buildForm()
		}];

	public fromModel(item: IndicatorDashboardSankeyChartConfig): IndicatorDashboardSankeyChartConfigEditorModel {
		super.fromModel(item);
		if(item){
			this.connectionExtractor = item.connectionExtractor;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);

		IndicatorDashboardSankeyChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name]))
		})

		return formGroup;
	}



	public static appendAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		IndicatorDashboardSankeyChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
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

		IndicatorDashboardSankeyChartConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
}
	
// * PIE CHART
export class IndicatorDashboardPieChartConfigEditorModel extends BaseIndicatorDashboardChartConfigEditorModel implements IndicatorDashboardPieChartConfig{
	roseType?: string;

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<IndicatorDashboardPieChartConfigEditorModel>(x => x.roseType),
			build: (rosetype: string) => FORM_BUILDER.control(rosetype)
		}
	];

	public fromModel(item: IndicatorDashboardPieChartConfig): IndicatorDashboardPieChartConfigEditorModel {
		super.fromModel(item);
		if(item){
			this.roseType = item.roseType;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);
		IndicatorDashboardPieChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name]))
		})

		return formGroup;
	}



	public static appendAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		IndicatorDashboardPieChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
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

		IndicatorDashboardPieChartConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
	
}


// * Polar Bar CHART
export class IndicatorDashboardPolarBarChartConfigEditorModel extends BaseIndicatorDashboardChartConfigEditorModel implements IndicatorDashboardPolarBarChartConfig{
	dataZoom?: DataZoom;
	radiusAxis: PolarBarRadiusAxis;
	

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<IndicatorDashboardPolarBarChartConfigEditorModel>(x => x.dataZoom),
			build: (x: DataZoom) => new BaseIndicatorDashboardDataZoomChartConfigEditorModel().fromModel(x).buildForm()
		},
		{
			name: nameof<IndicatorDashboardPolarBarChartConfigEditorModel>(x => x.radiusAxis),
			build: (x: PolarBarRadiusAxis) => FORM_BUILDER.group(x??{name: ''})
		}
	];

	public fromModel(item: IndicatorDashboardPolarBarChartConfig): IndicatorDashboardPolarBarChartConfigEditorModel {
		super.fromModel(item);
		if(item){
			this.dataZoom = item.dataZoom;
			this.radiusAxis = item.radiusAxis;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);
		IndicatorDashboardPolarBarChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name]))
		})

		return formGroup;
	}



	public static appendAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		IndicatorDashboardPolarBarChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
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

		IndicatorDashboardPolarBarChartConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
	
}

// * TREE MAP
export class IndicatorDashboardPolarTreeMapChartConfigEditorModel extends BaseIndicatorDashboardChartConfigEditorModel implements IndicatorDashboardTreeMapChartConfig{
	treeName?: string;
	toolTip: TreeMapToolTip;
	treeColors: Record<string, string>;

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<IndicatorDashboardPolarTreeMapChartConfigEditorModel>(x => x.toolTip),
			build: (x: TreeMapToolTip) => FORM_BUILDER.group(!x ? {metricName: null, name: null}: {metricName: null, name: null, ...x})
		},
		{
			name: nameof<IndicatorDashboardPolarTreeMapChartConfigEditorModel>(x => x.treeName),
			build: (x: string) => FORM_BUILDER.control(x)
		},
		{
			name: nameof<IndicatorDashboardPolarTreeMapChartConfigEditorModel>(x => x.treeColors),
			build: (x: Record<string, string>) => FORM_BUILDER.control(x ?? {})
		}
	];

	public fromModel(item: IndicatorDashboardTreeMapChartConfig): IndicatorDashboardPolarTreeMapChartConfigEditorModel {
		super.fromModel(item);
		if(item){
			this.treeName = item.treeName;
			this.toolTip = item.toolTip;
			this.treeColors = item.treeColors;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);

		IndicatorDashboardPolarTreeMapChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name]))
		})

		return formGroup;
	}



	public static appendAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		IndicatorDashboardPolarTreeMapChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
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

		IndicatorDashboardPolarTreeMapChartConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
	
}
// * MAP
export class IndicatorDashboardPolarMapChartConfigEditorModel extends BaseIndicatorDashboardChartConfigEditorModel implements IndicatorDashboardMapChartConfig{
	mapChartConfig: MapConfig;
	countryNameMapping?: Record<string, string>;

	public static readonly ADDITIONAL_FIELDS: AdditionalFieldBuilder[] = [
		{
			name: nameof<IndicatorDashboardPolarMapChartConfigEditorModel>(x => x.mapChartConfig),
			build: (x: MapConfig) => new IndicatorDashboardMapChartMapConfigItemEditorModel().fromModel(x).buildForm()
		},
		{
			name: nameof<IndicatorDashboardPolarMapChartConfigEditorModel>(x => x.countryNameMapping),
			build: (x: Record<string, string>) => FORM_BUILDER.control(x ?? {})
		}
	];

	public fromModel(item: IndicatorDashboardMapChartConfig): IndicatorDashboardPolarMapChartConfigEditorModel {
		super.fromModel(item);
		if(item){
			this.mapChartConfig = item.mapChartConfig;
			this.countryNameMapping = item.countryNameMapping;
		}

		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);

		IndicatorDashboardPolarMapChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
			formGroup.addControl(field.name, field.build(this[field.name]))
		})

		return formGroup;
	}



	public static appendAdditionalFields(formGroup: FormGroup): void{
		if(!formGroup){
			return;
		}

		const value = formGroup.value;
		const valueKeys = Object.keys(value);

		IndicatorDashboardPolarMapChartConfigEditorModel.ADDITIONAL_FIELDS.forEach(field =>{
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

		IndicatorDashboardPolarMapChartConfigEditorModel.ADDITIONAL_FIELDS.map(x => x.name).forEach(field =>{
			if(valueKeys.includes(field)){
				formGroup.removeControl(field);
			}
		})

	}
	
}

export class ConnectionExtractorEditorModel implements ConnectionExtractor{
	sourceKeyExtractor: string;
	targetKeyExtractor: string;
	valueKeyExtractor: string;
	valueTests?: Record<string, string> = {};
	groupTests?: Record<string, string> = {};
	limit?: ConnectionLimit;
	
    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: ConnectionExtractor): ConnectionExtractorEditorModel {
		if (item) {
			this.sourceKeyExtractor = item.sourceKeyExtractor;
			this.targetKeyExtractor = item.targetKeyExtractor;
			this.valueKeyExtractor = item.valueKeyExtractor;
			this.valueTests = item.valueTests ?? {};
			this.groupTests = item.groupTests ?? {};
			this.limit = item.limit;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const form =  this.formBuilder.group({
			sourceKeyExtractor: [{ value: this.sourceKeyExtractor, disabled: disabled }, context.getValidation('sourceKeyExtractor').validators],
			targetKeyExtractor: [{ value: this.targetKeyExtractor, disabled: disabled }, context.getValidation('targetKeyExtractor').validators],
			valueKeyExtractor: [{ value: this.valueKeyExtractor, disabled: disabled }, context.getValidation('valueKeyExtractor').validators],
			valueTests: [{ value: this.valueTests, disabled: disabled }, context.getValidation('valueTests').validators],
			groupTests: [{ value: this.groupTests, disabled: disabled }, context.getValidation('groupTests').validators],
		});


		if(this.limit){
			form.addControl(nameof<ConnectionExtractorEditorModel>(x => x.limit), 
			 new ConnectionLimitEditorModel().fromModel(this.limit).buildForm()
			)
		}

		return form;
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'sourceKeyExtractor', validators: [] });
		baseValidationArray.push({ key: 'targetKeyExtractor', validators: [] });
		baseValidationArray.push({ key: 'valueKeyExtractor', validators: [] });
		baseValidationArray.push({ key: 'valueTests', validators: [] });
		baseValidationArray.push({ key: 'groupTests', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

export class ConnectionLimitEditorModel implements ConnectionLimit{
	type: ConnectionLimitType;
	order: ConnectionLimitOrder;
	count: number;
	
    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: ConnectionLimit): ConnectionLimitEditorModel {
		if (item) {
			this.type = item.type;
			this.order = item.order;
			this.count = item.count;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			order: [{ value: this.order, disabled: disabled }, context.getValidation('order').validators],
			count: [{ value: this.count, disabled: disabled }, context.getValidation('count').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'type', validators: [Validators.required] });
		baseValidationArray.push({ key: 'order', validators: [Validators.required] });
		baseValidationArray.push({ key: 'count', validators: [Validators.required, Validators.min(1)] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}


export class IndicatorDashboardMapChartMapConfigItemEditorModel implements MapConfig{
	high?: MapConfigLegentItem;
	low?: MapConfigLegentItem;
    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: MapConfig): IndicatorDashboardMapChartMapConfigItemEditorModel {
		if (item) {
			this.high = item.high;
			this.low =item.low;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			high: this.formBuilder.group(this.high ?? {color: null, text: null}),
			low: this.formBuilder.group(this.low ?? {color: null, text: null})
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}


// * BAR CHART
export class IndicatorDashboardBarChartConfigEditorModel extends IndicatorDashboardLineChartConfigEditorModel {
	

	public fromModel(item: IndicatorDashboardBarChartConfig): IndicatorDashboardBarChartConfigEditorModel {
		super.fromModel(item);
		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		return super.buildForm(context, disabled);
	}

	public static cleanUpAdditionalFields(formGroup: FormGroup<any>): void {
		IndicatorDashboardLineChartConfigEditorModel.cleanUpAdditionalFields(formGroup);
	}
	public static appendAdditionalFields(formGroup: FormGroup<any>): void {
		IndicatorDashboardLineChartConfigEditorModel.appendAdditionalFields(formGroup);
	}
}

// * BASE FILTER

export class BaseIndicatorDashboardFilterChartConfigEditorModel implements ChartFilterBase{

	type: ChartFilterType;
	name: string;
	fieldCode: string;
	values: ChartFilterValue[] = [];
	indicatorFilterType: IndicatorFilterType;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: ChartFilterBase): BaseIndicatorDashboardFilterChartConfigEditorModel {
		if (item) {
			this.type = item.type;
			this.name = item.name;
			this.fieldCode = item.fieldCode;
			this.values = item.values ?? [];
			this.indicatorFilterType = item.indicatorFilterType;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			fieldCode: [{ value: this.fieldCode, disabled: disabled }, context.getValidation('fieldCode').validators],
			values: this.formBuilder.array(
				this.values.map(val => this.formBuilder.group(val))
			),
			indicatorFilterType: [{ value: this.indicatorFilterType, disabled: disabled }, context.getValidation('indicatorFilterType').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'name', validators: [] });
		baseValidationArray.push({ key: 'type', validators: [] });
		baseValidationArray.push({ key: 'fieldCode', validators: [] });
		baseValidationArray.push({ key: 'indicatorFilterType', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// * LEGEND

export class BaseIndicatorDashboardLegendChartConfigEditorModel implements LegendConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: LegendConfig): BaseIndicatorDashboardLegendChartConfigEditorModel {
		if (item) {

		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({

		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}
// * BASE AXIS
export class BaseIndicatorDashboardBaseAxisChartConfigEditorModel implements BaseIndicatorDashboardChartAxisConfig{
	name: string;
	boundaryGap?: boolean;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: BaseIndicatorDashboardChartAxisConfig): BaseIndicatorDashboardBaseAxisChartConfigEditorModel {
		if (item) {
			this.name = item.name;
			this.boundaryGap = item.boundaryGap;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			boundaryGap: [{ value: this.boundaryGap, disabled: disabled }, context.getValidation('boundaryGap').validators],

		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'name', validators: [] });
		baseValidationArray.push({ key: 'boundaryGap', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

//*  yAxis
export class BaseIndicatorDashboardYAxisChartConfigEditorModel extends BaseIndicatorDashboardBaseAxisChartConfigEditorModel implements IndicatorDashboardChartYAxisConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();



}

//*  xAxis
export class BaseIndicatorDashboardXAxisChartConfigEditorModel extends BaseIndicatorDashboardBaseAxisChartConfigEditorModel implements IndicatorDashboardChartXAxisConfig{
	axisLabel?: AxisLabel;
    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	public fromModel(item: IndicatorDashboardChartXAxisConfig): BaseIndicatorDashboardXAxisChartConfigEditorModel {
		if(item){
			this.axisLabel = item.axisLabel;
		}
		return this;
	}

	public buildForm(context?: ValidationContext, disabled?: boolean): UntypedFormGroup {
		const formGroup = super.buildForm(context, disabled);
		

		if(this.axisLabel){
			const {rotate, width} =this.axisLabel;
			formGroup.addControl(
				'axisLabel', this.formBuilder.group({rotate, width})
			)
		}

		return formGroup;
	}


}

// * DATA ZOOM
export class BaseIndicatorDashboardDataZoomChartConfigEditorModel implements DataZoom{
	inside?: boolean;
	slider?: boolean;
	areaZoom?: AreaZoom;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: DataZoom): BaseIndicatorDashboardDataZoomChartConfigEditorModel {
		if (item) {
			this.inside = !!item.inside;
			this.slider = !!item.slider;
			this.areaZoom = item.areaZoom;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		const formGroup =  this.formBuilder.group({
			inside: [{ value: this.inside, disabled: disabled }, context.getValidation('inside').validators], 
			slider: [{ value: this.slider, disabled: disabled }, context.getValidation('slider').validators],
		});

		if(this.areaZoom){
			const {end, start} = this.areaZoom;
			formGroup.addControl(nameof<BaseIndicatorDashboardDataZoomChartConfigEditorModel>(x => x.areaZoom), this.formBuilder.group({end,start}));
		}

		return formGroup;
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'inside', validators: [] });
		baseValidationArray.push({ key: 'slider', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}


// *** DOWNLOAD IMAGE EDITOR

export class BaseIndicatorDashboardDownloadImageChartConfigEditorModel implements ChartDownloadImageConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: ChartDownloadImageConfig): BaseIndicatorDashboardDownloadImageChartConfigEditorModel {
		if (item) {

		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({

		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// *  download DATA
export class BaseIndicatorDashboardDownloadDataChartConfigEditorModel implements ChartDownloadDataConfig{

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: ChartDownloadDataConfig): BaseIndicatorDashboardDownloadDataChartConfigEditorModel {
		if (item) {

		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({

		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}



// *  filed formatter
export class BaseIndicatorDashboardFieldFormatterChartConfigEditorModel implements FieldFormatterConfig{
	type: FieldFormatterType;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: FieldFormatterConfig): BaseIndicatorDashboardFieldFormatterChartConfigEditorModel {
		if (item) {
			this.type = item.type
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'type', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

//     ******** SERIE  ********

// *  Serie editor model
export class BaseIndicatorDashboardSerieEditorModel implements DashBoardSerieConfiguration{
	splitSeries?: DashBoardSerieSplitSerie[] = [];
	label: DashBoardSerieLabel;
	values: DashBoardSerieValues;
	nested: DashBoardSerieConfigurationNested;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: DashBoardSerieConfiguration): BaseIndicatorDashboardSerieEditorModel {
		if (item) {
			this.splitSeries = item.splitSeries ?? [];
			this.label = item.label;
			this.values = item.values;
			this.nested = item.nested;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			splitSeries: this.formBuilder.array(
				this.splitSeries.map(splitSerie => new BaseIndicatorDashboardSerieSplitSerieEditorModel().fromModel(splitSerie).buildForm())
			),
			label: new BaseIndicatorDashboardSerieLabelEditorModel().fromModel(this.label).buildForm(),
			values: new BaseIndicatorDashboardSerieValuesEditorModel().fromModel(this.values).buildForm(),
			nested: new BaseIndicatorDashboardSerieValuesNestedEditorModel().fromModel(this.nested).buildForm()
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

//  * SERIE LABEL
export class BaseIndicatorDashboardSerieLabelEditorModel implements DashBoardSerieLabel{
	color?: string;
	name: string;
	labelKey: string;


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: DashBoardSerieLabel): BaseIndicatorDashboardSerieLabelEditorModel {
		if (item) {
			this.color = item.color;
			this.name = item.name;
			this.labelKey = item.labelKey;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			labelKey: [{ value: this.labelKey, disabled: disabled }, context.getValidation('labelKey').validators],
			name: [{ value: this.name, disabled: disabled }, context.getValidation('name').validators],
			color: [{ value: this.color, disabled: disabled }, context.getValidation('color').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'name', validators: [] });
		baseValidationArray.push({ key: 'labelKey', validators: [] });
		baseValidationArray.push({ key: 'color', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

//  * Split serie
export class BaseIndicatorDashboardSerieSplitSerieEditorModel implements DashBoardSerieSplitSerie{
	key: string;


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: DashBoardSerieSplitSerie): BaseIndicatorDashboardSerieSplitSerieEditorModel {
		if (item) {
			this.key = item.key
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			key: [{ value: this.key, disabled: disabled }, context.getValidation('key').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
        //TODO
		baseValidationArray.push({ key: 'key', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}



//  * Split serie
export class BaseIndicatorDashboardSerieValuesEditorModel implements DashBoardSerieValues{
	formatter?: SeriesValueFormatter;
	valueKey: string;
	tests?: Record<string, string>[] = [];
	groupTests?: Record<string, string>[] = [];


    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: DashBoardSerieValues): BaseIndicatorDashboardSerieValuesEditorModel {
		if (item) {
			this.formatter = item.formatter;
			this.valueKey = item.valueKey;
			this.tests = item.tests ?? [];
			this.groupTests = item.groupTests ?? [];
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			valueKey: [{ value: this.valueKey, disabled: disabled }, context.getValidation('valueKey').validators],
			tests: [{ value: this.tests, disabled: disabled }, context.getValidation('tests').validators],
			groupTests: [{ value: this.groupTests, disabled: disabled }, context.getValidation('groupTests').validators],
			formatter : ((): FormGroup => {
				switch(this.formatter?.type){
					case SeriesValueFormatterType.Number:
						return new BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel().fromModel(this.formatter).buildForm();
					default:
						return new BaseIndicatorDashboardSerieValuesFormatterEditorModel().fromModel(this.formatter).buildForm();
				}
			})(),
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'valueKey', validators: [] });
		baseValidationArray.push({ key: 'tests', validators: [] });
		baseValidationArray.push({ key: 'groupTests', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}
export class BaseIndicatorDashboardSerieValuesNestedEditorModel implements DashBoardSerieConfigurationNested{
	type?: DashBoardSerieConfigurationNestedType;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: DashBoardSerieConfigurationNested): BaseIndicatorDashboardSerieValuesNestedEditorModel {
		if (item) {
			this.type = item.type;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();

		baseValidationArray.push({ key: 'type', validators: [] });
		

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}

// *  Formatter
export class BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel implements SeriesValueNumberFormatter{
	type: SeriesValueFormatterType = SeriesValueFormatterType.Number;
	decimalAccuracy?: number;

	public static readonly ADDITIONAL_FIELDS: string[] = ['decimalAccuracy'];

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: SeriesValueNumberFormatter): BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel {
		if (item) {
			this.decimalAccuracy = item.decimalAccuracy;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
			decimalAccuracy: [{ value: this.decimalAccuracy, disabled: disabled }, context.getValidation('decimalAccuracy').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'type', validators: [] });
		baseValidationArray.push({ key: 'decimalAccuracy', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}


	public static appendNumberFormatterControls(formGroup: FormGroup):void {

		if(!formGroup) return;

        const value = formGroup.value;
        const valueKeys = Object.keys(value);
		const formBuilder = new FormBuilder();

        BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel.ADDITIONAL_FIELDS.forEach(field => {
            if(!valueKeys.includes(field)){
                formGroup.addControl(field, formBuilder.control(null)) // todo maybe validators
            }
        })
	}

	public static cleanUpNumberFormatterControls(formGroup: FormGroup):void {
		if(!formGroup) return;

        const value = formGroup.value;
        const valueKeys = Object.keys(value);

        BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel.ADDITIONAL_FIELDS.forEach(field => {
            if(valueKeys.includes(field)){
                formGroup.removeControl(field)
            }
        })
	}

}


//  * Split serie
export class BaseIndicatorDashboardSerieValuesFormatterEditorModel implements SeriesValueFormatter{
	type: SeriesValueFormatterType;

    public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();


    public fromModel(item: SeriesValueFormatter): BaseIndicatorDashboardSerieValuesFormatterEditorModel {
		if (item) {
			this.type = item.type;
		}
		return this;
	}
	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			type: [{ value: this.type, disabled: disabled }, context.getValidation('type').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'type', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}

}