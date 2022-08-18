import { ThisReceiver } from "@angular/compiler";
import { Component, Inject, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { ElasticOrderEnum } from "@app/core/enum/elastic-order.enum";
import { Indicator } from "@app/core/model/indicator/indicator.model";
import { IndicatorPointDistinctLookup } from "@app/core/query/IndicatorPointDistinctLookup";
import { IndicatorPointService } from "@app/core/services/http/indicator-point.service";
import { BaseComponent } from "@common/base/base.component";
import { AtLeastItemsValidator } from "@common/forms/validation/custom-validator";
import { Subject } from "rxjs";
import { debounceTime, switchMap, takeUntil } from "rxjs/operators";
import { NewGroupDefinition } from "../my-indicator-columns-editor/my-indicator-columns-editor.component";

@Component({
    templateUrl:'./group-generator.component.html',
    styleUrls:[
        './group-generator.component.scss'
    ]
})

export class GroupGeneratorComponent extends BaseComponent implements OnInit{

    private _BATCH_SIZE = 10

    column: string;
    indicators: Indicator[];

    fields: string[];
    fieldsAvailable: number;

    get selectedFields(): readonly string[]{
        return this._selectedFieldsFormArray.value;
    };

    private _afterKey: Map<string, object>;

    searchTerm: string;


    private searchTermSubject$ = new Subject<string>();

    formGroup: FormGroup<{
        name: FormControl<string>;
        items: FormArray<FormControl<string>>
    }>



    get nameControl(): FormControl{
        return this.formGroup?.get('name') as FormControl;
    }


    private get _selectedFieldsFormArray(): FormArray{
        return this.formGroup.get('items') as FormArray;
    }

    get atLeastError(): boolean{
        return this._selectedFieldsFormArray?.errors?.atLeastItems && this._selectedFieldsFormArray.touched;
    }

    constructor(
        private indicatorPointService: IndicatorPointService,
        private dialogRef: MatDialogRef<GroupGeneratorComponent>,
        private formBuilder: FormBuilder,
        @Inject(MAT_DIALOG_DATA) private data
    ){
        super();
        this.indicators = data.indicators;
        this.column = data.column;
    }

    ngOnInit(): void {
        this._loadFields({});
        this._registerSearchTextListener();

        this._buildForm(this.data.name ?? '', this.data.values ?? []);
        
    }

    onSelectChange(event: MatCheckboxChange, field: string): void {
        if(event.checked){
            this._selectedFieldsFormArray.push(this.formBuilder.control(field));
            return;
        }


        const index = this.selectedFields.findIndex(val => val === field);
        if(index >= 0){
            this._selectedFieldsFormArray.removeAt(index);
        }

    }

    removeSelectedField(index: number): void {
        this._selectedFieldsFormArray.removeAt(index);
    }

    onLoadMore(): void {
        if(!(this.fieldsAvailable > this.fields.length)){
            return;
        }
        this._loadFields({keepPrevious: true});
    }

    

    onSearchTextChange(searchText: string): void {
        this.searchTermSubject$.next(searchText);
    }

    submit(): void {
        if(this.formGroup.invalid){
            this.formGroup.markAllAsTouched();
            return;
        }

        const groupDefinition: NewGroupDefinition = this.formGroup.getRawValue();
        this.dialogRef.close(
            groupDefinition
        );
    }

    cancel(): void {
        this.dialogRef.close();
    }


    private _buildForm(name: string = '', items: string[] =[]):void{
        this.formGroup = this.formBuilder.group({
            name: [name, Validators.required],
            items: this.formBuilder.array<string>(items, [AtLeastItemsValidator(1)])
        })
    }

    private _loadFields(options:{keepPrevious?: boolean}): void{

        const {keepPrevious} = options;

        this.indicatorPointService.getIndicatorPointQueryDistinct(this._buildLookup({keepPrevious}))
            .pipe(
                takeUntil(this._destroyed)
            )
            .subscribe(
                response =>{
                    this.fieldsAvailable = response.count;
                    this._afterKey = response.afterKey;

                    if(keepPrevious){
                        this.fields.push(...response.items);
                        return;
                    }
                    this.fields = response.items;
                }
        )
    }


    private _registerSearchTextListener(): void{
        this.searchTermSubject$
            .asObservable()
            .pipe(
                takeUntil(this._destroyed),
                debounceTime(400),
                switchMap(
                        searchTerm => this.indicatorPointService.getIndicatorPointQueryDistinct(
                            this._buildLookup({like: searchTerm})
                        )
                    )
            )
            .subscribe(
                response =>{
                    this._afterKey = response.afterKey;
                    this.fieldsAvailable = response.count;
                    this.fields = response.items;
                },
                _error => {
                    this._registerSearchTextListener();
                }
            )
    }

    private _buildLookup(options:{keepPrevious?: boolean, like?: string}): IndicatorPointDistinctLookup{

        const {keepPrevious, like} = options;

        const lookup = new IndicatorPointDistinctLookup();

        lookup.indicatorIds = this.indicators.map(indicator => indicator.id);
        lookup.field = this.column;
        lookup.order  = ElasticOrderEnum.ASC;
        lookup.batchSize = this._BATCH_SIZE;


        if(keepPrevious && this._afterKey){
            lookup.afterKey = this._afterKey;
        }


        if(like !== null && like !== undefined){
            lookup.like = `%${like}%`;
        }

        return lookup;
    }
}