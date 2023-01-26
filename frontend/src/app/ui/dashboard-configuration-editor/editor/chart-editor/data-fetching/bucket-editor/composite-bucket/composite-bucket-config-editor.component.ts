import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { MatDialog } from "@angular/material/dialog";
import { CompositeBucketConfigEditorModel, CompositeSourceEditorModel, DataHistogramBucketConfigEditorModel } from "@app/ui/dashboard-configuration-editor/editor/bucket-editor-models.model";
import { BaseComponent } from "@common/base/base.component";
import { filter, takeUntil } from "rxjs/operators";
import { AfterKeyEditorDialogComponent } from "./after-key-editor-dialog/after-key-editor-dialog.component";

@Component({
    templateUrl: './composite-bucket-config-editor.component.html',
    selector: 'composite-bucket-config-editor'
})
export class CompositeBucketConfigEditorComponent extends BaseComponent implements OnChanges{

    @Input()
    formGroup: FormGroup;

    // todo investigate
    get histogramBucket(): FormGroup | null | undefined{
        return this.formGroup?.get(CompositeBucketConfig.DateHistogramSource) as FormGroup;
    }

    get sources(): FormArray| null | undefined{
        return this.formGroup?.get(CompositeBucketConfig.Sources) as FormArray;
    }

    get afterKeyControl(): FormControl | null |undefined{
        return this.formGroup?.get('afterKey') as FormControl;
    }


    CompositeBucketConfig = CompositeBucketConfig;

    protected afterKeys: AfterKey[] = [];

    constructor(
        private dialog: MatDialog,
        private formBuilder: FormBuilder
    ){

        super();
    }
    ngOnChanges(changes: SimpleChanges): void {
        if(changes['formGroup']){
            this._buildAfterKeys();
        }
    }


    protected removeSource(index: number): void{
        this.sources?.removeAt(index);
    }

    protected addSource(): void{
        this.sources?.push(
            new CompositeSourceEditorModel().buildForm()
        )
    }



    protected addAfterKey(key: HTMLInputElement, value: HTMLInputElement):void{

        const keyValue = key?.value;
        const valueValue = value?.value;

        if(!keyValue || !valueValue){
            return;
        }

        const records = this.afterKeyControl?.value ?? {};
        this.afterKeyControl?.setValue({...records, [keyValue]: valueValue});


        this._buildAfterKeys();
        key.value = '';
        value.value = '';
    }

    protected editAfterKey(afterKey: AfterKey): void{
        this.dialog.open(AfterKeyEditorDialogComponent, {
            data: {
                afterKey
            }
        })
        .afterClosed()
        .pipe(
            filter(x => x),
            takeUntil(this._destroyed)
        )
        .subscribe(
            update => {
                let records = this.afterKeyControl?.value;
                if(!records){
                    return;
                }
                delete records[afterKey.key];
                records = {...records, [update.key]: update.value};
                this.formGroup?.get('afterKey').setValue(records);
                this._buildAfterKeys();
            }
        )
    }

    protected removeAfterKey(index: number): void{
        let records = this.afterKeyControl?.value;
        if(!records){
            return;
        }

        records = {...records};

        delete records[
            this.afterKeys[index].key
        ];

        this.afterKeyControl?.setValue(records);
        this._buildAfterKeys();

    }


    private _buildAfterKeys(): void{

        this.afterKeys = [];
        const records = this.afterKeyControl?.value;
        if(!records){
            return;
        }

        this.afterKeys = Object.keys(records).map(key => ({key, value: records[key]}));
    }
    

    protected onRemovableFieldChange(event:MatCheckboxChange, type :CompositeBucketConfig):void{
		let controlName = null;
		let control : AbstractControl = null;
		// todo possibly validators


		switch(type){
			case CompositeBucketConfig.Sources:
				controlName = CompositeBucketConfig.Sources;
                control = CompositeBucketConfigEditorModel.ADDITIONAL_FIELDS.find(x => x.name === controlName).build(null); // todo maybe validators
                // control = new AggregationMetricHavingConfigEditorModel().buildForm()
				break;
			case CompositeBucketConfig.AfterKey:
				controlName = CompositeBucketConfig.AfterKey;
                control = CompositeBucketConfigEditorModel.ADDITIONAL_FIELDS.find(x => x.name === controlName).build(null); // todo maybe validators
				// control = this.formBuilder.array([]);
				break;
			case CompositeBucketConfig.DateHistogramSource:
				controlName = CompositeBucketConfig.DateHistogramSource;
                control = CompositeBucketConfigEditorModel.ADDITIONAL_FIELDS.find(x => x.name === controlName).build(null); // todo maybe validators
				break;
			default:
				return;
		}


		if(![controlName, control].every(x => !!x)){
			return;
		}
		
		if(event.checked){
			if(this.formGroup?.get(controlName)){
				return;
			}
			this.formGroup?.addControl(controlName, control ) /// todo possibly validators added
			return;
		}

		//* unchecked 
		if(!this.formGroup?.get(controlName)){
			return;
		}
		this.formGroup?.removeControl(controlName);
        if(CompositeBucketConfig.AfterKey === controlName){
            this._buildAfterKeys();
        }
	}
}

interface AfterKey{
    key: string;
    value: string;
}

enum CompositeBucketConfig{
	Sources = 'sources',
	AfterKey = 'afterKey',
    DateHistogramSource = 'dateHistogramSource'
}