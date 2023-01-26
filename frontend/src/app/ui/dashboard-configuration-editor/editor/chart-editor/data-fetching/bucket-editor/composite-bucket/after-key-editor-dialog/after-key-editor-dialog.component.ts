import { Component, Inject, OnInit} from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";


@Component({
    templateUrl: './after-key-editor-dialog.component.html',
})
export class AfterKeyEditorDialogComponent implements OnInit{

    formGroup: FormGroup;


    constructor(
         @Inject(MAT_DIALOG_DATA) private data ,
        private dialogRef: MatDialogRef<AfterKeyEditorDialogComponent>,
        private formBuilder: FormBuilder
    ){

    }
    ngOnInit(): void {
        const afterKey = this.data?.afterKey;
        if(!afterKey){
            return;
        }
        this.formGroup = this.formBuilder.group(afterKey);
    }


    protected submit(): void{
        if(!this.formGroup?.valid){
            return;
        }

        this.dialogRef.close(this.formGroup.value);
    }

    protected close(): void{
        this.dialogRef.close(false);
    }

}
