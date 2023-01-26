import {Input, Component, OnInit, SimpleChanges} from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { GaugesBlock } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { BaseComponent } from '@common/base/base.component';
import { nameof } from 'ts-simple-nameof';
import { BaseIndicatorDashboardGaugeConfigEditorModel } from '../dashboard-configuration-editor.model';


@Component({
    styleUrls: ['./gauge-group-editor.component.scss'],
    templateUrl:'./gauge-group-editor.component.html',
    selector:'app-gauge-group-editor'
})
export class GaugeGroupEditorComponent extends BaseComponent implements OnInit{

    @Input()
    formGroup: FormGroup;
    
    
	selectedTabIndex = 0;


	protected get gaugesArray(): FormArray{
		return this.formGroup?.get(nameof<GaugesBlock>(x => x.gauges)) as FormArray;
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
		this.gaugesArray?.push(
            new BaseIndicatorDashboardGaugeConfigEditorModel().buildForm()
		);

		// this.matTabGroup.focusTab(this.chartsArray?.length ?? 0);
		// this.matTabGroup.selectedIndex = this.chartsArray?.length ?? 0
		this.selectedTabIndex = (this.gaugesArray?.length ?? 1) - 1 ;
	}

	protected onDelete(index: number): void{
		if(index < 0 || (index > this.gaugesArray?.length)){
			return;
		}

		if(index === this.selectedTabIndex){
			this.selectedTabIndex = Math.max(0 , index -1);
		}

		this.gaugesArray?.removeAt(index);
	}

	log(anything): void{
		console.log(anything);
	}

	selectTab(index: number):void{
		this.selectedTabIndex = index;
	}
}