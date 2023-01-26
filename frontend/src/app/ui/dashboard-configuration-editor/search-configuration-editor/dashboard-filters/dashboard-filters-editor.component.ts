import { Component, Input } from "@angular/core";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { SearchConfigurationDashboardStaticKeywordFilterEditorModel } from "../search-configuration-editor.model";

@Component({
    templateUrl: './dashboard-filters-editor.component.html',
    styleUrls: [
        './dashboard-filters-editor.component.scss'
    ],
    selector: 'app-dashboard-configuration-static-filters-editor'
})
export class SearchConfigurationDashboardFiltersEditorComponent{

    @Input()formGroup : FormGroup;

    get staticFilters(): FormGroup | null | undefined{
        return this.formGroup?.get('staticFilters') as FormGroup;
    }

    get keywordsFilters(): FormArray | null | undefined{
        return this.staticFilters?.get('keywordsFilters') as FormArray;
    }

    constructor(private formBuilder: FormBuilder){

    }


    protected removeKeywordFilter(index: number): void{
        this.keywordsFilters?.removeAt(index);
    }

    protected addKeywordFilter(): void{
        this.keywordsFilters?.push(
            new SearchConfigurationDashboardStaticKeywordFilterEditorModel().buildForm()
        )
    }


    protected addKeywordFilterValue(input: HTMLInputElement, keywordFilterIndex: number): void{
        const inputValue = input.value;
        if(!inputValue){
            return;
        }

        (this.keywordsFilters?.at(keywordFilterIndex)?.get('value') as FormArray)?.push(
            this.formBuilder.control(inputValue)
        )

        input.value = '';
    }

    protected removeKeywordFilterValue(keywordFilterIndex: number, valueIndex: number ): void{
        (this.keywordsFilters?.at(keywordFilterIndex).get('value') as FormArray)?.removeAt(valueIndex);
    }
}