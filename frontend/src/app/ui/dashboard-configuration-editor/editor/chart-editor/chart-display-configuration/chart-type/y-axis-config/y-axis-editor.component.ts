import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl:'./y-axis-editor.component.html',
    selector: 'app-y-axis-editor'
})

export class YaxisEditorComponent{
    @Input()
    formGroup: FormGroup;

    
}