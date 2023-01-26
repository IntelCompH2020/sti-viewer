import { Injectable } from "@angular/core";
import { BaseIndicatorDashboardGaugeConfig, IndicatorDashboardChart } from "@app/ui/indicator-dashboard/indicator-dashboard-config";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable()
export class ChartPreviewService{


    private _chart$ = new BehaviorSubject<ChartPreviewInfo>(null);



    public previewChart(chart: ChartPreviewInfo):void{
        this._chart$.next(JSON.parse(JSON.stringify(chart)));
    }

    public activeChart$(): Observable<ChartPreviewInfo>{
        return this._chart$.asObservable();
    }


}



export interface ChartPreviewInfo{
    type: ChartPreviewType,
    data: IndicatorDashboardChart | BaseIndicatorDashboardGaugeConfig;
}

export enum ChartPreviewType{
    Chart = 'chart',
    Gauge = 'gauge'
}