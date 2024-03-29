import { Component, EventEmitter, Input, isDevMode, OnChanges, OnInit, Output, SimpleChanges } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { ElasticOrderEnum } from "@app/core/enum/elastic-order.enum";
import { Indicator } from "@app/core/model/indicator/indicator.model";
import { PortofolioColumnConfig } from "@app/core/model/portofolio-config/portofolio-config.model";
import { IndicatorPointDistinctLookup } from "@app/core/query/IndicatorPointDistinctLookup";
import { IndicatorPointService } from "@app/core/services/http/indicator-point.service";
import { QueryParamsService } from "@app/core/services/ui/query-params.service";
import { IndicatorQueryParams } from "@app/ui/indicator-dashboard/indicator-dashboard.component";
import { BaseComponent } from "@common/base/base.component";
import { ConfirmationDialogComponent } from "@common/modules/confirmation-dialog/confirmation-dialog.component";
import { Guid } from "@common/types/guid";
import { TranslateService } from "@ngx-translate/core";
import { Subject } from "rxjs";
import { debounceTime, filter, switchMap, takeUntil } from "rxjs/operators";
import { GroupGeneratorComponent, GroupGeneratorComponentParams } from "../group-generator/group-generator.component";
import { ColumnGroup } from "../my-data-access-requests.component";

@Component({
    templateUrl: './my-indicator-columns-editor.component.html',
    styleUrls:[
        './my-indicator-columns-editor.component.scss'
    ],
    selector: 'app-my-indicator-columns-editor',
})
export class MyIndicatorColumnsEditorComponent extends BaseComponent implements OnInit, OnChanges{


    @Input()
    defaultDashboards: string[];

    @Input()
    column: PortofolioColumnConfig;

    @Input()
    indicators: Indicator[] = [];

    @Input()
    disabled: boolean = false;

    @Input()
    dashboardKey: string;

    @Input()
    groups: ColumnGroup[] | null | undefined;

    @Output()
    onGroupCreate = new EventEmitter<NewGroupDefinition>();

    @Output()
    onDeleteGroup = new EventEmitter<Guid>();

    @Output()
    onGroupUpdate = new EventEmitter<GroupUpdateDefinition>();



    private _afterKey: Map<string, object>;
    private readonly _BATCH_SIZE = 10;
    private _searchTextSubject$ = new Subject<string>();

    
    protected fields : string[];
    protected totalCount: number;
    protected searchText: string;
    protected dataGroups: DataGrouInfo[];

    constructor(
        private indicatorPointService: IndicatorPointService,
        private dialog: MatDialog,
        private router: Router,
        private queryParamsService: QueryParamsService,
        private language: TranslateService
        ){
            super();
    }
    ngOnInit(): void {
        this._loadFields({});
        this._registerSearchTextSubject();
    }
    
    ngOnChanges(changes: SimpleChanges): void {
        if(changes['groups']){
            this.dataGroups = this.groups?.map(group => (
                {
                    name: group.name,
                    values: group.values,
                    requestId: Guid.parse(group.requestId.toString()),
                    approved: group.approved,
                    groupHash: group.groupHash,
                    hash: group.hash
                }
            ));
        }    
    }

    protected editGroup(dgroupInfo: DataGrouInfo): void{
        this.dialog.open<GroupGeneratorComponent, GroupGeneratorComponentParams, NewGroupDefinition>(GroupGeneratorComponent,{
            data:{
                indicators: this.indicators,
                column: this.column.field.code,
                values: dgroupInfo.values,
                name: dgroupInfo.name
            },
            width: '30rem',
            disableClose: true
        })
        .afterClosed()
        .pipe(
            filter(x => !!x),
            takeUntil(
                this._destroyed
            )
        )
        .subscribe(
            group => {
                this.onGroupUpdate.emit({id: dgroupInfo.requestId , hash: dgroupInfo.hash , ...group});
            }
        );
    }

    protected createGroup(): void{
        this.dialog.open<GroupGeneratorComponent, GroupGeneratorComponentParams, NewGroupDefinition>(GroupGeneratorComponent,{
            data:{
                indicators: this.indicators,
                column: this.column.field.code,
            },
            width: '30rem',
            disableClose: true
        })
        .afterClosed()
        .pipe(
            takeUntil(this._destroyed),
            filter(x => !!x)
        )
        .subscribe(group =>{
            this.onGroupCreate.emit(group);
        });
    }

    protected loadMore(): void{
        if(this.totalCount<= this.fields.length){
            return;
        }

        this._loadFields({keepPrevious: true});
    }

