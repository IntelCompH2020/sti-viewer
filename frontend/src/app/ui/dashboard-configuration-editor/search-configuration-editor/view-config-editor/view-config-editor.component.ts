import { Component, Input } from "@angular/core";
import { FormArray, FormGroup } from "@angular/forms";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";
import { SearchViewConfigType } from "@app/ui/search/search-config.model";
import { SearchViewFieldConfigEditorModel } from "../search-configuration-editor.model";

@Component({
    templateUrl: './view-config-editor.component.html',
    styleUrls: [
        './view-config-editor.component.scss'
    ],
    selector: 'app-view-config-editor'
})
export class ViewConfigEditorComponent{

    @Input()formGroup : FormGroup;


    protected searchConfigViewTypes : SearchViewConfigType[] = this.enumUtils.getEnumValues<SearchViewConfigType>(SearchViewConfigType);

    get fields(): FormArray | undefined | null{
        return this.formGroup?.get('fields') as FormArray;
    }

    constructor(private enumUtils: AppEnumUtils){

    }


    protected addField(): void{
        this.fields?.push(
            new SearchViewFieldConfigEditorModel().buildForm()
        )
    }

    protected removeField(index: number): void{
        this.fields?.removeAt(index);
    }
}