import { Component, OnInit } from "@angular/core";
import { DataGroupRequestStatus } from "@app/core/enum/data-group-request-status.enum";
import { IsActive } from "@app/core/enum/is-active.enum";
import { DataGroupColumn, DataGroupRequest, DataGroupRequestConfig } from "@app/core/model/data-group-request/data-group-request.model";
import { IndicatorGroup } from "@app/core/model/indicator-group/indicator-group.model";
import { Indicator } from "@app/core/model/indicator/indicator.model";
import { DataGroupRequestLookup } from "@app/core/query/data-group-request.lookup";
import { DataGroupRequestService } from "@app/core/services/http/data-group-request.service";
import { IndicatorGroupService } from "@app/core/services/http/indicator-group.service";
import { BaseComponent } from "@common/base/base.component";
import { SnackBarNotificationLevel, UiNotificationService } from "@common/modules/notification/ui-notification-service";
import { Guid } from "@common/types/guid";
import { TranslateService } from "@ngx-translate/core";
import { takeUntil } from "rxjs/operators";
import { nameof } from "ts-simple-nameof";
import { GroupUpdateDefinition, NewGroupDefinition } from "./my-indicator-columns-editor/my-indicator-columns-editor.component";

@Component({
    templateUrl:'./my-data-access-requests.component.html',
    styleUrls:[
        './my-data-access-requests.component.scss',
    ],
})
export class MyDataAccessRequestComponent extends BaseComponent implements OnInit{

    private _COMPOSITE_REQUEST_KEY = '_composite_request_';
    private _GROUP_PAGE_SIZE = 1000;



    protected indicatorGroups: IndicatorGroup[];

    /* groupsAggregated schema
        <IndicatorGroupId> : {
            <column>: [
                {
                    requestId: <grouprequestId>, values: [<values requested>]
                }
            ] 
        } 
    
    */
    protected groupsAggregated: Record<string, Record<string, ColumnGroup[]>> = {};


    constructor(
            private indicatorGroupService: IndicatorGroupService ,
            private dataGroupRequestService : DataGroupRequestService,
            private uiNotificationService: UiNotificationService,
            private language: TranslateService
        ){
        super();
    }


    ngOnInit(): void {
        this.indicatorGroupService.getAll([
            nameof<IndicatorGroup>(x => x.id),
            nameof<IndicatorGroup>(x => x.name),
            nameof<IndicatorGroup>(x => x.dashboardKey),
            [nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.code)].join('.'),
            [nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.name)].join('.'),
            [nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.id)].join('.'),
            nameof<IndicatorGroup>(x => x.filterColumns)
        ])
        .pipe(takeUntil(this._destroyed))
        .subscribe((result) =>{
            this.indicatorGroups = result;
            this._getGroupRequests();
        })

    }



    protected getGroups(group: IndicatorGroup, column: string){
        return this.groupsAggregated?.[group.id.toString()]?.[column];
    }
    

    protected onUpdateGroup(code: string, indicatorGroupId: Guid, group: GroupUpdateDefinition):void{
        this.dataGroupRequestService.persist({
            id: group.id,
            status: DataGroupRequestStatus.NEW,
            config: {
                groupColumns: [{
                    fieldCode: code,
                    values: group.items
                }],
                indicatorGroupId
            },
            name: group.name,
            hash: group.hash
        })
        .subscribe(response => {
            this._getGroupRequests();
        })
    }

    protected onAddNewGroup(code: string, group: NewGroupDefinition, indicatorGroupId: Guid):void {
        this.dataGroupRequestService.persist({
            status: DataGroupRequestStatus.NEW,
            config: {
                groupColumns: [{
                    fieldCode: code,
                    values: group.items
                }],
                indicatorGroupId
            },
            name: group.name
        })
        .subscribe(response => {
            this._getGroupRequests();
        })
    }
    
    protected onDeleteGroup(id: Guid): void {
        this.dataGroupRequestService.delete(id).pipe(
            takeUntil(this._destroyed)
            )
            .subscribe(response => {
                this.uiNotificationService.snackBarNotification(
                    this.language.instant('APP.MY-DATA-ACCESS-REQUESTS.COLUMNS-EDITOR.GROUP-SUCCESS-DELETE'),
                    SnackBarNotificationLevel.Info
                    );
                this._getGroupRequests();
            })
    }

    private _getGroupRequests():void{
        const lookup = new DataGroupRequestLookup();
        lookup.isActive = [IsActive.Active];


        if(!this.indicatorGroups?.length){
            console.log('!!!no indicator groups yet');
            return;
        }

        lookup.page = {
            size: this._GROUP_PAGE_SIZE,
            offset: 0
        };

        lookup.order = {
            items:[
                'createdAt'
            ]
        }

        lookup.project = {
            fields:[
                nameof<DataGroupRequest>(x => x.id),
                nameof<DataGroupRequest>(x => x.groupHash),
                nameof<DataGroupRequest>(x => x.isActive),
                [nameof<DataGroupRequest>(x => x.config), nameof<DataGroupRequestConfig>(x => x.groupColumns), nameof<DataGroupColumn>(x => x.fieldCode)].join('.'),
                [nameof<DataGroupRequest>(x => x.config), nameof<DataGroupRequestConfig>(x => x.groupColumns), nameof<DataGroupColumn>(x => x.values)].join('.'),
                [nameof<DataGroupRequest>(x => x.config), nameof<DataGroupRequestConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.id)].join('.'),
                [nameof<DataGroupRequest>(x => x.config), nameof<DataGroupRequestConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.dashboardKey)].join('.'),
                nameof<DataGroupRequest>(x => x.status),
                nameof<DataGroupRequest>(x => x.name),
                nameof<DataGroupRequest>(x => x.hash),
            ]
        }

        this.dataGroupRequestService.query(lookup).subscribe(response =>{
            this.groupsAggregated = this._buildGroups(response.items);
        })
    }

    private _buildGroups(dataGroupRequests: DataGroupRequest[]): any{
        return dataGroupRequests.reduce((aggr, current) =>{

            const indicatorGroup = aggr[current.config.indicatorGroup.id.toString()] ?? {};

            let compositeRequest = false;

            if(current.config.groupColumns.length){
                const distinctFieldCode = current.config.groupColumns[0].fieldCode;
                compositeRequest = !current.config.groupColumns.every(groupcolumn => groupcolumn.fieldCode === distinctFieldCode)
            }

            current.config.groupColumns.forEach(groupColumn =>{
                const key = compositeRequest ? this._COMPOSITE_REQUEST_KEY :  groupColumn.fieldCode;
                const field = indicatorGroup[key] ?? [];
                const record: ColumnGroup = {
                    groupHash: current.groupHash,
                    name: current.name,
                    requestId: current.id,
                    values: groupColumn.values,
                    column: groupColumn.fieldCode,
                    approved: current.status === DataGroupRequestStatus.COMPLETED,
                    hash: current.hash
                }
                field.push(record);

                indicatorGroup[key] = field;
            })

            aggr[current.config.indicatorGroup.id.toString()] = indicatorGroup;
            return aggr;
        } ,{})
    }
}

export interface ColumnGroup{
    requestId: string | Guid;
    column: string;
    values: string[];
    approved: boolean;
    groupHash: string;
    hash: string;
    name: string;
}