import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

@Component({
    templateUrl: './edit-map-mapping-dialog.component.html',
})

export class EditMapMappingDialogComponent implements OnInit{


    formGroup: FormGroup;

    constructor(
        @Inject(MAT_DIALOG_DATA) private data,
        private dialogRef: MatDialogRef<EditMapMappingDialogComponent>,
        private formBuilder: FormBuilder
    ){

    }
    ngOnInit(): void {
        if(!this.data.mapping){
            return;
        }


        this.formGroup = this.formBuilder.group(this.data.mapping);
    }



    cancel(): void{
        this.dialogRef.close();
    }

    submit(): void{
        this.dialogRef.close(
            this.formGroup?.value
        )
    }
}