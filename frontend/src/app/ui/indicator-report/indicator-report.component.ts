import { CollectionViewer, DataSource, SelectionChange } from '@angular/cdk/collections';
import { FlatTreeControl, } from '@angular/cdk/tree';
import { HttpErrorResponse } from '@angular/common/http';
import { Component,  OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { BrowseDataFieldModel, BrowseDataTreeLevelItemModel, BrowseDataTreeLevelModel } from '@app/core/model/data-tree/browse-data-tree-level.model';
import { IndicatorReportLevelLookup, SelectedLevel } from '@app/core/query/indicator-report-level-lookup';
import { DataTreeService } from '@app/core/services/http/data-tree.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseComponent } from '@common/base/base.component';
import { HttpError, HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { BehaviorSubject, merge, Observable, of} from 'rxjs';
import { map, switchMap, takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { IndicatorQueryParams } from '../indicator-dashboard/indicator-dashboard.component';
import { IndicatorDetailsConfiguration } from './indicator-details/indicator-details.component';

@Component({
  selector: 'app-indicator-report',
  templateUrl: './indicator-report.component.html',
  styleUrls: ['./indicator-report.component.scss'],
  animations: GENERAL_ANIMATIONS
})
export class IndicatorReportComponent extends BaseComponent implements OnInit{
  @ViewChild('contents') contentsView;

  configuration: IndicatorDetailsConfiguration;
  selectedConfig = -1;

  selectedView = 0;
  treeConfigs: BrowseDataTreeLevelModel[];

  constructor(
    protected uiNotificationService: UiNotificationService,
    protected httpErrorHandlingService: HttpErrorHandlingService,
    private _router: Router,
    private _queryParamsService: QueryParamsService,
    private _activatedRoute: ActivatedRoute,
    private _dataTreeService: DataTreeService

    ) {
    super();
  
  }
  ngOnInit(): void {
    this.loadIndicators();
  }


  loadIndicators(): void{

    this._dataTreeService
      .getMyConfigs()
      .pipe(
        switchMap(configs => {
          
          this.configuration = {
            selectedLevelsTillNow: [],
            viewConfigId: configs[0].id,
          }


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

          return this._dataTreeService.queryLevel(lookup)
        }),
        takeUntil(this._destroyed),
      )
      .subscribe(results =>{

        try{
          const config = results.items?.[0];
          this.treeControl = new FlatTreeControl<DynamicFlatNode>(this.getLevel, this.isExpandable);
          this.dataSource = new DynamicDataSource(this.treeControl, this._dataTreeService);
          this.dataSource.data = config.items.map((item) => ({ dashboard: config.supportedDashboards?.length ? config.supportedDashboards[0] : null ,item: item.value, expandable: config.supportSubLevel, level: 0, isLoading: false, filtersTillNow:[{code: config.field.code, value: item.value}], indicatorId:null, viewConfigId:this.configuration.viewConfigId}));
        }catch{

        }
    } );
  }


  navigateToNode(node: DynamicFlatNode){
    this._router.navigate(['dashboard'], 
    {
      relativeTo: this._activatedRoute,
      queryParams: {
        params: this._queryParamsService.serializeObject<IndicatorQueryParams>({
          levels: node.filtersTillNow,
          displayName: node.item,
          dashboard: node.dashboard
        })
      }
    });
  }


  onCallbackError(errorResponse: HttpErrorResponse) {
    const error: HttpError = this.httpErrorHandlingService.getError(errorResponse);
    this.uiNotificationService.snackBarNotification(error.getMessagesString(), SnackBarNotificationLevel.Warning);

  }

  onExpand(): void {

    const nativeElement = this.contentsView.nativeElement as HTMLElement;
    if (!nativeElement) {
      console.warn('nativeElement not found');
      return;
    }


    setTimeout(() => {
      nativeElement.scrollTo({ left: nativeElement.scrollLeft + 20000, behavior: 'smooth' });
    }, 200); //todo find better alternative
  }

  treeControl: FlatTreeControl<DynamicFlatNode>;

  dataSource: DynamicDataSource;

  getLevel = (node: DynamicFlatNode) => node.level;

  isExpandable = (node: DynamicFlatNode) => node.expandable;

  hasChild = (_: number, _nodeData: DynamicFlatNode) => _nodeData.expandable;

}
export class DynamicDataSource extends BaseComponent implements DataSource<DynamicFlatNode>{
  

  dataChange = new BehaviorSubject<DynamicFlatNode[]>([]);

  get data(): DynamicFlatNode[] {
    return this.dataChange.value;
  }
  set data(value: DynamicFlatNode[]) {
    this._treeControl.dataNodes = value;
    this.dataChange.next(value);
  }

  constructor(
    private _treeControl: FlatTreeControl<DynamicFlatNode>,
    protected _dataTreeService: DataTreeService,

  ) {
    super();
  }

  connect(collectionViewer: CollectionViewer): Observable<DynamicFlatNode[]> {
    this._treeControl.expansionModel.changed.subscribe(change => {
      if (
        (change as SelectionChange<DynamicFlatNode>).added ||
        (change as SelectionChange<DynamicFlatNode>).removed
      ) {
        this.handleTreeControl(change as SelectionChange<DynamicFlatNode>);
      }
    });

    return merge(collectionViewer.viewChange, this.dataChange).pipe(map(() => this.data));
  }

  disconnect(collectionViewer: CollectionViewer): void {}

  /** Handle expand/collapse behaviors */
  handleTreeControl(change: SelectionChange<DynamicFlatNode>) {
    if (change.added) {
      change.added.forEach(node => this.toggleNode(node, true));
    }
    if (change.removed) {
      change.removed
        .slice()
        .reverse()
        .forEach(node => this.toggleNode(node, false));
    }
  }

  /**
   * Toggle the node, remove from display list
   */
  toggleNode(node: DynamicFlatNode, expand: boolean) {

    console.log('node selected', node, 'expand', expand);


    // const children = this._database.getChildren(node.item);
    const index = this.data.indexOf(node);
    // if (!children || index < 0) {
    if (index < 0) {
      // If no children, or cannot find the node, no op
      return;
    }
    
    const lookup = new IndicatorReportLevelLookup();
    lookup.configId = node.viewConfigId;
    lookup.selectedLevels = node.filtersTillNow;
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
    
    if(expand){

      node.isLoading = true;
      this._dataTreeService.queryLevel(lookup).pipe(
        map(item => {
          return item.items;
        }), takeUntil(this._destroyed))
        .subscribe(
          data => { 
            const config = data[0] as BrowseDataTreeLevelModel;
  
            const nodes:DynamicFlatNode[] = config.items.map(item => ({
              expandable: config.supportSubLevel,
              filtersTillNow: [...node.filtersTillNow, {code:config.field.code, value: item.value}],
              isLoading: false,
              item: item.value,
              level: node.level +1,
              viewConfigId: node.viewConfigId,
              dashboard: config.supportedDashboards?.length ? config.supportedDashboards[0] : null
            }))
            this.data.splice(index + 1, 0, ...nodes);
            this.dataChange.next(this.data);
            node.isLoading = false;
          });
    }else{

      let count = 0;
        for (
          let i = index + 1;
          i < this.data.length && this.data[i].level > node.level;
          i++, count++
        ) {}
        this.data.splice(index + 1, count);
        this.dataChange.next(this.data);
        node.isLoading = false;

    }
  }
}

export class DynamicFlatNode {
  constructor(
    public item: string,
    public level = 1,
    public expandable = false,
    public isLoading = false,
    public filtersTillNow: SelectedLevel[] = [],
    public viewConfigId: string,
    public dashboard: string
  ) {}
}