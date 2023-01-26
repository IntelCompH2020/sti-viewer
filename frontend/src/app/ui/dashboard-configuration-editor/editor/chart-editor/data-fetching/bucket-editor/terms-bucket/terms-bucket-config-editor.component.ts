import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl: './terms-bucket-config-editor.component.html',
    selector: 'terms-bucket-config-editor'
})
export class TermsBucketConfigEditorComponent{

    @Input()
    formGroup: FormGroup;

    constructor(

    ){

    }

}