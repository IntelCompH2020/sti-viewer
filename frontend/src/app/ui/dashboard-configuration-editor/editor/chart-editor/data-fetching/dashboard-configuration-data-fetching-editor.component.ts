import { Component, Input, OnInit } from '@angular/core';
import {  AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { RawEditorOutputData, RawEditorDialogComponent, RawEditorInputData } from '@app/ui/dashboard-configuration-editor/raw-editor/raw-editor-dialog.component';
import { BaseComponent } from '@common/base/base.component';
import { BaseBucketConfigEditorModel, MetricConfigEditorModel } from '../../bucket-editor-models.model';
import { BaseIndicatorDashboardChartRawDataRequestConfigEditorModel, BaseIndicatorDashboardChartStaticFilterConfigEditorModel } from '../../dashboard-configuration-editor.model';
import { filter, map, takeUntil } from 'rxjs/operators';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { TranslateService } from '@ngx-translate/core';


@Component({
	selector: 'app-dashboard-configuration-data-fetching-editor',
	templateUrl: './dashboard-configuration-data-fetching-editor.component.html',
	styleUrls: ['./dashboard-configuration-data-fetching-editor.component.scss'],
})
export class DashboardConfigurationDataFetchingEditorComponent extends BaseComponent implements OnInit{

	
	@Input()
	formGroup: FormGroup;


	get bucket(): FormGroup | null | undefined{
		return this.formGroup?.get(FetchingConfig.Bucket) as FormGroup;
	}

	get staticFilters(): FormGroup | null | undefined{
		return this.formGroup?.get(FetchingConfig.StaticFilters) as FormGroup;
	}

	get metricsArray(): FormArray | null | undefined{
        return this.formGroup?.get(FetchingConfig.Metrics) as FormArray;
    }

	get rawDataRequest(): FormGroup | null | undefined{
		return this.formGroup?.get(FetchingConfig.RawDataRequest) as FormGroup;
	}


	FetchingConfig = FetchingConfig;

	constructor(
		private formBuilder: FormBuilder,
		private dialog:MatDialog,
		private uiNotificationsService: UiNotificationService,
		private language: TranslateService
		){
		super();		
	}

	ngOnInit(): void {

	}


	protected removeMetric(index: number): void{
        this.metricsArray.removeAt(index);
    }
    protected addMetric():void{
        this.metricsArray?.push(
            new MetricConfigEditorModel().buildForm()
        )
    }


	protected onRemovableFieldChange(event:MatCheckboxChange, type :FetchingConfig):void{
		let controlName = null;
		// todo possibly validators


		switch(type){
			case FetchingConfig.Bucket:
				controlName = FetchingConfig.Bucket;
				break;
			case FetchingConfig.Metrics:
				controlName = FetchingConfig.Metrics;
				break;
			case FetchingConfig.RawDataRequest:
				controlName = FetchingConfig.RawDataRequest;
				break;
			case FetchingConfig.StaticFilters:
				controlName = FetchingConfig.StaticFilters;
				break;
			default:
				return;
		}


		if(![controlName].every(x => !!x)){
			return;
		}
		
		if(event.checked){
			if(this.formGroup?.get(controlName)){
				return;
			}
			const control = this._getControlOf(type);
			if(!control){
				return
			}
			this.formGroup?.addControl(controlName, control) /// todo possibly validators added
			return;
		}

		//* unchecked 
		if(!this.formGroup?.get(controlName)){
			return;
		}
		this.formGroup?.removeControl(controlName)
	}


	protected openRawEditor(): void{
		this.dialog.open<RawEditorDialogComponent, RawEditorInputData, RawEditorOutputData[]>(RawEditorDialogComponent,{
			disableClose: true,
			data: {
				items: [
					{
						name:this.language.instant('APP.DASHBOARD-CONFIGURATION.EDITOR.CHART-EDITOR.DATA-FETCHING-EDITOR.BUCKET-CONFIGURATION'),
						key: FetchingConfig.Bucket,
						value: this.bucket?.value
					},
					{
						name:this.language.instant('APP.DASHBOARD-CONFIGURATION.EDITOR.CHART-EDITOR.DATA-FETCHING-EDITOR.STATIC-FILTERS'),
						key: FetchingConfig.StaticFilters,
						value: this.staticFilters?.value
					},
					{
						name:this.language.instant('APP.DASHBOARD-CONFIGURATION.EDITOR.CHART-EDITOR.DATA-FETCHING-EDITOR.METRIC-CONFIGURATION'),
						key:FetchingConfig.Metrics,
						value: this.metricsArray?.value
					},
					{
						name:this.language.instant('APP.DASHBOARD-CONFIGURATION.EDITOR.CHART-EDITOR.DATA-FETCHING-EDITOR.RAW-DATA-REQUEST'),
						key: FetchingConfig.RawDataRequest,
						value: this.rawDataRequest?.value
					}
				].filter(x => x.value)
			}
		})
		.afterClosed()
		.pipe(
			filter(
				x => !!x
			),
			takeUntil(
				this._destroyed
			),
			map(
				configs => configs.map(config => {
					try {
						return {
							key: config.key as FetchingConfig,
							control: this._getControlOf(config.key as FetchingConfig, JSON.parse(config.value))
						}
					} catch (error) {
						return null;
					}
				})
			)
		)
		.subscribe(
			items =>{
				if(!items.every(item => !!item?.control))	{
					this.uiNotificationsService.snackBarNotification('Could not parse data', SnackBarNotificationLevel.Error);
					return;
				}


				items.forEach(
					item => this.formGroup.setControl(item.key, item.control)
				)
			}
		)
	}


	private _getControlOf(type: FetchingConfig, model?: any): AbstractControl{
		switch(type){
			case FetchingConfig.Bucket:
				return new BaseBucketConfigEditorModel().fromModel(model).buildForm();
			case FetchingConfig.Metrics:
				return this.formBuilder.array([]);
			case FetchingConfig.RawDataRequest:
				return new BaseIndicatorDashboardChartRawDataRequestConfigEditorModel().fromModel(model).buildForm()
			case FetchingConfig.StaticFilters:
				return  new BaseIndicatorDashboardChartStaticFilterConfigEditorModel().fromModel(model).buildForm()
			default:
				return null;
		}
	}
}


enum FetchingConfig{
	Metrics = 'metrics',
	Bucket = 'bucket',
	StaticFilters = 'staticFilters',
	RawDataRequest = 'rawDataRequest'
}