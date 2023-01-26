import { Component, OnInit, ViewChild } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, NgModel } from "@angular/forms";
import { SearchConfigurationEditorModel } from "./search-configuration-editor.model";
import * as FileSaver from 'file-saver';
import { UserSettings, UserSettingsEntityType, UserSettingsType } from "@app/core/model/user/user-settings.model";
import { filter, map, tap, switchMap, takeUntil } from "rxjs/operators";
import { ActivatedRoute, Router } from "@angular/router";
import { nameof } from "ts-simple-nameof";
import { UserSettingsService } from "@app/core/services/http/user-settings.service";
import { Observable, of } from "rxjs";
import { Guid } from "@common/types/guid";
import { BasePendingChangesComponent } from "@common/base/base-pending-changes.component";
import { SnackBarNotificationLevel, UiNotificationService } from "@common/modules/notification/ui-notification-service";
import { TranslateService } from "@ngx-translate/core";

// const config = require('./config.json')


@Component({
    templateUrl: './search-configuration-editor.component.html',
    styleUrls: [
        './search-configuration-editor.component.scss'
    ]
})
export class SearchConfigurationEditorComponent extends BasePendingChangesComponent implements OnInit{
    canDeactivate(): boolean | Observable<boolean> {
        return !this.formGroup?.dirty;
    }


    @ViewChild('configurationKey') configurationKey: NgModel;
    pendingBlockingRequest: boolean = false;
    loadedSettings: UserSettings = {} as UserSettings;

    private editorModel: SearchConfigurationEditorModel
    protected formGroup : FormGroup;


    get searchFields(): FormArray | undefined | null{
        return this.formGroup?.get('searchFields') as FormArray;
    }

    get indicatorIds(): FormArray | undefined | null{
        return this.formGroup.get('indicatorIds') as FormArray;
    }

    get staticFilters(): FormGroup | undefined | null{
        return this.formGroup.get('staticFilters') as FormGroup;
    }

    get viewConfig(): FormGroup | undefined | null{
        return this.formGroup.get('viewConfig') as FormGroup;
    }

    get supportedDashboards(): FormArray | undefined | null{
        return this.formGroup.get('supportedDashboards') as FormArray;
    }

    get dashboardFilters(): FormGroup | undefined | null{
        return this.formGroup.get('dashboardFilters') as FormGroup;
    }
    constructor(
        private formBuilder: FormBuilder, 
        private route: ActivatedRoute, 
        private userSettingsService: UserSettingsService,
        private router: Router,
        private uiNotificationsService: UiNotificationService,
        private language: TranslateService
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
			takeUntil(this._destroyed)
        ).subscribe(
            config => {
                this.editorModel = new SearchConfigurationEditorModel().fromModel(config);
                this.formGroup = this.editorModel.buildForm();
            }
        )
        
    }


    protected removeIndicator(index: number): void{
        this.indicatorIds?.removeAt(index);
    }

    protected addIndicator(indicatorInput: HTMLInputElement): void{
        const indicatorId =indicatorInput.value;

        if(!indicatorId){
            return;
        }
        this.indicatorIds?.push(this.formBuilder.control(indicatorId));
        indicatorInput.value = '';
    }

    protected removeSearchField(index: number): void{
        this.searchFields?.removeAt(index);
    }

    protected addSearchField(searchFieldInput: HTMLInputElement): void{
        const searchTerm =searchFieldInput.value;

        if(!searchTerm){
            return;
        }
        this.searchFields?.push(this.formBuilder.control(searchTerm));
        searchFieldInput.value = '';
    }

    protected removeSupportedDashboard(index: number): void{
        this.supportedDashboards?.removeAt(index);
    }

    protected addSupportedDashbard(input: HTMLInputElement): void{
        const inputValue = input.value;
        if(!inputValue){
            return;
        }
        this.supportedDashboards?.push(
            this.formBuilder.control(inputValue)
        )

        input.value = '';
    }



    protected downloadJSON(): void{
        if(!this.formGroup?.value){
            return;
        }
		const blob = new Blob([JSON.stringify(this.formGroup.value)], {type: "application/json;charset=utf-8"});
		FileSaver.saveAs(blob, "configuration.json");
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

        if(!this.loadedSettings){
            return;
        }

        this.pendingBlockingRequest = true;

        this.userSettingsService.persist({
            id: this.loadedSettings?.id,
            entityId: this.loadedSettings?.entityId,
            key: this.loadedSettings?.key,
            entityType: UserSettingsEntityType.Application,
            type: UserSettingsType.GlobalSearch,
            value: JSON.stringify(this.formGroup.value),
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


}