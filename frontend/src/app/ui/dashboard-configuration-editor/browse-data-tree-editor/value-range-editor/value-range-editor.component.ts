import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl: './value-range-editor.component.html',
    styleUrls: [
        './value-range-editor.component.scss'
    ],
    selector: 'app-value-range-editor'
})
export class ValueRangeEditorComponent{

    @Input()formGroup : FormGroup;

    constructor(){

    }
}