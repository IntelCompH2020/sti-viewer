import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { FormControl, FormGroup } from "@angular/forms";
import { nameof } from "ts-simple-nameof";

@Component({
    templateUrl: './tree-map-configuration.component.html',
    selector: 'app-tree-map-configuration-editor'
})
export class TreeMapConfigurationEditorComponent implements OnChanges{
    

    @Input()
    formGroup: FormGroup;

    get toolTip(): FormGroup | undefined | null{
        return this.formGroup?.get('toolTip') as FormGroup;
    }


    get treeColors(): FormControl | undefined | null{
        return this.formGroup?.get('treeColors') as FormControl;
    }

    protected treeColorsArray: TreeColorItem[] = [];



    ngOnChanges(changes: SimpleChanges): void {
        if(changes[nameof<TreeMapConfigurationEditorComponent>(x => x.formGroup)]){

            this.treeColorsArray = [];
            if(!this.formGroup){
                return;
            }
            this._buildTreeColors();
        }
    }


    protected removeColorItem(index: number): void{
        const name = this.treeColorsArray[index]?.name;
        let colorValues = this.treeColors?.value;
        if(!colorValues){
            return;
        }

        colorValues = {...colorValues};
        delete colorValues[name];

        this.treeColors.setValue(colorValues);
        this._buildTreeColors();

    }

    protected addColorItem(nameInput: HTMLInputElement, color: HTMLInputElement): void{
        const nameValue = nameInput?.value;
        const colorValue = color?.value;

        if(!nameValue || !colorValue){
            return;
        }


        const treeColorValues = this.treeColors?.value ?? {};

        this.treeColors.setValue({...treeColorValues, [nameValue]: colorValue});
        this._buildTreeColors();

        nameInput.value = '';
        color.value = '';
    }


    private _buildTreeColors(): void{
        const colorsValue = this.treeColors?.value;

        if(!colorsValue){
            return;
        }
        this.treeColorsArray = Object.keys(colorsValue).map(name => ({name, color: colorsValue[name]}));
    }
}



interface TreeColorItem{
    name: string;
    color: string;
}