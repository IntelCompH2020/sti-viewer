import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { MatSelectChange } from "@angular/material/select";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";
import { SeriesValueFormatterType } from "@app/ui/indicator-dashboard/indicator-dashboard-config";
import { BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel } from "../../../dashboard-configuration-editor.model";

@Component({
    selector: 'app-value-formatter-types',
    templateUrl:'./value-formatter-types.component.html'
})
export class SerieValueFormatterTypesComponent implements OnChanges{

    @Input()
    formGroup: FormGroup;
    SeriesValueFormatterType = SeriesValueFormatterType;
	SeriesValueFormatterTypes  = this.enumUtils.getEnumValues<SeriesValueFormatterType>(SeriesValueFormatterType);


    private previousFormatterType: SeriesValueFormatterType;

    constructor(private enumUtils: AppEnumUtils){

    }
    ngOnChanges(changes: SimpleChanges): void {
        if(changes['formGroup']){
            if(!this.formGroup){
                this.previousFormatterType = null;
                return;
            }
            this.previousFormatterType = this.formGroup.value.type;
        }
    }

    onValueFormatterChange(event: MatSelectChange): void{
        // cluan up previous
        switch(this.previousFormatterType){
            case SeriesValueFormatterType.Number: {
                BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel.cleanUpNumberFormatterControls(this.formGroup);
                break;
            }
        }

        //append new controls
        switch(event.value){
            case SeriesValueFormatterType.Number:{
                BaseIndicatorDashboardSerieValuesNumberFormatterEditorModel.appendNumberFormatterControls(this.formGroup)
                break;
            }
        }

        this.previousFormatterType = event.value;
	}
}