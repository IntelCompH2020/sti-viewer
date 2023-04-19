import {Component, Inject} from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ElasticOrderEnum } from '@app/core/enum/elastic-order.enum';
import { IndicatorPointDistinctLookup } from '@app/core/query/IndicatorPointDistinctLookup';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { BaseComponent } from '@common/base/base.component';
import { Guid } from '@common/types/guid';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { map, debounceTime, switchMap } from 'rxjs/operators';


@Component({
    templateUrl:'./new-column-request-dialog.component.html',
    styleUrls:[
        './new-column-request-dialog.component.scss'
    ],
})

export class NewColumnRequesDialogComponent extends BaseComponent{



    indicatorIds: Guid[] = []
    column: string = '';

    control: FormControl = new FormControl();

    searchTerm: string = '';
    private searchTerm$ = new BehaviorSubject<string>(this.searchTerm);

    selectedItems = new Set<string>();
    exludedItems:  string[] = [];

    results: string[];

    title: string;

    constructor(
        private dialogRef: MatDialogRef<NewColumnRequesDialogComponent, string[]>,
        private indicatorPointService: IndicatorPointService,
        @Inject(MAT_DIALOG_DATA) data: NewColumnRequesDialogComponentParams,
        
        
    ){
        super();
        this.title = data?.title;
        this.column = data?.column;
        this.indicatorIds = data?.indicatorIds ?? [];
        this.exludedItems = data?.exludedItems ?? []

        this.registerListener();
        
    }



    private registerListener(){
        this.searchTerm$.pipe(
            debounceTime(600),
            switchMap(
                searchQuery => this.indicatorPointService.getIndicatorPointQueryDistinct(this._buildLookup({searchQuery, excludedItems: this.exludedItems}))
            )
        ).subscribe(results => {
            
            this.results = results?.items ?? []
        })
    }

    protected onSelectionChange(event: MatCheckboxChange, item: string): void{
        if(event.checked){
            this.selectedItems.add(item);
            return;
        }
        this.selectedItems.delete(item);
    }


    protected isItemSelected(item: string): boolean{
        return this.selectedItems.has(item);
    }

    protected onSearchTermChange():void{
        this.searchTerm$.next(this.searchTerm);
    }

    protected submit():void{
        this.dialogRef.close([...this.selectedItems]);
    }

    protected cancel(): void{
        this.dialogRef.close(null);
    }

    private _buildLookup(params: {searchQuery?: string, excludedItems?: any[]}): IndicatorPointDistinctLookup{

        const {searchQuery, excludedItems} = params;

        const lookup = new IndicatorPointDistinctLookup();

        lookup.indicatorIds = this.indicatorIds;
        lookup.field = this.column;
        lookup.order  = ElasticOrderEnum.ASC;
        lookup.viewNotApprovedValues = true;
        lookup.batchSize = 20

        if(this.exludedItems?.length){
            lookup.excludedValues = this.exludedItems;
        }

        if(searchQuery){
            lookup.like = `%${searchQuery}%`;
        }

        if(excludedItems){
        }

        return lookup;
    }
}


export interface NewColumnRequesDialogComponentParams{
    column: string;
    indicatorIds: Guid[];
    exludedItems: string[];
    title: string
}
