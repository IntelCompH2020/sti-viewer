import { CdkDrag } from "@angular/cdk/drag-drop";
import { Component, Input, ViewChild } from "@angular/core";
import { IndicatorPointService } from "@app/core/services/http/indicator-point.service";
import { USE_CACHE } from "@app/core/services/tokens/caching.token";
import { DashboardUITagsService } from "@app/ui/indicator-dashboard/ui-services/dashboard-tags.service";
import { BaseComponent } from "@common/base/base.component";
import { Observable } from "rxjs";
import { delay, filter, startWith, takeUntil, tap } from "rxjs/operators";
import { ChartPreviewInfo, ChartPreviewService, ChartPreviewType } from "../chart-preview.service";

@Component({
    templateUrl: './dashboard-configuration-chart-preview.component.html',
    selector: 'app-chart-configuration-previewer',
    providers:[
      {
        provide: USE_CACHE, useValue: true 
      },
      IndicatorPointService,
      DashboardUITagsService
    ],
    styleUrls:[
      './dashboard-configuration-chart-preview.component.scss'
    ]
})
export class DashboardConfigurationChartPreviewEditorComponent extends BaseComponent{


  @ViewChild(CdkDrag) cdkDrag: CdkDrag;

  isScaled = false;
  protected chart: ChartPreviewInfo;
  ChartPreviewType = ChartPreviewType;


  

  constructor(private chartPreviewService: ChartPreviewService){
    super();
    this.chartPreviewService.activeChart$()
      .pipe(
        filter(x => !!x),
        startWith(null),
        delay(100),
        takeUntil(this._destroyed),
          tap((chart) => console.log({chart}))
        )
        .subscribe((chart) => {
          this.chart = chart;
        })
  }



  scale():void{
    this.isScaled = !this.isScaled;
    this.cdkDrag?.reset();
  }
  onAnimationEnd(event: TransitionEvent):void{
    console.log({event})
  }
}