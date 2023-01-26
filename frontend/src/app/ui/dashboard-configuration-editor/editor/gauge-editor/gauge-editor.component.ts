import {Component, OnInit, OnChanges, Input, Output, EventEmitter, SimpleChanges} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { BaseIndicatorDashboardGaugeConfig, IndicatorDashboardChart } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { BaseComponent } from '@common/base/base.component';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { merge, of, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, take, takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { ChartPreviewService, ChartPreviewType } from '../chart-preview.service';
import { cleanUpEmpty } from '../editor-utils';



@Component({
    templateUrl:'./gauge-editor.component.html',
    styleUrls:[
        './gauge-editor.component.scss'
    ],
    selector:'app-gauge-editor'
})
export class GaugeEditorComponent extends BaseComponent{

    @Input()
	formGroup: FormGroup;

	@Output()
	delete = new EventEmitter<void>();
	
	protected get typeControl(): FormControl | null | undefined{
		return this.formGroup.get('type') as FormControl;
	}

	protected formChangeSubscription: Subscription;

	constructor(
		private dialog: MatDialog,
		private chartPreviewService: ChartPreviewService
		
		){
		super();

	}
	ngOnChanges(changes: SimpleChanges): void {
		if(changes[nameof<GaugeEditorComponent>( x => x.formGroup)]){
			this._cleanSubscription();
			if(this.formGroup){
				this._initHook();
			}
		}
	}

	ngOnInit(): void {

	}


	onDelete(): void{
		this.dialog.open(ConfirmationDialogComponent,{
			data: {
				message: 'Delete this item?',
				confirmButton: 'Yes, delete',
				cancelButton: 'Cancel'
			}
		})
		.afterClosed()
		.pipe(
			filter(x => x),
			takeUntil(this._destroyed)
		).subscribe(
			() => {
				this.delete.emit();
			}
		);
	}

	hookChart(): void{

		if(this.formChangeSubscription){
			this._cleanSubscription();
			return;
		}

		this.formChangeSubscription = merge(
			of(this.formGroup.value),
			this.formGroup.valueChanges.pipe(
				debounceTime(800),
			)
		).pipe(
			map(val => {
				const valCopy = JSON.parse(JSON.stringify(val));
				cleanUpEmpty(valCopy.bucket);
				return valCopy;
			}),
			takeUntil(this._destroyed)
		)
		
		.subscribe((val) =>{
			this.chartPreviewService.previewChart({
                type: ChartPreviewType.Gauge,
                data: val
            })
		});

	}



	private _initHook():void{
		this.chartPreviewService.activeChart$()
		.pipe(
            take(1),
			map(chart => {
				//todo find more appropiate way fo defining if is hooked
                if(!chart){
                    return false;
                }

				if(this.formGroup?.value?.chartName){
					return false;
				}

				if(chart.type === ChartPreviewType.Chart ){
					return (chart.data as IndicatorDashboardChart)?.chartName === this.formGroup?.value?.chartName
				}
				if(chart.type  === ChartPreviewType.Gauge){
					return (chart.data as BaseIndicatorDashboardGaugeConfig).name === this.formGroup?.value?.chartName
				}
			}),
			filter(x => x),
			distinctUntilChanged(),
			takeUntil(this._destroyed)
		).subscribe(() =>{
			this.hookChart();
		})
	}

	private _cleanSubscription(): void{
		this.formChangeSubscription?.unsubscribe();
		this.formChangeSubscription = null;
	}


}