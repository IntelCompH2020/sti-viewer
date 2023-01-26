import { Component, Input } from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { AxisLabel } from "@app/ui/indicator-dashboard/indicator-dashboard-config";

@Component({
    templateUrl:'./x-axis-editor.component.html',
    selector: 'app-x-axis-editor'
})

export class XaxisEditorComponent{
    @Input()
    formGroup: FormGroup;

    constructor(private formBuilder: FormBuilder){

    }

    get axisLabel(): FormGroup | undefined | null{
        return this.formGroup.get('axisLabel') as FormGroup;
    }


    onAxisLabelChange(event: MatCheckboxChange):void{
        if(event.checked){
            if(this.axisLabel){
                return;
            }

            const axisLabel: AxisLabel = {rotate: null, width: null};
            this.formGroup.addControl('axisLabel', this.formBuilder.group(axisLabel));
            return;
        }

        //* removing axis label
        if(!this.axisLabel){
            return;
        }

        this.formGroup.removeControl('axisLabel');
        
    }

    
}