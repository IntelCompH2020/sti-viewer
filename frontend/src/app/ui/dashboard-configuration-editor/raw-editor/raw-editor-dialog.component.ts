import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";



@Component({
    templateUrl:'./raw-editor-dialog.component.html',
    styleUrls:['./raw-editor-dialog.component.scss']
})
export class RawEditorDialogComponent{

    protected jsonConfigs:RawEditorOutputData[] = [];


    protected invalidConfigs: string;

    constructor(
        private dialogRef: MatDialogRef<RawEditorDialogComponent, RawEditorOutputData[]>,
        @Inject(MAT_DIALOG_DATA) data: RawEditorInputData
    ){
        this.jsonConfigs = data?.items?.map(item => ({
            ...item,
            value: JSON.stringify(item.value, undefined, 4)
        }));
    }



    close():void{
        this.dialogRef.close();
    }

    submit(): void{
        this.invalidConfigs = '';
        this.jsonConfigs.forEach(config => {
            try {
                JSON.parse(config.value);
            } catch (error) {
                this.invalidConfigs += (this.invalidConfigs ? ', ': '') + config.name;
            }
        });

        if(this.invalidConfigs){
            return;
        }


        this.dialogRef.close(this.jsonConfigs);
    }

}

export interface RawEditorInputData{
    items:{
        name: string;
        value: Object,
        key: string
    }[]
}


export interface RawEditorOutputData {
    name: string;
    value: string;
    key: string;
}