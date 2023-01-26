import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { BaseComponent } from "@common/base/base.component";
import { filter, takeUntil } from "rxjs/operators";
import { nameof } from "ts-simple-nameof";
import { EditMapMappingDialogComponent } from "./edit-map-mapping-dialog/edit-map-mapping-dialog.component";

@Component({
    templateUrl: './map-chart-configuration.component.html',
    selector: 'app-map-configuration-editor'
})
export class MapConfigurationEditorComponent extends BaseComponent implements OnChanges{
    

    @Input()
    formGroup: FormGroup;

    get mapChartConfig(): FormGroup | null | undefined{
        return this.formGroup?.get('mapChartConfig') as FormGroup;
    }

    get highConfig(): FormGroup | undefined | null{
        return this.mapChartConfig?.get('high') as FormGroup;
    }
    get lowConfig(): FormGroup | undefined | null{
        return this.mapChartConfig?.get('low') as FormGroup;
    }

    protected countryMappings: {
        jsonName: string,
        mapName: string;
    }[] = [];


    constructor(
        private dialog: MatDialog
    ){
        super();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if(changes[
            nameof<MapConfigurationEditorComponent>(x=> x.formGroup)
        ]){
            this._buildCountryMappings();
        }
    }




    protected editMapping(index: number): void{
        this.dialog.open(EditMapMappingDialogComponent, {
            data: {
                mapping: this.countryMappings[index]
            }
        })
        .afterClosed()
        .pipe(
            filter(x => x),
            takeUntil(this._destroyed)
        )
        .subscribe((update) =>{
            const currentValues = this.formGroup.value.countryNameMapping;
            if(!currentValues){
                return ;
            }

            const newMappings = Object.keys(currentValues).reduce((aggr, currentKey, idx) =>{
                if(index === idx){
                    return {...aggr, ...{[update.jsonName]: update.mapName}};
                }

                return {...aggr, ...{[currentKey]: currentValues[currentKey]}}
            } ,{});


            this.formGroup?.get('countryNameMapping')?.setValue(newMappings);
            this._buildCountryMappings();
        })

    }
    protected deleteMapping(index: number): void{
        const target = this.countryMappings[index];
        let records = this.formGroup.value?.countryNameMapping;

        if(!records){
            return;
        }
        records = {...records};

        delete records[target.jsonName];
        this._buildCountryMappings();
        // this.countryMappings = this.countryMappings.filter((_, idx) => index !== idx );

    }

    protected addMapping(jsonName: HTMLInputElement, mapName: HTMLInputElement): void{
        const jsonvalue = jsonName.value;
        const mapvalue = mapName.value;

        if(!jsonvalue || !mapvalue){
            return;
        }
        const records = this.formGroup.value?.countryNameMapping ?? {};


        this.formGroup.get('countryNameMapping')?.setValue({
            ...records, 
            ...{[jsonvalue]:mapvalue}
        });

        this._buildCountryMappings();


        jsonName.value = '';
        mapName.value = '';
    }


    private _buildCountryMappings(): void{

        if(!this.formGroup){
            return;
        }

        const records = this.formGroup.value?.countryNameMapping;
        this.countryMappings  = []
        if(!records){
            return;
        }

        Object.keys(records).forEach(jsonName =>{
            this.countryMappings.push({
                jsonName,
                mapName: records[jsonName]
            })
        });
    }
}