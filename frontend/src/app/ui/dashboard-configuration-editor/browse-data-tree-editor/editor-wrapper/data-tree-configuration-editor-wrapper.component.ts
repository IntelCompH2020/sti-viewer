import { Component, OnInit, ViewChild } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, NgModel } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { UserSettings, UserSettingsEntityType, UserSettingsType } from "@app/core/model/user/user-settings.model";
import { Guid } from "@common/types/guid";
import { Observable, of } from "rxjs";
import { filter, map , switchMap, takeUntil, tap} from "rxjs/operators";
import { nameof } from "ts-simple-nameof";
import { BrowseDataTreeEditorModel } from "../browse-data-tree-editor.model";
import { UserSettingsService } from '@app/core/services/http/user-settings.service';
import * as FileSaver from 'file-saver';
import { MatDialog } from "@angular/material/dialog";
import { ConfirmationDeleteDialogComponent } from "@common/modules/confirmation-dialog/confirmation-delete-dialog/confirmation-delete-dialog.component";
import { BasePendingChangesComponent } from "@common/base/base-pending-changes.component";
import { SnackBarNotificationLevel, UiNotificationService } from "@common/modules/notification/ui-notification-service";
import { TranslateService } from "@ngx-translate/core";
// import { cleanUpEmpty } from "../../editorUtils";


@Component({
    templateUrl: './data-tree-configuration-editor-wrapper.component.html',
    styleUrls:[
        './data-tree-configuration-editor-wrapper.component.scss'
    ]
})
export class DataTreeConfigurationEditorWrapperComponent extends BasePendingChangesComponent implements OnInit{
    @ViewChild('configurationKey') configurationKey: NgModel;

    canDeactivate(): boolean | Observable<boolean> {
        return !this.formArray?.dirty;
    }


    pendingBlockingRequest: boolean = false;

    constructor(
        private formBuilder: FormBuilder, 
        private router: Router, 
        private route: ActivatedRoute, 
        private dialog: MatDialog, 
        private userSettingsService: UserSettingsService,
        private uiNotificationsService: UiNotificationService,
        private language: TranslateService
        ){
        super();
    }


    loadedSettings: UserSettings = {} as UserSettings;

    formArray: FormArray;
    protected selectedFormGroup: FormGroup;
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


					return this.userSettingsService.getSingle(id as unknown as Guid, fields).pipe(
                        tap(x => {this.loadedSettings = x}),
						map(x => JSON.parse(x.value))
					)
				}
				return of([]);
			}),
			filter(x => x),
			takeUntil(this._destroyed)
		)
		.subscribe(response => {
            this.formArray = this.formBuilder.array(
                response.map(item => new BrowseDataTreeEditorModel().fromModel(item).buildForm())
            )
            
            if(response.length){
                this.selectedFormGroup = this.formArray.at(0) as FormGroup;
            }
            
		})


    }


    protected selectGroup(index: number): void{
        this.selectedFormGroup = this.formArray.at(index) as FormGroup;
    }


    protected addConfiguration(): void{
        const fg = new BrowseDataTreeEditorModel().buildForm();
        this.formArray.push(fg);
        this.selectedFormGroup = fg;

    }


    protected removeConfiguration(index: number): void{
        this.dialog.open(ConfirmationDeleteDialogComponent)
            .afterClosed()
            .pipe(
                filter(x => x),
                takeUntil(this._destroyed)
            )
            .subscribe(() =>{
                if(this.selectedFormGroup === this.formArray.at(index)){
                    if(index !== 0 ){
                        this.selectedFormGroup = this.formArray.at(index -1) as FormGroup;
                    }else if(this.formArray.length === 1){
                        this.selectedFormGroup = null;
                    }else{
                        this.selectedFormGroup = this.formArray.at(index +1) as FormGroup;
                    }
                }
        
                this.formArray.removeAt(index);
            })


        
    }


    downloadJSON(): void{
        const blob = new Blob([JSON.stringify(this.formArray.value)], {type: "application/json;charset=utf-8"});
		FileSaver.saveAs(blob, "configuration.json");
    }


    save(): void{

        if(!this.configurationKey.valid){
            this.configurationKey.control.markAsTouched();
            return;
        }

        if(!this.formArray?.valid){
            this.formArray.markAllAsTouched();
            return;
        }

        if(!this.loadedSettings){
            return;
        }

        this.pendingBlockingRequest = true;
        this.userSettingsService.persist({
            id: this.loadedSettings?.id,
            entityId: this.loadedSettings?.entityId,
            key: this.loadedSettings.key,
            entityType: UserSettingsEntityType.Application,
            type: UserSettingsType.BrowseDataTree,
            value: JSON.stringify(this.formArray.value),
            hash: this.loadedSettings?.hash
        })
        .pipe(
            takeUntil(this._destroyed)
        )
        .subscribe(
            response => {
                this.formArray.markAsPristine();
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
        );
    }

}