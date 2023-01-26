import {Input,Component} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { FieldFormatterType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { BaseComponent } from '@common/base/base.component';
import { BaseIndicatorDashboardFieldFormatterChartConfigEditorModel } from '../../dashboard-configuration-editor.model';

@Component({
    styleUrls:[
        './gauge-display-editor.component.scss'
    ],
    templateUrl:'./gauge-display-editor.component.html',
    selector:'app-gauge-display-editor'
})

export class GaugeDisplayEditorConfigurationComponent extends BaseComponent{

    @Input()
    formGroup: FormGroup;

	ChangeField = ChangeField;
	FieldFormatterTypes= this.enumUtils.getEnumValues<FieldFormatterType>(FieldFormatterType);


	constructor( 
		private enumUtils: AppEnumUtils,
		){
		super();		
	}

	ngOnInit(): void {

	}



	protected onChange(event: MatCheckboxChange, field: ChangeField): void{

		switch(field){
			
			case ChangeField.LabelsTransform:{
				if(!event.checked){
					this.formGroup.removeControl('labelsTransform');
					return;
				}
				if(this.formGroup.get('labelsTransform')){
					return;
					
				}
				this.formGroup.addControl(
					'labelsTransform', 
					new BaseIndicatorDashboardFieldFormatterChartConfigEditorModel().buildForm()
				);
				return;
			}
		}
	}
}


enum ChangeField {
	LabelsTransform = 'labelsTransform'
}