    protected onSearchTextChange(searchTerm: string): void{
        this._searchTextSubject$.next(searchTerm);
    }

    protected navigateToGroupDashboard(groupHash: string, displayName):void{
        this.router.navigate(['indicator-report','dashboard'], { queryParams: {
            params: this.queryParamsService.serializeObject<IndicatorQueryParams>({
              keywordFilters: [],
              displayName :this.language.instant('APP.MY-DATA-ACCESS-REQUESTS.COLUMNS-EDITOR.GROUP') + displayName,
              dashboard: this.computeDashboard(null),
              groupHash
            })}
          });
    }

    protected deleteGroup(groupId: Guid):void{
        this.dialog.open(ConfirmationDialogComponent,{
            data:{
                message: this.language.instant('APP.MY-DATA-ACCESS-REQUESTS.COLUMNS-EDITOR.DELETE-DIALOG.MESSAGE'),
                cancelButton: this.language.instant('APP.MY-DATA-ACCESS-REQUESTS.COLUMNS-EDITOR.DELETE-DIALOG.CANCEL-BUTTON'),
                confirmButton: this.language.instant('APP.MY-DATA-ACCESS-REQUESTS.COLUMNS-EDITOR.DELETE-DIALOG.CONFIRM-BUTTON')
            }
        })
        .afterClosed()
        .pipe(
            takeUntil(this._destroyed),
            filter(x => x)
        )
        .subscribe(() =>{
            this.onDeleteGroup.emit(groupId);
        })
    }


    protected navigateToDashboard(field: string): void{
        this.router.navigate(['indicator-report','dashboard'], { queryParams: {


            params: this.queryParamsService.serializeObject<IndicatorQueryParams>({
            //   levels: [{
            //     code:this.column,
            //     value:field
            //   }],
            
              displayName: `${this.column.field.name} : ${field}`, // TODO
              dashboard: this.computeDashboard(field),
              keywordFilters: [
                {
                    field: this.column.field.code, 
                    values: [field]
                }
              ]
            })}
          });
    }

    
    private computeDashboard(field: string): string{
        let dashboard = this.defaultDashboards?.length ? this.defaultDashboards[0]: null;

        if(this.column?.defaultDashboards?.length){
            dashboard = this.column.defaultDashboards[0];
        }

        if(this.column?.dashboardOverrides?.length){
            const override = this.column?.dashboardOverrides.find(
                override => override?.requirements?.every(
                    requirement => requirement.value === field
                )
            );

            if(override?.supportedDashboards?.length){
                dashboard = override.supportedDashboards[0];
            }
        }

        

        return dashboard;
    }


    private _registerSearchTextSubject(): void{
        this._searchTextSubject$
            .asObservable()
            .pipe(
                takeUntil(this._destroyed),
                debounceTime(400),
                switchMap(
                    searchText => this.indicatorPointService.getIndicatorPointQueryDistinct(
                        this._buildLookup({like: searchText})
                        )
                    )
            )
            .subscribe(
                response =>{
                    this.fields = response.items;
                    this.totalCount = response.count;
                    this._afterKey = response.afterKey;
                },
                error => this._registerSearchTextSubject()
            )
    }

    private _loadFields(options:{keepPrevious?: boolean}): void{
        const {keepPrevious} = options;
        this.indicatorPointService.getIndicatorPointQueryDistinct(this._buildLookup({keepPrevious}))
        .pipe(
            takeUntil(this._destroyed)
        )
        .subscribe(response => {
            this._afterKey = response.afterKey;
            this.totalCount = response.count;

            if(keepPrevious){
                this.fields.push(...response.items);
                return;
            }
            this.fields = response.items;
        });
    }

    private _buildLookup(options:{keepPrevious?: boolean, like?: string}): IndicatorPointDistinctLookup{

        const {keepPrevious, like} = options;

        const lookup = new IndicatorPointDistinctLookup();

        lookup.indicatorIds = this.indicators.map(indicator => indicator.id);
        lookup.field = this.column.field.code;
        lookup.order  = ElasticOrderEnum.ASC;
        lookup.batchSize = this._BATCH_SIZE;

        if(keepPrevious && this._afterKey){
            lookup.afterKey = this._afterKey;
        }

        if(like!== null && like !==undefined){
            lookup.like = `%${like}%`;
        }

        return lookup;
    }

}


interface DataGrouInfo{
    name: string,
    requestId: Guid,
    approved: boolean,
    groupHash: string;
    values: string[];
    hash: string;
}

export interface NewGroupDefinition{
    name: string;
    items: string[];
}

export interface GroupUpdateDefinition extends NewGroupDefinition{
    id: Guid;
    hash: string;
}