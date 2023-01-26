import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl: './polar-bar-configuration.component.html',
    selector: 'app-polar-bar-configuration-editor'
})
export class PolarBarConfigurationEditor{
    
    @Input()
    formGroup: FormGroup;

    get dataZoom (): FormGroup | undefined | null{
        return this.formGroup?.get('dataZoom') as FormGroup;
    }

    get radiusAxis (): FormGroup | undefined | null{
        return this.formGroup?.get('radiusAxis') as FormGroup;
    }

    
}