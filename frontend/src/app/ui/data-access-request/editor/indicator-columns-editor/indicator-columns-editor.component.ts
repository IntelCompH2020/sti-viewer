import { Component, Input, OnInit } from "@angular/core";
import { FormControl} from "@angular/forms";
import { ElasticOrderEnum } from "@app/core/enum/elastic-order.enum";
import { IndicatorPointDistinctLookup } from "@app/core/query/IndicatorPointDistinctLookup";
import { IndicatorPointService } from "@app/core/services/http/indicator-point.service";
import { MultipleAutoCompleteConfiguration } from "@common/modules/auto-complete/multiple/multiple-auto-complete-configuration";
import { Guid } from "@common/types/guid";
import { Observable, of } from "rxjs";
import { map } from "rxjs/operators";

@Component({
    templateUrl: './indicator-columns-editor.component.html',
    styleUrls:[
        './indicator-columns-editor.component.scss'
    ],
    selector: 'app-indicator-columns-editor',
})
export class IndicatorColumnsEditorComponent implements OnInit{


    @Input()
    column: string;

    @Input()
    indicatorIds: Guid[];

    @Input()
    control: FormControl<string[]>;

    @Input()
    disabled: boolean = false;


    constructor(
        private indicatorPointService: IndicatorPointService
        ){

    }

    autoCompleteConfiguration : MultipleAutoCompleteConfiguration = {
        getSelectedItems:(selectedItems) => this._getSelectedItems(selectedItems),
        valueAssign: item => item ,
        initialItems: (excludedItems) => this.indicatorPointService.getIndicatorPointQueryDistinct(this._buildLookup({excludedItems})).pipe(map(x => x.items)),
        filterFn: (searchQuery, excludedItems) => this.indicatorPointService.getIndicatorPointQueryDistinct(this._buildLookup({searchQuery, excludedItems})).pipe(map(x => x.items)) 
    }

    ngOnInit(): void {
    }


    private _buildLookup(params: {searchQuery?: string, excludedItems?: any[]}): IndicatorPointDistinctLookup{

        const {searchQuery, excludedItems} = params;

        const lookup = new IndicatorPointDistinctLookup();

        lookup.indicatorIds = this.indicatorIds;
        lookup.field = this.column;
        lookup.order  = ElasticOrderEnum.ASC;
        lookup.viewNotApprovedValues = true;
        lookup.batchSize = 20


        if(searchQuery){
            lookup.like = `%${searchQuery}%`;
        }

        if(excludedItems){
        }

        return lookup;
    }

    private _getSelectedItems(selectedItems: any[]): Observable<any[]>{
        return of(selectedItems);
    }

}