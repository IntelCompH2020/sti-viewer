import { Component, Input } from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { AreaZoom } from "@app/ui/indicator-dashboard/indicator-dashboard-config";

@Component({
    templateUrl: './data-zoom-editor.component.html',
    selector: 'app-data-zoom-editor'
})
export class DataZoomEditorComponent{
    @Input()
    formGroup: FormGroup;

    

    get areaZoom(): FormGroup | null | undefined{
        return this.formGroup?.get('areaZoom') as FormGroup;
    }

    constructor(private formBuilder: FormBuilder){

    }

    onAreaZoomChange(event: MatCheckboxChange): void{
        if(event.checked){
            if( this.areaZoom){
                return;
            }
            const areaZoom: AreaZoom = {
                end: null,
                start: null
            }

            this.formGroup.addControl('areaZoom', this.formBuilder.group(areaZoom));
            return;
        }

        // * removing control
        if(!this.areaZoom){
            return;
        }

        this.formGroup.removeControl('areaZoom');
    }

}