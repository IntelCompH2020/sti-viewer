import { Component, Input } from "@angular/core";
import { FormArray, FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { MatChipInputEvent } from "@angular/material/chips";
import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";
import { BaseIndicatorDashboardChartKeywordFilterConfigEditorModel } from "../../../dashboard-configuration-editor.model";

@Component({
    templateUrl: './static-filters-config-editor.component.html',
    selector: 'static-filters-config-editor'
})
export class StaticFiltersConfigEditorComponent{

    @Input()
    formGroup: FormGroup;

    get keywordFilters(): FormArray | null | undefined{
        return this.formGroup?.get('keywordsFilters') as FormArray;
    }

    MetricAggregateTypes = this.enumUtils.getEnumValues<MetricAggregateType>(MetricAggregateType)

    constructor(
        private enumUtils: AppEnumUtils,
        private formBuilder: FormBuilder
    ){

    }


    protected removeFilterValue(valueIndex: number, index: number): void{

        (this.keywordFilters?.at(index).get('value') as FormArray).removeAt(valueIndex);
    }

    protected addFilterValue(event: MatChipInputEvent, index: number): void{

        if(!event.value){
            return;
        }
        (this.keywordFilters?.at(index).get('value') as FormArray)?.push( this.formBuilder.control(event.value))
        event.chipInput.clear();
    }

    addKeywordFilter(): void{
        this.keywordFilters?.push(
            new BaseIndicatorDashboardChartKeywordFilterConfigEditorModel().buildForm()
        )
    }
    removeKeywordFilter(index: number): void{
        this.keywordFilters?.removeAt(index);
    }
}