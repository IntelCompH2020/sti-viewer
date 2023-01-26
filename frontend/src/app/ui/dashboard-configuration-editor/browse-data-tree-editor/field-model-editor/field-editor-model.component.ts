import { Component, Input } from "@angular/core";
import { FormArray, FormGroup } from "@angular/forms";
import { IndicatorFieldBaseType } from "@app/core/enum/indicator-field-base-type.enum";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";
import { AltTextEditorModel, OperatorEditorModel } from "../browse-data-tree-editor.model";

@Component({
    templateUrl: './field-editor-model.component.html',
    styleUrls: [
        './field-editor-model.component.scss'
    ],
    selector: 'app-field-model-editor'
})
export class FieldModelEditorComponent{

    @Input()formGroup : FormGroup;

    // get altLabelsArray(): FormArray | undefined | null{
    //     return this.formGroup.get('altLabels') as FormArray;
    // }

    // get altDescriptionsArray(): FormArray | undefined | null{
    //     return this.formGroup.get('altDescriptions') as FormArray;
    // }

    // get valueRange(): FormGroup | undefined | null{
    //     return this.formGroup.get('valueRange') as FormGroup;
    // }

    // get operationsArray(): FormArray | undefined | null{
    //     return this.formGroup.get('operations') as FormArray;
    // }


    // baseTypes: IndicatorFieldBaseType [] = this.enumUtils.getEnumValues<IndicatorFieldBaseType>(IndicatorFieldBaseType);

    constructor(private enumUtils: AppEnumUtils){

    }



    // protected addAltLabel(): void{
    //     this.altLabelsArray?.push(new AltTextEditorModel().buildForm());
    // }
    // protected removeAltLabel(index: number): void{
    //     this.altLabelsArray?.removeAt(index);
    // }

    // protected addAltDescription(): void{
    //     this.altDescriptionsArray?.push(new AltTextEditorModel().buildForm());
    // }
    // protected removeAltDescription(index: number): void{
    //     this.altDescriptionsArray?.removeAt(index);
    // }

    // protected addOperation(): void{
    //     this.operationsArray?.push(new OperatorEditorModel().buildForm());
    // }
    // protected removeOperation(index: number): void{
    //     this.operationsArray?.removeAt(index);
    // }


}