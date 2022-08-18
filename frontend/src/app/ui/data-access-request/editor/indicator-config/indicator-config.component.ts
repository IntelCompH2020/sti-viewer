import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl} from '@angular/forms';
import { AccessRequestConfig, FilterColumnConfig, Indicator } from '@app/core/model/indicator/indicator.model';
import { IndicatorLookup } from '@app/core/query/indicator.lookup';
import { IndicatorService } from '@app/core/services/http/indicator.service';
import { SingleAutoCompleteConfiguration } from '@common/modules/auto-complete/single/single-auto-complete-configuration';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { Guid } from '@common/types/guid';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Component({
  selector: 'app-indicator-config',
  templateUrl: './indicator-config.component.html',
  styleUrls: ['./indicator-config.component.css']
})
export class IndicatorConfigComponent implements OnInit {

  @Input() control: UntypedFormControl;
  @Input() disabled: boolean = false;
  @Input() index: number;
  @Input() alreadySelectedIndicators: Guid[] = [];
  @Output() onDeleteConfig: EventEmitter<number> = new EventEmitter<number>();
  get selectedColumns(): string[] {
    return this.control?.value?.filterColumns.map(x => x?.column).filter(x => x) ?? [];
  }
  selectedIndicator: Indicator;

  constructor(
    protected language: TranslateService,
    private indicatorService: IndicatorService,
    private uiNotificationService: UiNotificationService) { }

  ngOnInit(): void {
  }
  ngOnChanges(changes: SimpleChanges): void {

    if (changes['control']) {
      this.selectedIndicator = this.control.value.indicator;
    }
  }

  onSelectIndicator(indicator: Indicator) {
    this.selectedIndicator = indicator;
    this.filterColumns.clear();
    if (!this.selectedIndicator.config) {
      this.uiNotificationService.snackBarNotification('The indicator configuration is missing', SnackBarNotificationLevel.Warning);
      return;
    }
    let fg = new UntypedFormBuilder().group({ column: null, values: [] });
    this.filterColumns.push(fg);

  }

  createIndicatorLookup(ids: Guid[]): IndicatorLookup {
    const lookup: IndicatorLookup = new IndicatorLookup();
    if (ids != null) lookup.ids = ids;
    lookup.order = { items: [this.toDescSortField(nameof<Indicator>(x => x.createdAt))] };
    if (this.alreadySelectedIndicators.length > 0) { lookup.excludedIds = this.alreadySelectedIndicators }

    lookup.project = {
      fields: [
        nameof<Indicator>(x => x.id),
        nameof<Indicator>(x => x.code),
        nameof<Indicator>(x => x.name),
        nameof<Indicator>(x => x.description),
        [nameof<Indicator>(x => x.config), nameof<AccessRequestConfig>(x => x.filterColumns), nameof<FilterColumnConfig>(x => x.code)].join('.')

      ]
    };
    return lookup;
  }
  initialIndicatorItems = (): Observable<Indicator[]> => {
    const lookup = this.createIndicatorLookup(null);
    lookup.like = null;

    return this.indicatorService.query(lookup).pipe(
      map(item => {
        const result: Indicator[] = [];
        item.items.forEach(x => {
          result.push({
            id: x.id,
            code: x.code,
            description: x.description,
            name: x.name,
            config: x.config
          } as Indicator
          )
        });
        return result;
      }
      ));
  }
  indicatorFilterFn = (searchQuery: string): Observable<Indicator[]> => {
    const lookup = this.createIndicatorLookup(null);
    lookup.like = '%' + searchQuery + '%';

    return this.indicatorService.query(lookup).pipe(
      map(item => {
        const result: Indicator[] = [];
        item.items.forEach(x => {
          result.push({
            id: x.id,
            code: x.code,
            description: x.description,
            name: x.name,
            config: x.config
          } as Indicator
          )
        });
        return result;
      }
      ));
  }

  indicatorAutocompleteConfiguration: SingleAutoCompleteConfiguration = {
    initialItems: this.initialIndicatorItems.bind(this),
    filterFn: this.indicatorFilterFn.bind(this),
    displayFn: (item: Indicator) => item?.name,
    titleFn: (item: Indicator) => item?.name
  };


  addNewColumn() {
    let fg = new UntypedFormBuilder().group({ column: null, values: [] });
    this.filterColumns.push(fg);
  }
  deleteColumn(index: number) {
    this.filterColumns.removeAt(index);
  }
  get filterColumns(): UntypedFormArray {
    return this.control?.get('filterColumns') as UntypedFormArray;
  }
  public toDescSortField(value: string): string {
    return '-' + value;
  }
}
