import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { SerieValueTestPipe } from "../serie-value-test.pipe";

@Component({
    templateUrl: './value-test-dialog.component.html'
})
export class ValueTestDialogComponent implements OnInit{
    formGroup: FormGroup


    constructor(@Inject(MAT_DIALOG_DATA) private data, private dialogRef: MatDialogRef<ValueTestDialogComponent>, private formBuilder: FormBuilder ){
    }
    ngOnInit(): void {
        const testPipe = new SerieValueTestPipe();
        this.formGroup = this.formBuilder.group({
            target: testPipe.transform(this.data?.test,'target' ),
            value: testPipe.transform(this.data?.test,'value' ),
        })       
    }


    submit(): void{
        const {target, value} = this.formGroup.value;
        this.dialogRef.close({[target]: [value]})
    }

    cancel(): void{
        this.dialogRef.close(false);
    }

}