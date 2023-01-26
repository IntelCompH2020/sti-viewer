import { Component, Input, OnInit} from '@angular/core';
import {  FormArray, FormGroup } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { BaseComponent } from '@common/base/base.component';
import { BaseIndicatorDashboardSerieEditorModel, BaseIndicatorDashboardSerieSplitSerieEditorModel, ConnectionLimitEditorModel } from '../../dashboard-configuration-editor.model';
import { filter, takeUntil } from 'rxjs/operators';
import { ValueTestDialogComponent } from './value-test-dialog/value-test-dialog.component';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { ConnectionLimitOrder, ConnectionLimitType, DashBoardSerieConfigurationNestedType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { ConfirmationDeleteDialogComponent } from '@common/modules/confirmation-dialog/confirmation-delete-dialog/confirmation-delete-dialog.component';
import { MatCheckboxChange } from '@angular/material/checkbox';


@Component({
	selector: 'app-dashboard-configuration-data-mapping-editor',
	templateUrl: './dashboard-configuration-data-mapping-editor.component.html',
	styleUrls: ['./dashboard-configuration-data-mapping-editor.component.scss'],
})
export class DashboardConfigurationDataMappingEditorComponent extends BaseComponent implements OnInit{

	
	@Input()
	formGroup: FormGroup;


	protected availableNestingTypes:DashBoardSerieConfigurationNestedType[]  = this.enumUtils.getEnumValues<DashBoardSerieConfigurationNestedType>(DashBoardSerieConfigurationNestedType)
	protected avalableConnectionLimitTypes = this.enumUtils.getEnumValues<ConnectionLimitType>(ConnectionLimitType)
	protected avalableConnectionOrderTypes = this.enumUtils.getEnumValues<ConnectionLimitOrder>(ConnectionLimitOrder)

	constructor(
		private dialog: MatDialog, 
		private enumUtils: AppEnumUtils
	){
		super();		
	}

	protected get connectionExtractor(): FormGroup | undefined | null{
		return this.formGroup?.get('connectionExtractor') as FormGroup;
	}
	protected get connectionExtractorLimit(): FormGroup | undefined | null{
		return this.connectionExtractor?.get('limit') as FormGroup;
	}
	protected get seriesArray(): FormArray | null | undefined{
		return this.formGroup?.get('series') as FormArray;
	}

	

	ngOnInit(): void {

	}


	protected addSerie(): void{
		this.seriesArray?.push(
			new BaseIndicatorDashboardSerieEditorModel().buildForm()
		)
	}


	protected deleteSerie(index: number): void{
		this.dialog.open(ConfirmationDeleteDialogComponent)
		.afterClosed()
		.pipe(
			takeUntil(this._destroyed),
			filter(x => x)
		)
		.subscribe(()=>{
			this.seriesArray?.removeAt(index);
		})
	}


	protected addKeywordFromInput(event: MatChipInputEvent,serie: FormGroup): void{
		if (event.value) {
			(serie.get('splitSeries') as FormArray).push(
				new BaseIndicatorDashboardSerieSplitSerieEditorModel().fromModel({key: event.value}).buildForm()
			)
			event.chipInput!.clear();
		}
	}

	protected removeKeyword(serie: FormGroup, index: number): void{
		(serie.get('splitSeries') as FormArray)?.removeAt(index);
	}

	protected addNewTest(serie: FormGroup, target: HTMLInputElement, value: HTMLInputElement){

		if(!(target.value && value.value)){
			return;
		}

		const testsControl = serie.get('values').get('tests');
		testsControl.setValue([...testsControl.value, {[target.value]: [value.value]}]);
		target.value = '';
		value.value = '';

	}

	protected removeTest(serie: FormGroup, index: number): void{
		const testsControl = serie.get('values').get('tests');
		const newTests = (testsControl.value as Record<string, string>[]).filter((_,idx) => idx !== index);
		testsControl.setValue(newTests);
	}

	protected editTest(serie: FormGroup, index: number): void{
		const testsControl = serie.get('values').get('tests');
		const tests = testsControl.value as Record<string,string>[];
		const targetTest = tests[index]

		this.dialog.open(ValueTestDialogComponent,{
			data: {
				test: targetTest
			}
		})
		.afterClosed()
		.pipe(
			filter(x => x),
			takeUntil(this._destroyed)
		).subscribe((updatedTest) =>{
			tests[index] = updatedTest as Record<string, string>;
			testsControl.setValue(tests);
			
		})

	}
	protected addNewGroupTest(serie: FormGroup, target: HTMLInputElement, value: HTMLInputElement){

		if(!(target.value && value.value)){
			return;
		}

		const groupTestsControl = serie.get('values').get('groupTests');
		groupTestsControl.setValue([...groupTestsControl.value, {[target.value]: [value.value]}]);
		target.value = '';
		value.value = '';
	}

	protected removeGroupTest(serie: FormGroup, index: number): void{
		const testsControl = serie.get('values').get('groupTests');
		const newTests = (testsControl.value as Record<string, string>[]).filter((_,idx) => idx !== index);
		testsControl.setValue(newTests);
	}

	protected editGroupTest(serie: FormGroup, index: number): void{
		const testsControl = serie.get('values').get('groupTests');
		const tests = testsControl.value as Record<string,string>[];
		const targetTest = tests[index]

		this.dialog.open(ValueTestDialogComponent,{
			data: {
				test: targetTest
			}
		})
		.afterClosed()
		.pipe(
			filter(x => x),
			takeUntil(this._destroyed)
		).subscribe((updatedTest) =>{
			tests[index] = updatedTest as Record<string, string>;
			testsControl.setValue(tests);
			
		})
	}

	protected addNewConnectionValueTest(target: HTMLInputElement, value: HTMLInputElement){

		if(!(target.value && value.value)){
			return;
		}

		const testsControl = this.connectionExtractor?.get('valueTests');
		testsControl.setValue({...testsControl.value, [target.value]: value.value});
		target.value = '';
		value.value = '';

	}

	protected removeConnectionValueTest( key: string): void{
		const testsControl = this.connectionExtractor?.get('valueTests');
		const newTests = {...testsControl.value};
		delete newTests[key];

		testsControl.setValue(newTests);
	}

	protected editConnectionValueTest(key: string): void{
		const testsControl = this.connectionExtractor.get('valueTests');

		const tests = {...testsControl.value};

		const targetTest = {[key]: tests[key] };

		this.dialog.open(ValueTestDialogComponent,{
			data: {
				test: targetTest
			}
		})
		.afterClosed()
		.pipe(
			filter(x => x),
			takeUntil(this._destroyed)
		).subscribe((updatedTest) =>{
			delete tests[key];
			testsControl.setValue({...tests, ...updatedTest});
			
		})

	}
	protected addNewConnectionGroupTest(target: HTMLInputElement, value: HTMLInputElement){

		if(!(target.value && value.value)){
			return;
		}

		const groupTestsControl = this.connectionExtractor.get('groupTests');
		groupTestsControl.setValue({...groupTestsControl.value,[target.value]: value.value});
		target.value = '';
		value.value = '';
	}

	protected removeConnectionGroupTest( key: string): void{
		const testsControl = this.connectionExtractor.get('groupTests');
		const newTests = {...testsControl.value};
		delete newTests[key];
		testsControl.setValue(newTests);
	}

	protected editConnectionGroupTest( key: string): void{
		const testsControl = this.connectionExtractor.get('groupTests');

		const tests = {...testsControl.value};

		const targetTest = {[key]: tests[key] };

		this.dialog.open(ValueTestDialogComponent,{
			data: {
				test: targetTest
			}
		})
		.afterClosed()
		.pipe(
			filter(x => x),
			takeUntil(this._destroyed)
		).subscribe((updatedTest) =>{
			delete tests[key];
			testsControl.setValue({...tests, ...updatedTest});
			
		})
	}



	onValueFormatterChange(event): void{
		console.log('change', event);
	}

	onConnectionLimitChange(event: MatCheckboxChange): void{
		if(event.checked){
			if(this.connectionExtractorLimit){
				return;
			}

			this.connectionExtractor?.addControl(
				'limit', 
				new ConnectionLimitEditorModel().buildForm()
			)
			return;
		}


		// * form should be removed
		if(!this.connectionExtractorLimit){
			return;
		}

		this.connectionExtractor?.removeControl('limit');
	}

}
