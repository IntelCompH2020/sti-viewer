import {Component, Input, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { GaugeType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';


@Component({
    templateUrl:'./gauge-type-editor.component.html',
    styleUrls:[
        './gauge-type-editor.component.scss'
    ],
    selector:'app-gauge-type-editor'
})

export class GaugeTypeEditorComponent{


    @Input()
    formGroup: FormGroup;


    
    availableGaugeTypes: GaugeType[] = this.enumUtils.getEnumValues<GaugeType>(GaugeType);

    GaugeType = GaugeType;
    private previousChartType: GaugeType;
    constructor(private enumUtils: AppEnumUtils ){

    }
    ngOnChanges(changes: SimpleChanges): void {
        if(changes['formGroup']){
            if(!this.formGroup){
                this.previousChartType = null;
                return;
            }
            this.previousChartType = this.formGroup.value.type;
        }
    }

    protected onValueFormatterChange(event: MatSelectChange): void{
        console.log('previous value', this.previousChartType);
        console.log('current value', event.value);
        // cluan up previous
        switch(this.previousChartType){
            case GaugeType.ValueCard: {
                break;
            }
            default:{
                break;
            }
        }

        //append new controls
        switch(event.value){
            case GaugeType.ValueCard:{
                break;
            }
            default:{
                break;
            }

        }

        this.previousChartType = event.value;
	}
}