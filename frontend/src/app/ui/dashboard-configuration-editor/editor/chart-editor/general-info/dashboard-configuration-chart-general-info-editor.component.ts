import { Component, Input, OnInit } from '@angular/core';
import {  FormArray, FormControl, FormGroup } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { BaseComponent } from '@common/base/base.component';

@Component({
	selector: 'app-dashboard-configuration-chart-general-info-editor',
	templateUrl: './dashboard-configuration-chart-general-info-editor.component.html',
	styleUrls: ['./dashboard-configuration-chart-general-info-editor.component.scss'],
})
export class DashboardConfigurationChartGeneralInfoEditorComponent extends BaseComponent implements OnInit{

	
	@Input()
	formGroup: FormGroup;



	get tags(): FormArray{
		return this.formGroup?.get('tags')?.get('attachedTags') as FormArray;
	}

	// availableChartTypes: IndicatorDashboardChartType[] = this.enumUtils.getEnumValues<IndicatorDashboardChartType>(IndicatorDashboardChartType);
	constructor(){
		super();		
	}

	ngOnInit(): void {

	}


	protected addTag(event: MatChipInputEvent):void{
		
		this.tags?.push(
			new FormControl(
				event.value
			)
		);

		event?.chipInput?.clear();
	}


	protected removeTag(index: number){
		this.tags?.removeAt(index);
	}

}
