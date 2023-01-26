import {Component, Input} from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
    styleUrls:[
        './gauge-general-info-editor.component.scss',

    ],
    templateUrl:'./gauge-general-info-editor.component.html',
    selector:'app-gauge-general-info-editor'
})

export class GaugeGeneralInfoEditorComponent{


    @Input()
    formGroup: FormGroup;


}