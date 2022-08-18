import { Options } from '@angular-slider/ngx-slider';
import { AfterViewInit, Component, EventEmitter, Inject, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ElasticOrderEnum } from '@app/core/enum/elastic-order.enum';
import { IndicatorPointDistinctLookup } from '@app/core/query/IndicatorPointDistinctLookup';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { Guid } from '@common/types/guid';
import { BaseIndicatorDashboardChartConfig, ChartFilter, ChartFilterSlider, ChartFilterType } from '../../indicator-dashboard-config';
import {MultipleAutoCompleteConfiguration} from '../../../../../common/modules/auto-complete/multiple/multiple-auto-complete-configuration';
import {SingleAutoCompleteConfiguration} from '../../../../../common/modules/auto-complete/single/single-auto-complete-configuration';
import { IndicatorPointLookup } from '../../../../../app/core/query/indicator-point.lookup';
import { map } from 'rxjs/operators';


@Component({
  selector: 'app-indicator-dashboard-filters',
  templateUrl: './indicator-dashboard-filters.component.html',
  styleUrls: ['./indicator-dashboard-filters.component.scss']
})
export class IndicatorDashboardFiltersComponent implements OnInit, AfterViewInit {

  public filters: ChartFilter[];
  public FilterTypes = ChartFilterType;
  public sliderConfigurations :Options[];

  filtersArray: UntypedFormArray;
  chartName: string;

  manualRefresh: EventEmitter<void> = new EventEmitter<void>();

  bannedValues: Record<string, any[]>;

  constructor(
    @Inject(MAT_DIALOG_DATA) private params: IndicatorListingFiltersComponentData,
    private formBuilder: UntypedFormBuilder,
    private _dialogRef: MatDialogRef<IndicatorDashboardFiltersComponent>,
    indicatorPointService: IndicatorPointService
  ) {
    this.bannedValues = this.params.bannedValues;
    this.filters = this.params.config.filters.map(_=>({
      ..._, autoCompleteConfiguration : (() =>{

        if(_.type === ChartFilterType.Select){
          if(_.multiple){ // Multiple auto complete configuration
            const configuration: MultipleAutoCompleteConfiguration = {
              initialItems:() => indicatorPointService.getIndicatorPointQueryDistinct( this._buildFieldLookup({field:_.fieldCode, like:null, indicatorId: Guid.parse(params.config.indicatorId)}))
              .pipe(
                map(x => x.items),
              ),
              filterFn:(searchQuery: string, excludedItems) => indicatorPointService.getIndicatorPointQueryDistinct( this._buildFieldLookup({field: _.fieldCode, like:searchQuery, indicatorId:Guid.parse(params.config.indicatorId) , excludedItems}))
                .pipe(
                  map(x => x.items),
                ),
              displayFn:(item) => item ,
            }
            return configuration;
          }
          const configuration: SingleAutoCompleteConfiguration = {
            initialItems: () => indicatorPointService.getIndicatorPointQueryDistinct(this._buildFieldLookup({field: _.fieldCode,like: null, indicatorId: Guid.parse(params.config.indicatorId)}))
                .pipe(
                  map(x => x.items),
                ),
            filterFn:(searchQuery: string ) => indicatorPointService.getIndicatorPointQueryDistinct( this._buildFieldLookup({field: _.fieldCode, like: searchQuery, indicatorId: Guid.parse(params.config.indicatorId)}))
              .pipe(
                map(x => x.items),
              ),
            displayFn:(item) => item,
            valueAssign: item => item
          }
          return configuration;
        }

        return {

        }
      })()
    }));
    this.chartName = this.params.config.chartName;

    this.sliderConfigurations = this.filters.map(filter => {
      if(filter.type !== ChartFilterType.Slider){
        return null;
      }
      
      const castedFilter = filter as ChartFilterSlider;
      return {
        showTicksValues: true,
        stepsArray: castedFilter?.values?.map(val => ({legend: val.name, value: val.value }))
      }
    });


    this.filtersArray = this.formBuilder.array(
      this.filters.map((filter, i) => this.formBuilder.group({
        fieldCode: filter.fieldCode,
        indicatorFilterType: filter.indicatorFilterType,
        value: (() => {


          if(this.bannedValues[filter.fieldCode]) return null;

          let paramsValue;
          if(params.values?.length){
            // paramsValue = params?.values?.find(value => value.fieldCode === filter.fieldCode)?.value
            paramsValue = params?.values[i].value
          }

          if (paramsValue !== null && paramsValue !== undefined) {
            return new UntypedFormControl(paramsValue);
          }

          if (filter.type === ChartFilterType.Slider) {
            const castedFilter = filter as ChartFilterSlider;
            if (castedFilter?.range) {
              return new UntypedFormControl([
                filter.values[0].value, filter.values[filter.values.length - 1].value
              ]);
            }
          }

          return null;
        })()

      }))
    )
  }
  ngAfterViewInit(): void {
    setTimeout(() => {
      this.manualRefresh.emit();
    },300);
  }

  ngOnInit(): void {
  }


  submit(): void {
    const value = this.filtersArray.value;
    this._dialogRef.close(value);
  }


  close(): void {
    this._dialogRef.close(null);
  }


  private _buildFieldLookup(params:{ indicatorId: Guid, field: string, like: string, excludedItems?: any[] }): IndicatorPointDistinctLookup{
    const {field, like, indicatorId, excludedItems} = params;
    const lookup = new IndicatorPointDistinctLookup();


    lookup.indicatorIds = [indicatorId];
    lookup.order = ElasticOrderEnum.ASC;
    lookup.batchSize = 10;

    lookup.field = field;

    if(like){
      lookup.like = `%${like}%`;
    }

    // const bannedValues = this.params.bannedValues[field];

    const indicatorPointQuery = new IndicatorPointLookup();
    this.bannedValues;

    indicatorPointQuery.keywordFilters  = Object.keys(this.bannedValues).map(key =>({
      field: key,
      values: this.bannedValues[key]
    })).filter(x => x.field && x?.values?.length);

    // if(bannedValues?.length){
    //   indicatorPointQuery.keywordFilters = [
    //     {
    //       field,
    //       values: bannedValues
    //     }
    //   ];
    // }



    // if(excludedItems?.length){
    //   // TODO MAYBE NOT  ONLY FOR KEYWORD FILTERS
    //   let keywordFilters = indicatorPointQuery.keywordFilters ?? [];

    //   const fieldFilter = keywordFilters.find(x => x.field === field) ?? {field, values: []}; 

    //   const uniqueValues = new Set(fieldFilter.values);

    //   excludedItems.forEach(item => {
    //     uniqueValues.add(item);
    //   })

    //   fieldFilter.values = Array.from(uniqueValues);

    //   keywordFilters = keywordFilters.filter(x => x.field !== field);
    //   keywordFilters.push(fieldFilter);
      
    //   indicatorPointQuery.keywordFilters = keywordFilters;
    // }


    if(
      Object.keys(indicatorPointQuery).some(key => [indicatorPointQuery[key]] !== undefined)
    ){
      lookup.indicatorPointQuery = indicatorPointQuery;
    }
    return lookup;
  }

}



export interface IndicatorListingFiltersComponentData {
  config: BaseIndicatorDashboardChartConfig,
  values: { fieldCode: string, value: any }[],
  bannedValues:Record<string, any[]>
}