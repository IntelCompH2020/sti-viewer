import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { AbstractControl, FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { MatSelectChange } from "@angular/material/select";
import { BucketAggregateType } from "@app/core/enum/bucket-aggregate-type.enum";
import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";
import { AggregationMetricHavingOperator, AggregationMetricHavingType } from "@app/core/model/bucket/bucket.model";
import { CompositeBucketConfigEditorModel, DataHistogramBucketConfigEditorModel, MetricConfigEditorModel, NestedBucketConfigEditorModel, TermsBucketConfigEditorModel , AggregationMetricHavingConfigEditorModel} from "../../../bucket-editor-models.model";

@Component({
    templateUrl: './bucket-config-editor.component.html',
    selector: 'bucket-config-editor'
})
export class BucketConfigEditorComponent implements OnChanges{

    @Input()
    formGroup: FormGroup;

    BucketAggregateType = BucketAggregateType;
    BucketAggregateTypes = this.enumUtils.getEnumValues<BucketAggregateType>(BucketAggregateType)
    MetricAggregateTypes = this.enumUtils.getEnumValues<MetricAggregateType>(MetricAggregateType);
    AggregationMetricHavingTypes = this.enumUtils.getEnumValues<AggregationMetricHavingType>(AggregationMetricHavingType);
    AggregationMetricHavingOperators = this.enumUtils.getEnumValues<AggregationMetricHavingOperator>(AggregationMetricHavingOperator);


    get havingFormGroup(): FormGroup | null | undefined{
        return this.formGroup.get(StaticFilterConfig.Having) as FormGroup;
    }

    get metricsArray(): FormArray | null | undefined{
        return this.formGroup.get(StaticFilterConfig.Metrics) as FormArray;
    }


    private previousBucketType: BucketAggregateType;
    

    StaticFilterConfig = StaticFilterConfig;

    constructor(
        private enumUtils: AppEnumUtils,
        private formBuilder: FormBuilder
    ){

    }
    ngOnChanges(changes: SimpleChanges): void {
        if(changes['formGroup']){
            if(!this.formGroup){
                this.previousBucketType = null;
                return;
            }
            this.previousBucketType = this.formGroup.value.type;
        }
    }


    protected removeMetric(index: number): void{
        this.metricsArray.removeAt(index);
    }
    protected addMetric():void{
        this.metricsArray?.push(
            new MetricConfigEditorModel().buildForm()
        )
    }

    protected onBucketTypeChange(event: MatSelectChange): void{
        // cluan up previous
        switch(this.previousBucketType){
            case BucketAggregateType.Nested: {
                // IndicatorDashboardLineChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                NestedBucketConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                break;
            }
            case BucketAggregateType.Terms: {
                // IndicatorDashboardLineChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                TermsBucketConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                break;
            }
            case BucketAggregateType.DateHistogram: {
                // IndicatorDashboardLineChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                DataHistogramBucketConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                break;
            }
            case BucketAggregateType.Composite: {
                // IndicatorDashboardLineChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                CompositeBucketConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                break;
            }

            default:{
                break;
            }
        }

        //append new controls
        switch(event.value){
            case BucketAggregateType.Nested:{
                // IndicatorDashboardLineChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                NestedBucketConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case BucketAggregateType.Terms:{
                // IndicatorDashboardLineChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                TermsBucketConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case BucketAggregateType.DateHistogram:{
                // IndicatorDashboardLineChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                DataHistogramBucketConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case BucketAggregateType.Composite:{
                // IndicatorDashboardLineChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                CompositeBucketConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            default:{
                break;
            }


        }

        this.previousBucketType = event.value;
	}



    protected onRemovableFieldChange(event:MatCheckboxChange, type :StaticFilterConfig):void{
		let controlName = null;
		let control : AbstractControl = null;
		// todo possibly validators


		switch(type){
			case StaticFilterConfig.Having:
				controlName = StaticFilterConfig.Having;
                control = new AggregationMetricHavingConfigEditorModel().buildForm()
				break;
			case StaticFilterConfig.Metrics:
				controlName = StaticFilterConfig.Metrics;
				control = this.formBuilder.array([]);
				break;
			default:
				return;
		}


		if(![controlName, control].every(x => !!x)){
			return;
		}
		
		if(event.checked){
			if(this.formGroup?.get(controlName)){
				return;
			}
			this.formGroup?.addControl(controlName, control ) /// todo possibly validators added
			return;
		}

		//* unchecked 
		if(!this.formGroup?.get(controlName)){
			return;
		}
		this.formGroup?.removeControl(controlName)
	}

}

enum StaticFilterConfig{
	Metrics = 'metrics',
	Having = 'having'
}