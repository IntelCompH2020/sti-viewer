import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import {  AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { ChartFilterType, FieldFormatterType, IndicatorFilterType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { BaseComponent } from '@common/base/base.component';
import { filter } from 'rxjs/operators';
import { BaseIndicatorDashboardDownloadDataChartConfigEditorModel, BaseIndicatorDashboardDownloadImageChartConfigEditorModel, BaseIndicatorDashboardFieldFormatterChartConfigEditorModel, BaseIndicatorDashboardFilterChartConfigEditorModel, BaseIndicatorDashboardLegendChartConfigEditorModel } from '../../dashboard-configuration-editor.model';
import { FilterDialogCompoennt } from './filter-dialog/filter-dialog.component';

@Component({
	selector: 'app-dashboard-configuration-chart-display-editor',
	templateUrl: './chart-configuration-display-editor.component.html',
	styleUrls: ['./chart-configuration-display-editor.component.scss'],
})
export class DashboardConfigurationChartDisplayEditorComponent extends BaseComponent implements OnInit, OnChanges{

	
	@Input()
	formGroup: FormGroup;

	ChangeField = ChangeField;
	FieldFormatterTypes= this.enumUtils.getEnumValues<FieldFormatterType>(FieldFormatterType);


	protected get filtersArray(): FormArray | null |undefined{
		return this.formGroup.get('filters') as FormArray;
	}


	constructor(
		private formBuilder: FormBuilder, 
		private enumUtils: AppEnumUtils,
		private dialog: MatDialog,
		){
		super();		
	}

	ngOnChanges(changes: SimpleChanges): void {

	}

	ngOnInit(): void {

	}



	protected onChange(event: MatCheckboxChange, field: ChangeField): void{

		switch(field){
			case ChangeField.Filters:{
				if(!event.checked){//  todo maybe better disable
					this.formGroup.removeControl('filters');
					return;
				}

				if(this.formGroup.get('filters')){ 
					return;
				}
				this.formGroup.addControl(
					'filters',
					this.formBuilder.array([
					])
				)
				return;
			}
			case ChangeField.Legend:{
				if(!event.checked){
					this.formGroup.removeControl('legend');
					return;
				}
				if(this.formGroup.get('legend')){
					return;
					
				}
				this.formGroup.addControl(
					'legend', 
					new BaseIndicatorDashboardLegendChartConfigEditorModel().buildForm()
				);
				return;
			}
			case ChangeField.ChartDownloadImage:{
				if(!event.checked){
					this.formGroup.removeControl('chartDownloadImage');
					return;
				}
				if(this.formGroup.get('chartDownloadImage')){
					return;
					
				}
				this.formGroup.addControl(
					'chartDownloadImage', 
					new BaseIndicatorDashboardDownloadImageChartConfigEditorModel().buildForm()
				);
				return;
			}
			case ChangeField.ChartDownloadData:{
				if(!event.checked){
					this.formGroup.removeControl('chartDownloadData');
					return;
				}
				if(this.formGroup.get('chartDownloadData')){
					return;
					
				}
				this.formGroup.addControl(
					'chartDownloadData', 
					new BaseIndicatorDashboardDownloadDataChartConfigEditorModel().buildForm()
				);
				return;
				
			}
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

	protected addFilter(): void{

		this.dialog.open(
			FilterDialogCompoennt,
			{
				disableClose: true
			}
		)
		.afterClosed()
		.pipe(
			filter(x => x)
		)
		.subscribe(
			filter => {
				this.filtersArray?.push(
				new BaseIndicatorDashboardFilterChartConfigEditorModel().fromModel(filter).buildForm()
			)}
		)
	}

	protected editFilter(index: number){
		this.dialog.open(FilterDialogCompoennt,{
			disableClose: true,
			data:{
				filter: this.filtersArray.at(index).value
			}
		})
		.afterClosed()
		.pipe(
			filter(x => x)
		)
		.subscribe(
			filter =>{
				this.filtersArray.at(index).patchValue(filter)
			}
		)
	}

	protected deleteFilter(index: number): void{
		this.filtersArray.removeAt(index);
	}
}


enum ChangeField {
	Filters = 'filters',
	Legend = 'legend',
	ChartDownloadImage= 'chartDownloadImage',
	ChartDownloadData = 'chartDownloadData',
	LabelsTransform = 'labelsTransform'
}