import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { MatSelectChange } from "@angular/material/select";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";
import { IndicatorDashboardChartType } from "@app/ui/indicator-dashboard/indicator-dashboard-config";
import { IndicatorDashboardBarChartConfigEditorModel, IndicatorDashboardLineChartConfigEditorModel, IndicatorDashboardPieChartConfigEditorModel, IndicatorDashboardPolarBarChartConfigEditorModel, IndicatorDashboardPolarMapChartConfigEditorModel, IndicatorDashboardPolarTreeMapChartConfigEditorModel, IndicatorDashboardSankeyChartConfigEditorModel } from "../../../dashboard-configuration-editor.model";

@Component({
    templateUrl: './chart-type-editor.component.html',
    selector: 'app-dashboard-config-chart-type-editor'
})
export class DashboardConfigChartTypeEditorComponent implements OnChanges{

    @Input()
    formGroup: FormGroup;

    availableChartTypes: IndicatorDashboardChartType[] = this.enumUtils.getEnumValues<IndicatorDashboardChartType>(IndicatorDashboardChartType);

    IndicatorDashboardChartType = IndicatorDashboardChartType;
    private previousChartType: IndicatorDashboardChartType;
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
            case IndicatorDashboardChartType.Line: {
                IndicatorDashboardLineChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup);
                break;
            }
            case IndicatorDashboardChartType.Bar:{
                IndicatorDashboardBarChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup)
                break;
            }
            case IndicatorDashboardChartType.Pie:{
                IndicatorDashboardPieChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup)
                break;
            }
            case IndicatorDashboardChartType.PolarBar:{
                IndicatorDashboardPolarBarChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup)
                break;
            }
            case IndicatorDashboardChartType.TreeMap:{
                IndicatorDashboardPolarTreeMapChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup)
                break;
            }
            case IndicatorDashboardChartType.Map:{
                IndicatorDashboardPolarMapChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup)
                break;
            }
            case IndicatorDashboardChartType.Sankey:{
                IndicatorDashboardSankeyChartConfigEditorModel.cleanUpAdditionalFields(this.formGroup)
                break;
            }

            // * not having any additional fields
            //! editor models not implemented
            case IndicatorDashboardChartType.Scatter:
            case IndicatorDashboardChartType.Graph:
            default:{
                break;
            }
        }

        //append new controls
        switch(event.value){
            case IndicatorDashboardChartType.Line:{
                IndicatorDashboardLineChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case IndicatorDashboardChartType.Bar:{
                IndicatorDashboardBarChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case IndicatorDashboardChartType.Pie:{
                IndicatorDashboardPieChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case IndicatorDashboardChartType.PolarBar:{
                IndicatorDashboardPolarBarChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case IndicatorDashboardChartType.TreeMap:{
                IndicatorDashboardPolarTreeMapChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case IndicatorDashboardChartType.Map:{
                IndicatorDashboardPolarMapChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }
            case IndicatorDashboardChartType.Sankey:{
                IndicatorDashboardSankeyChartConfigEditorModel.appendAdditionalFields(this.formGroup);
                break;
            }

            // * not having any additional fields
            //! editor models not implemented
            case IndicatorDashboardChartType.Scatter:
            case IndicatorDashboardChartType.Graph:
            default:{
                break;
            }


        }

        this.previousChartType = event.value;
	}
}