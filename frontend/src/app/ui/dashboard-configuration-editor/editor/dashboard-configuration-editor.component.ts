import { Component, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormGroup, NgModel } from '@angular/forms';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { DasboardConfigurationEditorModel, IndicatorDashboardTabConfigModelEditorModel } from './dashboard-configuration-editor.model';
import * as FileSaver from 'file-saver';
import { BaseServerFetchConfiguration, GaugesBlock, IndicatorDashboardChartGroupConfig, IndicatorDashboardConfig, TabBlockType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';
import { ActivatedRoute, Router } from '@angular/router';
import { filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { UserSettingsService } from '@app/core/services/http/user-settings.service';
import { nameof } from 'ts-simple-nameof';
import { UserSettings, UserSettingsEntityType, UserSettingsType } from '@app/core/model/user/user-settings.model';
import { Guid } from '@common/types/guid';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDeleteDialogComponent } from '@common/modules/confirmation-dialog/confirmation-delete-dialog/confirmation-delete-dialog.component';
import { BasePendingChangesComponent } from '@common/base/base-pending-changes.component';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { TranslateService } from '@ngx-translate/core';
import { DashboardTabBlockToChartGroupBlockPipe } from '@app/core/formatting/pipes/dashboard-chart-group.pipe';
import { DefaultsService } from '@app/core/services/data-transform/defaults.service';

// const DASBOARD_CONFIG = require('./default.config.json')

@Component({
	selector: 'app-dashboard-configuration-editor',
	templateUrl: './dashboard-configuration-editor.component.html',
	styleUrls: ['./dashboard-configuration-editor.component.scss'],
	animations: GENERAL_ANIMATIONS
})
export class DashboardConfigurationEditorComponent extends BasePendingChangesComponent implements OnInit{

	canDeactivate(): boolean | Observable<boolean> {
		return !this.formGroup?.dirty
	}


	
    @ViewChild('configurationKey') configurationKey: NgModel;
    pendingBlockingRequest: boolean = false;
    loadedSettings: UserSettings = {} as UserSettings;
	
	protected editorModel!: DasboardConfigurationEditorModel;
	protected formGroup: FormGroup;


	protected selectedTabFormGroup: FormGroup | undefined | null;


	protected get tabGroupsArray(): FormArray{
		return this.formGroup?.get('tabs') as FormArray;
	}

	protected preview: boolean = false;

	constructor(
		private route: ActivatedRoute, 
		private userSettingsService: UserSettingsService,
		private dialog: MatDialog,
		private uiNotificationsService: UiNotificationService,
		private language: TranslateService,
		private router: Router,
		private tabBlockToChartGroupBlock: DashboardTabBlockToChartGroupBlockPipe,
		private defautlsService: DefaultsService,
		){
		super();		
	}

	ngOnInit(): void {

		this.route.paramMap.pipe(

			switchMap(params => {
				const id = params?.get('id');
				if(id){
					const fields = [
						nameof<UserSettings>(x => x.value),
						nameof<UserSettings>(x => x.id),
						nameof<UserSettings>(x => x.hash),
						nameof<UserSettings>(x => x.updatedAt),
						nameof<UserSettings>(x => x.entityId),
						nameof<UserSettings>(x => x.key),
						nameof<UserSettings>(x => x.entityType),
						nameof<UserSettings>(x => x.type),
					]


					return this.userSettingsService.getSingle(id as unknown as Guid, fields)
					.pipe(
                        tap(x => {this.loadedSettings = x}),
						map(x => JSON.parse(x.value))
					)
				}
				return of({});
			}),
			filter(x => x),
			map(x => this.defautlsService.enrichDashboardConfigWithDefaults(x)),
			takeUntil(this._destroyed)
		)
		.subscribe(response => {

			this.editorModel = new DasboardConfigurationEditorModel().fromModel(response);

			this.formGroup = this.editorModel.buildForm();
			if(this.tabGroupsArray.length){
				this.selectedTabFormGroup = this.tabGroupsArray.at(0) as FormGroup;
			}
		})

	}


	protected addNewTab(): void{
		const newtab = new IndicatorDashboardTabConfigModelEditorModel().fromModel({chartGroups: []} as any).buildForm()
		this.tabGroupsArray?.push(
			newtab
		);
		this.selectedTabFormGroup = newtab;
	}

	protected downloadConfig(): void{


		const indicatorConfig = this.formGroup.value;
		this._cleanUpIndicatorConfig(indicatorConfig);

		const blob = new Blob([JSON.stringify(indicatorConfig)], {type: "application/json;charset=utf-8"});
		FileSaver.saveAs(blob, "configuration.json");

	}



	protected removeTab(index: number): void{

		this.dialog.open(ConfirmationDeleteDialogComponent)
		.afterClosed()
		.pipe(
			filter(x => x),
			takeUntil(this._destroyed)
		)
		.subscribe(() =>{
			if(this.selectedTabFormGroup === this.tabGroupsArray.at(index)){
				this.selectedTabFormGroup = null;
			}
			this.tabGroupsArray.removeAt(index);
		})


	}


		
    protected save(): void{

        if(!this.formGroup?.valid){
            this.formGroup?.markAllAsTouched();
            return;
        }

        if(!this.configurationKey?.valid){
            this.configurationKey?.control.markAsTouched();
            return;
        }

        if(this.pendingBlockingRequest){
            return;
        }


        this.pendingBlockingRequest = true;


		const value = this.formGroup.value;
		this._cleanUpIndicatorConfig(value);

        this.userSettingsService.persist({
            id: this.loadedSettings?.id,
            entityId: this.loadedSettings?.entityId,
            key: this.loadedSettings?.key,
            entityType: UserSettingsEntityType.Application,
            type: UserSettingsType.Dashboard,
            value: JSON.stringify(value),
            hash: this.loadedSettings?.hash
        })
        .pipe(
            takeUntil(this._destroyed)
        )
        .subscribe(
            () => {
                this.formGroup.markAsPristine();
                this.uiNotificationsService.snackBarNotification(
                    this.language.instant('COMMONS.SNACK-BAR.SUCCESSFUL-UPDATE'), 
                    SnackBarNotificationLevel.Success
                )
                this.pendingBlockingRequest = false
                this.router.navigate(['config-editor']);
            },
            error  => {
                this.uiNotificationsService.snackBarNotification(
                    this.language.instant('COMMONS.ERRORS.DEFAULT')
                    , SnackBarNotificationLevel.Error
                )
                this.pendingBlockingRequest = false;
            }
        )


    }





	private _cleanUpIndicatorConfig(config: IndicatorDashboardConfig):void{
		

		const cleanUpBucketsAndMetrics = (chart: BaseServerFetchConfiguration) => {
			const bucket = chart.bucket;
			const metrics = chart.metrics;


			this._cleanUpEmpty(bucket);
			if(!metrics?.length){
				delete chart.metrics;
			}
		}


		config.tabs.forEach(tab => {
			tab.chartGroups.forEach(
				chartgroup => {

					switch(chartgroup.type){
						case TabBlockType.ChartGroup:{

							(chartgroup as IndicatorDashboardChartGroupConfig)?.charts?.forEach(chart =>cleanUpBucketsAndMetrics(chart))
							break;
						}
						case TabBlockType.Gauge:{

							(chartgroup as GaugesBlock)?.gauges?.forEach(gauge => cleanUpBucketsAndMetrics(gauge));
							break;
						}
					}

				}
			)
		})
	}





	private _cleanUpEmpty(object: Record<any, any> | any[], parentKey?: any): void{
		if(!object){
			return;
		}

		if(Array.isArray(object)){
			if(!parentKey){
				throw'missing parent key';
			}
			return;

		}



		Object.keys(object).forEach(key =>{

			const targetValue = object[key];

			if((targetValue === null) || (targetValue === undefined)){
				delete object[key];
				return;
			}

			if(Array.isArray(targetValue)){
				if(!targetValue?.length){
					delete object[key];
					return;
				}
				targetValue.forEach(obj => this._cleanUpEmpty(obj, key));
				return;
			}

			if(targetValue instanceof Object){
				this._cleanUpEmpty(targetValue);
				if(!Object.keys(targetValue).length){
					delete object[key];
					return;
				}
			}

		})
	}

}
