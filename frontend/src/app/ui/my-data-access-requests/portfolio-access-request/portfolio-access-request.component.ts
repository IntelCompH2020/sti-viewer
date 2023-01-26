import {Component, OnInit} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { DataAccessRequestStatus } from '@app/core/enum/data-access-request-status.enum';
import { IndicatorGroupAccessColumnConfigItemView, IndicatorGroupAccessColumnConfigView, IndicatorGroupAccessConfigView } from '@app/core/model/indicator-group/indicator-group-access-config-view.model';
import { FilterColumn, IndicatorGroup } from '@app/core/model/indicator-group/indicator-group.model';
import { Indicator } from '@app/core/model/indicator/indicator.model';
import { PortofolioColumnConfig, PortofolioConfig } from '@app/core/model/portofolio-config/portofolio-config.model';
import { DataAccessRequestService } from '@app/core/services/http/data-access-request.service';
import { BasePendingChangesComponent } from '@common/base/base-pending-changes.component';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, retry, takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { MyDataAccessRequestResolver } from '../my-data-access-request.resolver';
import { NewColumnRequesDialogComponent, NewColumnRequesDialogComponentParams } from './new-column-request-dialog/new-column-request-dialog.component';


@Component({
    templateUrl:'./portfolio-access-request.component.html',
    styleUrls:[
        './portfolio-access-request.component.scss'
    ],
    
})
export class PortfolioAccessRequestComponent extends BasePendingChangesComponent implements OnInit{

    filterColumns: UIFilterColumn[];
    indicatorGroup: IndicatorGroup;



    protected DataAccessRequestStatus = DataAccessRequestStatus;


    private readonly config: PortofolioConfig =  this.route.snapshot.data[MyDataAccessRequestResolver.RESOLVER_KEY]

    constructor(
        private dataAccessRequestService: DataAccessRequestService,
        private dialog: MatDialog,
        private uiNotificationsService: UiNotificationService,
        private language: TranslateService,
        private route: ActivatedRoute,
    ){
        super();
    }



    protected addColumn( column: UIFilterColumn):void{
        this.dialog.open<NewColumnRequesDialogComponent,NewColumnRequesDialogComponentParams, string[] >(NewColumnRequesDialogComponent,{
            data:{
                column: column.code,
                indicatorIds: this.indicatorGroup?.indicators?.map(x => x.id) ?? [],
                exludedItems: [

                    ...(column.items ?? [])
                    .filter(x => [
                                DataAccessRequestStatus.SUBMITTED,
                                DataAccessRequestStatus.APPROVED,
                                DataAccessRequestStatus.IN_PROCESS,
                            ].includes(x.status)
                    )
                    .map(item => item.value),

                    ...column.additions
                ],
            },
            width: '30rem',
            disableClose: true
        })
        .afterClosed()
        .pipe(
            filter(x => !!x),
            takeUntil(this._destroyed)
        )
        .subscribe(additions => {
            column.additions = [... new Set<string>([...column.additions, ...additions])]     
        })
    }



    /// defuatls


    ngOnInit(): void {
        this.loadData();
    }


