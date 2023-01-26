import { Component, Input } from "@angular/core";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { SearchConfigurationStaticKeywordFilterEditorModel } from "../search-configuration-editor.model";

@Component({
    templateUrl: './static-filters-editor.component.html',
    styleUrls: [
        './static-filters-editor.component.scss'
    ],
    selector: 'app-search-configuration-static-filters-editor'
})
export class SearchConfigurationStaticFiltersEditorComponent{

    @Input()formGroup : FormGroup;


    get keywordFiltersArray(): FormArray | undefined | null{
        return this.formGroup?.get('keywordsFilters') as FormArray;
    }
    constructor(private formBuilder: FormBuilder){

    }


    protected removeKeywordFilter(index: number): void{
        this.keywordFiltersArray?.removeAt(index);
    }


    protected addKeywordFilter(): void{
        this.keywordFiltersArray?.push(
            new SearchConfigurationStaticKeywordFilterEditorModel().buildForm()
        )
    }


    protected addKeywordFilterValue(input: HTMLInputElement, keywordFilterIndex: number): void{
        const inputValue = input.value;
        if(!inputValue){
            return;
        }

        (this.keywordFiltersArray.at(keywordFilterIndex)?.get('value') as FormArray)?.push(
            this.formBuilder.control(inputValue)
        )

        input.value = '';
    }

    protected removeKeywordFilterValue(keywordFilterIndex: number, valueIndex: number ): void{
        (this.keywordFiltersArray.at(keywordFilterIndex).get('value') as FormArray)?.removeAt(valueIndex);
    }
}