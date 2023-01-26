import { Component, Input } from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";

@Component({
    templateUrl:'./bar-chart-configuration.component.html',
    selector: 'app-dashboarc-bar-chart-configuration-editor'
})
export class DashboardConfigurationBarChartComponent {


    @Input()
    formGroup: FormGroup;


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