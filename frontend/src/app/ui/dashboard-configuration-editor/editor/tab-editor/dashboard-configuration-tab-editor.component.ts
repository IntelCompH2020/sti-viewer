import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TabBlockType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { BaseComponent } from '@common/base/base.component';
import { ConfirmationDeleteDialogComponent } from '@common/modules/confirmation-dialog/confirmation-delete-dialog/confirmation-delete-dialog.component';
import { filter, takeUntil } from 'rxjs/operators';
import { IndicatorDashboardChartGroupConfigEditorModel, IndicatorDashboardGaugeGroupConfigEditorModel } from '../dashboard-configuration-editor.model';

@Component({
	selector: 'app-dashboard-configuration-tab-editor',
	templateUrl: './dashboard-configuration-tab-editor.component.html',
	styleUrls: ['./dashboard-configuration-tab-editor.component.scss'],
})
export class DashboardConfigurationTabEditorComponent extends BaseComponent implements OnInit, OnChanges{

	
	@Input()
	formGroup?: FormGroup;


	TabBlockType = TabBlockType;
	protected get chartGroupsArray(): FormArray{
		return this.formGroup?.get('chartGroups') as FormArray;
	}

	constructor(private dialog: MatDialog){
		super();		
	}
	ngOnChanges(changes: SimpleChanges): void {
		this.chartGroupSelected = null;
	}

	ngOnInit(): void {
		if(this.chartGroupsArray.length){
			this.chartGroupSelected = this.chartGroupsArray.at(0) as FormGroup;
		}
	}


	chartGroupSelected: FormGroup;
	addChartGroup():void{
		const addition = new IndicatorDashboardChartGroupConfigEditorModel().fromModel({ charts: []} as any).buildForm()

		this.chartGroupsArray?.push(
			addition
		)
		this.onSelectChartGroup(addition);

	}
	addGaugeGroup():void{
		const addition = new IndicatorDashboardGaugeGroupConfigEditorModel().fromModel({ charts: []} as any).buildForm()

		this.chartGroupsArray?.push(
			addition
		)
		this.onSelectChartGroup(addition);

	}

	onSelectChartGroup(chartGroup: FormGroup){
		this.chartGroupSelected = chartGroup;
	}


	
	protected removeChartGroup(index: number): void{


		this.dialog.open(ConfirmationDeleteDialogComponent)
		.afterClosed()
		.pipe(
			filter(x => x),
			takeUntil(this._destroyed)
		)
		.subscribe(() =>{
			if(this.chartGroupSelected === this.chartGroupsArray.at(index)){
				this.chartGroupSelected = null;
			}
			this.chartGroupsArray.removeAt(index);
		})

	}


}
