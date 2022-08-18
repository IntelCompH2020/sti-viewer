import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BrowseDataFieldModel, BrowseDataTreeLevelItemModel, BrowseDataTreeLevelModel } from '@app/core/model/data-tree/browse-data-tree-level.model';
import { IndicatorReportLevelLookup, SelectedLevel } from '@app/core/query/indicator-report-level-lookup';
import { DataTreeService } from '@app/core/services/http/data-tree.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { IndicatorQueryParams } from '@app/ui/indicator-dashboard/indicator-dashboard.component';
import { BaseComponent } from '@common/base/base.component';
import { HttpError, HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { GENERAL_ANIMATIONS } from '../../../animations';

@Component({
  selector: 'app-indicator-details',
  templateUrl: './indicator-details.component.html',
  styleUrls: ['./indicator-details.component.scss'],
  animations:GENERAL_ANIMATIONS
})
export class IndicatorDetailsComponent extends BaseComponent implements OnInit, OnChanges {


  @Input()
  configuration: IndicatorDetailsConfiguration;

  @Output()
  onIndicatorExpanded = new EventEmitter<void>();

  selectedIindex: number = null;
  selectedJindex: number = null;

  childConfiguration: IndicatorDetailsConfiguration;



  data: BrowseDataTreeLevelModel[];

  constructor(
    protected httpErrorHandlingService: HttpErrorHandlingService,
    protected uiNotificationService: UiNotificationService,
    private _router: Router,
    private _queryParamsService: QueryParamsService,
    private _activatedRoute: ActivatedRoute,
    private _dataTreeService: DataTreeService

  ) {
    super();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.selectedJindex = null;
    this.selectedIindex = null;
    this.childConfiguration = null;
    this.loadLevel();
  }

  ngOnInit(): void {
  }



  loadLevel(): void {

    const lookup = new IndicatorReportLevelLookup();
    lookup.configId = this.configuration.viewConfigId;

    lookup.selectedLevels = this.configuration.selectedLevelsTillNow;

    lookup.project = {
      fields: [
        nameof<BrowseDataTreeLevelModel>(x => x.supportSubLevel),
        nameof<BrowseDataTreeLevelModel>(x => x.supportedDashboards),
        nameof<BrowseDataTreeLevelModel>(x => x.items),
        [nameof<BrowseDataTreeLevelModel>(x => x.items), nameof<BrowseDataTreeLevelItemModel>(x => x.value) ].join('.'),
        [nameof<BrowseDataTreeLevelModel>(x => x.field), nameof<BrowseDataFieldModel>(x => x.code) ].join('.'),
        [nameof<BrowseDataTreeLevelModel>(x => x.field), nameof<BrowseDataFieldModel>(x => x.name) ].join('.'),
      ]
    };



    this._dataTreeService.queryLevel(lookup).pipe(
    takeUntil(this._destroyed))
      .subscribe(
        data => this.data = data.items,
        error => this.onCallbackError(error));
  }

  onSelectIndicator(i: number, j: number ): void {

    
    const value = this.data[i].items[j].value;
    const code = this.data[i].field.code;
  
    if(!this.data[i].supportSubLevel){
      if(this.data[i].supportedDashboards?.length){
        this.selectReport(i,j);
      }
      return ;
    }

    const indicatorExpanded = this.selectedIindex === null || this.selectedJindex === null;

    this.selectedJindex = j;
    this.selectedIindex = i;

    this.childConfiguration = {
      viewConfigId: this.configuration.viewConfigId,
      selectedLevelsTillNow:[...this.configuration.selectedLevelsTillNow, {
        code,
        value
      }]
    }

    if (indicatorExpanded) {
      this.onIndicatorExpanded.next();
    }
  }
  selectReport(i: number, j: number): void {

    const value = this.data[i].items[j].value;
    const code = this.data[i].field.code;
    
    if(!this.data[i].supportedDashboards?.length){
      return;
    }

    this._router.navigate(['dashboard'], {relativeTo: this._activatedRoute, queryParams: {
      params: this._queryParamsService.serializeObject<IndicatorQueryParams>({
        levels: [...this.configuration.selectedLevelsTillNow, {
          code,
          value
        }],
        displayName: value,
        dashboard: this.data[i].supportedDashboards[0]
      })}
    });
    
  }
  onExpand(): void {
    this.onIndicatorExpanded.next();
  }

  onCallbackError(errorResponse: HttpErrorResponse) {
    const error: HttpError = this.httpErrorHandlingService.getError(errorResponse);
    this.uiNotificationService.snackBarNotification(error.getMessagesString(), SnackBarNotificationLevel.Warning);

  }

}


export interface IndicatorDetailsConfiguration {
  selectedLevelsTillNow: SelectedLevel[];
  viewConfigId: string;
}