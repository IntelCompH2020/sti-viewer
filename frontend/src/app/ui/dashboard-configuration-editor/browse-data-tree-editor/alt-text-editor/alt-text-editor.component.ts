import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl: './alt-text-editor.component.html',
    styleUrls: [
        './alt-text-editor.component.scss'
    ],
    selector: 'app-alt-text-editor'
})
export class AltTextEditorComponent{

    @Input()formGroup : FormGroup;

    constructor(){

    }
}