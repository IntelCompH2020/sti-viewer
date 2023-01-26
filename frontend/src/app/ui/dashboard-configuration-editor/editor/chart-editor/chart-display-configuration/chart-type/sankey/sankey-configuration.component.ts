import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl: './sankey-configuration.component.html',
    selector: 'app-sankey-configuration-editor'
})
export class SankeyConfigurationEditor{
    
    @Input()
    formGroup: FormGroup;

    
}