    private loadData():void{

        if(!this.config?.indicatorGroup?.code){
            console.warn('no indicator group code found');
            return;
        }

        this.dataAccessRequestService.getIndicatorGroupAccessConfigView(
            this.config.indicatorGroup.code, 
            [
                nameof<IndicatorGroupAccessConfigView>(x => x.group),
                [nameof<IndicatorGroupAccessConfigView>(x => x.group), nameof<IndicatorGroup>(x => x.id) ].join('.'),
                [nameof<IndicatorGroupAccessConfigView>(x => x.group), nameof<IndicatorGroup>(x => x.code) ].join('.'),
                [nameof<IndicatorGroupAccessConfigView>(x => x.group), nameof<IndicatorGroup>(x => x.name) ].join('.'),

                [nameof<IndicatorGroupAccessConfigView>(x => x.group), nameof<IndicatorGroup>(x => x.indicators) ].join('.'),
                [nameof<IndicatorGroupAccessConfigView>(x => x.group), nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.id) ].join('.'),
                
                
                nameof<IndicatorGroupAccessConfigView>(x => x.filterColumns),
                [nameof<IndicatorGroupAccessConfigView>(x => x.filterColumns), nameof<IndicatorGroupAccessColumnConfigView>(x => x.code) ].join('.'),
                [nameof<IndicatorGroupAccessConfigView>(x => x.filterColumns), nameof<IndicatorGroupAccessColumnConfigView>(x => x.items) ].join('.'),
                [nameof<IndicatorGroupAccessConfigView>(x => x.filterColumns), nameof<IndicatorGroupAccessColumnConfigView>(x => x.items), nameof<IndicatorGroupAccessColumnConfigItemView>(x => x.status) ].join('.'),
                [nameof<IndicatorGroupAccessConfigView>(x => x.filterColumns), nameof<IndicatorGroupAccessColumnConfigView>(x => x.items), nameof<IndicatorGroupAccessColumnConfigItemView>(x => x.value) ].join('.'),
            ]
        )
        .pipe(takeUntil(this._destroyed))
        .subscribe(response => {


            const fColumns = new Map<string, PortofolioColumnConfig>();
            this.config.columns.forEach(column => {
                fColumns.set(column.field.code, column);
            })



            const responseColums = new Map<string, IndicatorGroupAccessColumnConfigItemView[]>();



            response.filterColumns?.forEach(item => {
                responseColums.set(item.code, item.items);
            })

            this.indicatorGroup = response.group;
            // this.filterColumns =  response?.filterColumns
            //     .filter(filterColumn => fColumns.has(filterColumn.code))
            //     .map(filterColumn => ({
            //     ...filterColumn,



            //     additions: [],
            //     major: !!fColumns.get(filterColumn.code).major,
            //     order: fColumns.get(filterColumn.code).order            
            // }));



            this.filterColumns = this.config?.indicatorGroup?.filterColumns?.map(filterColumn =>({
                code: filterColumn.code,
                items: responseColums.get(filterColumn.code)?? [],
                additions: [],
                major: !!fColumns.get(filterColumn.code).major,
                order: fColumns.get(filterColumn.code).order,
                label: fColumns.get(filterColumn.code).field.name            
            }))

        })
    }


    protected removeAdditionForColumn(column: UIFilterColumn, additionIndex: number):void{
        if(!column?.additions?.length){
            console.warn("no additions to remove")
            return
        }

        if( additionIndex<0 || additionIndex > (column.additions.length -1)){
            console.warn('invalid index ');
            return;
        }

        column.additions = column.additions.filter((_addition, index) => index !== additionIndex);
        
    }


    protected submitChanges(): void{
        const filterColumnChanges = this.filterColumns?.filter(filterColumn => filterColumn?.additions?.length);

        if(!filterColumnChanges?.length){
            this.uiNotificationsService.snackBarNotification(
                this.language.instant('APP.MY-DATA-ACCESS-REQUESTS.EDIT-PORTOFOLIO.NO-CHANGES-WERE-MADE'),
                SnackBarNotificationLevel.Warning);
            return;     
        }

        this.dataAccessRequestService.persist({
            config:{
                indicators: [],
                indicatorGroups: [{
                    groupId: this.indicatorGroup.id,
                    filterColumns: filterColumnChanges.map(filterColumn => ({column: filterColumn.code, values: filterColumn.additions}))
                }]
            },
            status: DataAccessRequestStatus.SUBMITTED,
        } as any)
        .subscribe(response => {
            this.uiNotificationsService.snackBarNotification(
                this.language.instant('APP.MY-DATA-ACCESS-REQUESTS.EDIT-PORTOFOLIO.CHANGES-WERE-MADE-SUCCESSFULLY'),
                SnackBarNotificationLevel.Success);
            this.loadData();
        });

    }

    private pendingChanges():boolean{
        return !!this.filterColumns?.filter(filterColumn => filterColumn?.additions?.length)?.length;
    }



    canDeactivate(): boolean | Observable<boolean> {
        return !this.pendingChanges();
    }

}


interface UIFilterColumn extends IndicatorGroupAccessColumnConfigView{
    additions: string[];
    major: boolean;
    order: number;
    label: string;
}