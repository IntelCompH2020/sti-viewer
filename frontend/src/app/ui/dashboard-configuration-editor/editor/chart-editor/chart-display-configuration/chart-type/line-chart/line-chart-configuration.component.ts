import { Component, Input } from "@angular/core";
import { AbstractControl, FormBuilder, FormGroup } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";

@Component({
    templateUrl:'./line-chart-configuration.component.html',
    selector: 'app-dashboarc-line-chart-configuration-editor'
})
export class DashboardConfigurationLineChartComponent {


    @Input()
    formGroup: FormGroup;


    get dataZoom(): FormGroup | undefined | null{
        return this.formGroup?.get('dataZoom') as FormGroup;
    }


    constructor(private formBuilder: FormBuilder){

    }

    protected onAreaStyleChange(event: MatCheckboxChange): void{

        if(!event.checked){//  todo maybe better disable
            this.formGroup.removeControl('areaStyle');
            return;
        }

        if(this.formGroup.get('areaStyle')){ 
            this.formGroup.get('areaStyle').setValue({});
            return;
        }
        this.formGroup.addControl(
            'areaStyle',
            this.formBuilder.control({})
        )
        return;
		
	}
    
}