import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl: './operation-editor.component.html',
    styleUrls: [
        './operation-editor.component.scss'
    ],
    selector: 'app-operation-editor'
})
export class OperationEditorComponent{

    @Input()formGroup : FormGroup;

    constructor(){

    }
}