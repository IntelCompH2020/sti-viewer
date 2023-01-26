import { Component, Input } from "@angular/core";
import { FormArray, FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { MatChipInputEvent } from "@angular/material/chips";
import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";

@Component({
    templateUrl: './raw-data-request-config-editor.component.html',
    selector: 'raw-data-request-config-editor'
})
export class RawDataRequestConfigEditorComponent{

    @Input()
    formGroup: FormGroup;

    MetricAggregateTypes = this.enumUtils.getEnumValues<MetricAggregateType>(MetricAggregateType)

    get page(): FormGroup | null | undefined{
        return this.formGroup.get('page') as FormGroup;
    }

    get order(): FormGroup | null | undefined{
        return this.formGroup.get('order') as FormGroup;
    }

    get orderingItems(): FormArray | null | undefined{
        return this?.order.get('items') as FormArray;
    }
    constructor(
        private enumUtils: AppEnumUtils,
        private formBuilder: FormBuilder
    ){

    }



    protected addOrderingValue(event: MatChipInputEvent): void{
        if(!event.value){
            return;
        }
        this.orderingItems?.push(
            this.formBuilder.control(event.value)
        )
        event.chipInput.clear();
    }

    protected removeOrderingValue(index: number): void{
        this.orderingItems?.removeAt(index);
    }
}