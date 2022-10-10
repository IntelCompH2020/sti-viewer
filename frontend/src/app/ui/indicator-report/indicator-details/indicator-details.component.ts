import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BrowseDataFieldModel, BrowseDataTreeLevelItemModel, BrowseDataTreeLevelModel } from '@app/core/model/data-tree/browse-data-tree-level.model';
import { IndicatorPointKeywordFilter, IndicatorPointLookup } from '@app/core/query/indicator-point.lookup';
import { IndicatorReportLevelLookup } from '@app/core/query/indicator-report-level-lookup';
import { DataTreeService } from '@app/core/services/http/data-tree.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { IndicatorQueryParams } from '@app/ui/indicator-dashboard/indicator-dashboard.component';
import { BaseComponent } from '@common/base/base.component';
import { HttpError, HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { Subject } from 'rxjs';
import { debounceTime, switchMap, takeUntil } from 'rxjs/operators';
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

  @Output()
  goto = new EventEmitter<GotoEvent>();

  @Input()
  disabled: boolean = false;


  @Input()
  availableForPresentation: string[];


  data: BrowseDataTreeLevelModel[];

  searchTerm: string;
  private _searchTermChanges$ = new Subject<{textValue: string; field :string;}>();
  private readonly _SEARCH_TERM_DELAY_ = 600;

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
    if(changes.configuration){
      this.searchTerm = '';
      this.selectedJindex = null;
      this.selectedIindex = null;
      this.childConfiguration = null;
      this.loadLevel();
    }
  }

  ngOnInit(): void {
    this._registerSearchTermChangeListener();
  }



  searchTermChanged(params: {text: string, field: string }){
    const {text, field} = params;
    this._searchTermChanges$.next({field, textValue: text});
  }


  loadLevel(config?: {searchTerm?: string, field?: string }): void {

    const lookup = this._buildLookup(config);

    this._dataTreeService.queryLevel(lookup).pipe(
    takeUntil(this._destroyed))
      .subscribe(
        data => this.data = data.items,
        error => this.onCallbackError(error));
  }

  onGoto(event: GotoEvent ): void{
    if(this.disabled){
      return;
    }
    this.goto.emit(event);
  }

  onSelectIndicator(i: number, j: number ): void {

    if(this.disabled){
      return;
    }

    const value = this.data[i].items[j].value;
    const dashboard = this.data[i].items[j].supportedDashboards?.[0] ?? this.data[i].supportedDashboards?.length;
    const code = this.data[i].field.code;



    const parent = this.data[i];
    const child = parent.items[j];

    // either parent or child doesnt allow sublevel
    if(!this.supportsSubLevel(parent, child)){

      //parent doesnt allow sublevel
      if(!this.data[i].supportSubLevel){

        defineAction:{
          if(dashboard){
            this.selectReport(i,j);
            break defineAction;
          }
  
          // * Go to goto
          if(this.configuration.goTo){
            this.goto.emit({
              configurationId: this.configuration.goTo,
              keywordFilters:[
                ...(this.configuration.keywordFilters ?? []),
                {
                  field: code,
                  values: [value]
                },
              ]
            });
            
            this.selectedJindex = j;
            this.selectedIindex = i;
          }
  
        }
  
        return ;
      }
      return;
    }
  
    


 


    const indicatorExpanded = this.selectedIindex === null || this.selectedJindex === null;

    this.selectedJindex = j;
    this.selectedIindex = i;

    this.childConfiguration = {
      goTo: this.configuration.goTo,
      viewConfigId: this.configuration.viewConfigId,
      selectedLevelsTillNow:[...(this.configuration.selectedLevelsTillNow ?? []), code],
      keywordFilters: [{
        field: code,
        values: [value]
      },
      ...( this.configuration?.keywordFilters ??[])]
    }

    if (indicatorExpanded) {
      this.onIndicatorExpanded.next();
    }
  }
  selectReport(i: number, j: number): void {

    if(this.disabled){
      return;
    }
    const value = this.data[i].items[j].value;
    const dashboard = this.data[i].items[j].supportedDashboards?.[0] ?? this.data[i].supportedDashboards[0]; 
    const code = this.data[i].field.code;
    if(!dashboard){
      return;
    }

    this._router.navigate(['dashboard'], {relativeTo: this._activatedRoute, queryParams: {
      params: this._queryParamsService.serializeObject<IndicatorQueryParams>({
        keywordFilters: [
          // ...this.configuration.selectedLevelsTillNow,
          // code, 
        ...(this.configuration.keywordFilters ??[]),
        {
          field: code,
          values: [value]
        }
        ],
        displayName: value,
        dashboard
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


  protected trackByField(_index: number, item: BrowseDataTreeLevelModel): string{
    return JSON.stringify(item.field);
  }

  private _buildLookup(config: {searchTerm?: string, field?: string } = {searchTerm: undefined, field: undefined}):IndicatorReportLevelLookup{
    const {searchTerm, field} = config;

    const lookup = new IndicatorReportLevelLookup();
    lookup.configId = this.configuration.viewConfigId;

    lookup.selectedLevels = this.configuration.selectedLevelsTillNow;

    lookup.project = {
      fields: [
        nameof<BrowseDataTreeLevelModel>(x => x.supportSubLevel),
        nameof<BrowseDataTreeLevelModel>(x => x.supportedDashboards),
        nameof<BrowseDataTreeLevelModel>(x => x.items),
        [nameof<BrowseDataTreeLevelModel>(x => x.items), nameof<BrowseDataTreeLevelItemModel>(x => x.value) ].join('.'),
        [nameof<BrowseDataTreeLevelModel>(x => x.items), nameof<BrowseDataTreeLevelItemModel>(x => x.supportedDashboards) ].join('.'),
        [nameof<BrowseDataTreeLevelModel>(x => x.items), nameof<BrowseDataTreeLevelItemModel>(x => x.supportSubLevel) ].join('.'),
        [nameof<BrowseDataTreeLevelModel>(x => x.field), nameof<BrowseDataFieldModel>(x => x.code) ].join('.'),
        [nameof<BrowseDataTreeLevelModel>(x => x.field), nameof<BrowseDataFieldModel>(x => x.name) ].join('.'),
      ]
    };

    if(this.configuration.keywordFilters?.length){
      const indicatorPointLookup = new IndicatorPointLookup();
      indicatorPointLookup.keywordFilters = this.configuration.keywordFilters;
      lookup.filters = indicatorPointLookup;
    }

    if(searchTerm && field){
      const filters = (lookup.filters ?? new IndicatorPointLookup());
      filters.fieldLikeFilter = {
          fields: [field],
          like: searchTerm
      }

      lookup.filters = filters;

    }

    return lookup;
  }

  private _registerSearchTermChangeListener():void{
    
    this._searchTermChanges$.pipe(
      debounceTime(this._SEARCH_TERM_DELAY_),
      switchMap(change => {
        const lookup = this._buildLookup({field: change.field, searchTerm: change.textValue});
        return this._dataTreeService.queryLevel(lookup);
      }),
  takeUntil(this._destroyed)
    ).subscribe(
      data => this.data = data.items,
      error => {
        this._registerSearchTermChangeListener();
        this.onCallbackError(error);
      });
    }

  protected supportsSubLevel(parentItem: BrowseDataTreeLevelModel, childItem: BrowseDataTreeLevelItemModel): boolean{

    if(childItem?.supportSubLevel === !!childItem?.supportSubLevel){
      return childItem.supportSubLevel;
    }
    return !!parentItem.supportSubLevel;
  }
}


export interface IndicatorDetailsConfiguration {
  selectedLevelsTillNow: string[];
  viewConfigId: string;
  goTo?: string;
  keywordFilters? :IndicatorPointKeywordFilter[];
}

export interface GotoEvent{
  configurationId: string;
  keywordFilters?: IndicatorPointKeywordFilter[];
}