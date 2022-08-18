import { X } from '@angular/cdk/keycodes';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormArray, FormBuilder, UntypedFormControl } from '@angular/forms';
import { ElasticOrderEnum } from '@app/core/enum/elastic-order.enum';
import { FilterColumnConfig, Indicator } from '@app/core/model/indicator/indicator.model';
import { IndicatorPointLookup } from '@app/core/query/indicator-point.lookup';
import { IndicatorPointDistinctLookup } from '@app/core/query/IndicatorPointDistinctLookup';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { HttpError, HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-indicator-column-config',
  templateUrl: './indicator-column-config.component.html',
  styleUrls: ['./indicator-column-config.component.css']
})
export class IndicatorColumnConfigComponent implements OnInit {

  @Input() filterColumn: UntypedFormControl;
  @Input() selectedIndicator: Indicator;
  @Input() disabled: boolean = false;
  @Input() index: number;
  @Input() selectedColumns: string[] = [];
  @Output() onDeleteColumn: EventEmitter<number> = new EventEmitter<number>();

  availableColumns: string[] = [];
  availableColumnValues: string[] = [];


  constructor(
    private indicatorPointService: IndicatorPointService,
    protected uiNotificationService: UiNotificationService) {
  }

  ngOnInit(): void {


  }

  selectColumn(changes: any) {
    if (!changes.source._selected) { return; }
    if (this.disabled) { return; }
    let lookup: IndicatorPointDistinctLookup = new IndicatorPointDistinctLookup();
    lookup.field = changes.source.value;
    lookup.order = ElasticOrderEnum.ASC;
    lookup.indicatorIds = [this.selectedIndicator.id];
    this.indicatorPointService.getIndicatorPointQueryDistinct(lookup).pipe()
      .subscribe(
        data => {

          this.availableColumnValues = data.items as string[];
        },
        error => this.uiNotificationService.snackBarNotification(error.error.error, SnackBarNotificationLevel.Warning)
      );

  }

  ngOnChanges(changes: SimpleChanges): void {

    if (changes['selectedColumns']) {
      this.getAvailableColumns();
    }
    if (!changes['selectedIndicator']) { return; }
    if (!this.selectedIndicator.config) { return; }
    if (!this.selectedIndicator.config.filterColumns) { return; }
    this.getAvailableColumns();

  }

  getAvailableColumns(): void {
    this.availableColumns = this.selectedIndicator.config.filterColumns.map(x => x.code).filter(x => {
      if (this.selectedColumns && this.selectedColumns.length > 0 && this.filterColumn.get('column').value != x) {
        return !this.selectedColumns.includes(x);
      }
      return true;
    });

  }


}
