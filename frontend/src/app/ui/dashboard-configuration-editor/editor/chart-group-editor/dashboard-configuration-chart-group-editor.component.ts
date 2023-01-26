import { Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { MatTabGroup } from '@angular/material/tabs';
import { BaseComponent } from '@common/base/base.component';
import { BaseIndicatorDashboardChartConfigEditorModel } from '../dashboard-configuration-editor.model';

@Component({
	selector: 'app-dashboard-configuration-chart-group-editor',
	templateUrl: './dashboard-configuration-chart-group-editor.component.html',
	styleUrls: ['./dashboard-configuration-chart-group-editor.component.scss'],
})
export class DashboardConfigurationChartGroupEditorComponent extends BaseComponent implements OnInit, OnChanges{
	@ViewChild(MatTabGroup) matTabGroup:MatTabGroup;
	
	@Input()
	formGroup: FormGroup;


	selectedTabIndex = 0;


	protected get chartsArray(): FormArray{
		return this.formGroup?.get('charts') as FormArray;
	}

	constructor(){
		super();		
	}
	ngOnChanges(changes: SimpleChanges): void {
		this.selectedTabIndex = 0;
	}

	ngOnInit(): void {

	}



	protected addChart(): void{
		this.chartsArray?.push(
			new BaseIndicatorDashboardChartConfigEditorModel().buildForm()
		);

		// this.matTabGroup.focusTab(this.chartsArray?.length ?? 0);
		// this.matTabGroup.selectedIndex = this.chartsArray?.length ?? 0
		this.selectedTabIndex = (this.chartsArray?.length ?? 1) - 1 ;
	}

	protected onDelete(index: number): void{
		if(index < 0 || (index > this.chartsArray?.length)){
			return;
		}

		if(index === this.selectedTabIndex){
			this.selectedTabIndex = Math.max(0 , index -1);
		}

		this.chartsArray?.removeAt(index);
	}

	log(anything): void{
		console.log(anything);
	}

	selectTab(index: number):void{
		this.selectedTabIndex = index;
	}
}
