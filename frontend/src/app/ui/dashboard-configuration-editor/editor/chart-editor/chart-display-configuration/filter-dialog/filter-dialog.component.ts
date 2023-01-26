import { Component, Inject, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";
import { ChartFilterType, IndicatorFilterType } from "@app/ui/indicator-dashboard/indicator-dashboard-config";
import { BaseIndicatorDashboardFilterChartConfigEditorModel } from "../../../dashboard-configuration-editor.model";

@Component({
    templateUrl:'./filter-dialog.component.html'
})
export class FilterDialogCompoennt implements OnInit{

    formGroup: FormGroup;

    ChartFilterTypes = this.enumUtils.getEnumValues<ChartFilterType>(ChartFilterType);
	IndicatorFilterTypes= this.enumUtils.getEnumValues<IndicatorFilterType>(IndicatorFilterType);


    get valuesArray(): FormArray | null | undefined{
        return this.formGroup?.get('values') as FormArray;
    }

    constructor(
        private dialogRef : MatDialogRef<FilterDialogCompoennt>, 

        @Inject(MAT_DIALOG_DATA) 
        private data,
        private enumUtils: AppEnumUtils,
        private formBuilder: FormBuilder,
        ){

    }
    ngOnInit(): void {
        this.formGroup = new BaseIndicatorDashboardFilterChartConfigEditorModel().fromModel(this.data?.filter).buildForm();
    }

    protected cancel(): void{
        this.dialogRef.close(false);
    }

    protected submit(): void{
        if(!this.formGroup.valid){
            return;
        }
        const value = this.formGroup.value
        this.dialogRef.close(
            value
        )
    }

    addValues(): void{
        this.valuesArray.push(
            this.formBuilder.group({
                name: '', // TODO BETTER ON EDITOR MODEL
                value: ''
            })
        )
    }

    removeValue(index: number): void{
        this.valuesArray?.removeAt(index);
    }
}