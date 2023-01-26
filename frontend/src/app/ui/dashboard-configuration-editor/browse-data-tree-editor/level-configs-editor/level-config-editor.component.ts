import { Component, Input } from "@angular/core";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { MatDialog } from "@angular/material/dialog";
import { BaseComponent } from "@common/base/base.component";
import { ConfirmationDeleteDialogComponent } from "@common/modules/confirmation-dialog/confirmation-delete-dialog/confirmation-delete-dialog.component";
import { filter, takeUntil } from "rxjs/operators";
import { BrowseDataTreeLevelDashboardOverrideEditorModel, DataTreeLevelDashboardOverrideFieldRequirementEditorModel } from "../browse-data-tree-editor.model";

@Component({
    templateUrl: './level-config-editor.component.html',
    styleUrls: [
        './level-config-editor.component.scss'
    ],
    selector: 'app-level-config-editor'
})
export class LevelConfigEditorComponent extends BaseComponent{

    @Input()formGroup : FormGroup;


    get dashboardOverrides(): FormArray | undefined | null{
        return this.formGroup?.get('dashboardOverrides') as FormArray;
    }

    constructor(private formBuilder: FormBuilder, private dialog: MatDialog){
        super();
    }


    protected addItemInArray( input: HTMLInputElement, array: FormArray): void{
        const inputValue = input.value;
        if(!inputValue){
            return;
        }

        array.push(this.formBuilder.control(inputValue));
        input.value = '';
    }

    protected removeItemFromArray(index: number, array: FormArray): void{
        array.removeAt(index);
    }


    protected addDashboardOverride(): void{
        this.dashboardOverrides?.push(new BrowseDataTreeLevelDashboardOverrideEditorModel().buildForm());
    }

    protected removeDashboardOverride(index: number): void{
        this.dialog.open(ConfirmationDeleteDialogComponent)
        .afterClosed()
        .pipe(
            filter(x => x),
            takeUntil(this._destroyed)
        ).subscribe( () =>{
            this.dashboardOverrides?.removeAt(index);
        })
    }


    protected addRequirement(dashBoardOverride: FormGroup): void{
        (dashBoardOverride?.get('requirements') as FormArray)?.push(
            new DataTreeLevelDashboardOverrideFieldRequirementEditorModel().buildForm()
        )
    }

    protected removeRequirement(dashBoardOverride: FormGroup, index: number): void{
        (dashBoardOverride?.get('requirements') as FormArray)?.removeAt(index);
    }



    protected onDefaultDashboardChange(event: MatCheckboxChange): void{

        if(event.checked){
            
            if(this.formGroup.get('defaultDashboards')){
                return;
            }

            this.formGroup.addControl(
                'defaultDashboards',
                this.formBuilder.array([])
            )

            return;
            
        }

        if(!this.formGroup.get('defaultDashboards')){
            return;
        }
        this.formGroup.removeControl('defaultDashboards');

    }

    protected onDashboardOverrideChange(event: MatCheckboxChange): void{
        
        if(event.checked){
            
            if(this.formGroup.get('dashboardOverrides')){
                return;
            }

            this.formGroup.addControl(
                'dashboardOverrides',
                this.formBuilder.array([])
            )

            return;
            
        }

        if(!this.formGroup.get('dashboardOverrides')){
            return;
        }
        this.formGroup.removeControl('dashboardOverrides');

    }
}