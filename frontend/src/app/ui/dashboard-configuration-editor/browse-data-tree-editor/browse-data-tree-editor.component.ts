import { Component, Input, OnInit, ViewChild } from "@angular/core";
import { FormArray, FormGroup } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatTabGroup } from "@angular/material/tabs";
import { BaseComponent } from "@common/base/base.component";
import { ConfirmationDeleteDialogComponent } from "@common/modules/confirmation-dialog/confirmation-delete-dialog/confirmation-delete-dialog.component";
import { filter, takeUntil } from "rxjs/operators";
import { BrowseDataTreeLevelConfigEditorModel } from "./browse-data-tree-editor.model";

@Component({
    templateUrl: './browse-data-tree-editor.component.html',
    styleUrls: [
        './browse-data-tree-editor.component.scss'
    ],
    selector: 'app-browse-data-tree-editor'
})
export class BrowseDataTreeEditorComponent extends BaseComponent implements OnInit{

    @ViewChild(MatTabGroup) tabGroup: MatTabGroup;

    @Input()
    formGroup: FormGroup;

    protected get levelConfigs(): FormArray | undefined | null {
        return this.formGroup.get('levelConfigs') as FormArray;
    }

    constructor(private dialog: MatDialog){
        super();
    }

    ngOnInit(): void {

    }


    removeLevelConfig(index: number): void{

        this.dialog.open(ConfirmationDeleteDialogComponent)
        .afterClosed()
        .pipe(
            filter(x => x),
            takeUntil(this._destroyed)
        )
        .subscribe(() =>{
            this.levelConfigs?.removeAt(index);
    
            if(index ! === 0){
                this.tabGroup.selectedIndex = this.tabGroup.selectedIndex - 1;
            }
        })



    }


    addLevelConfig(): void{
        this.levelConfigs?.push(new BrowseDataTreeLevelConfigEditorModel().buildForm())
        this.tabGroup.selectedIndex = this.levelConfigs.length;
    }

}