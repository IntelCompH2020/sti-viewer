import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { NgxEchartsModule } from 'ngx-echarts';
import { IndicatorDashboardChartComponent } from './indicator-dashboard-chart/indicator-dashboard-chart.component';
import { IndicatorDashboardRoutingModule } from './indicator-dashboard-routing.module';
import { IndicatorDashboardTabComponent } from './indicator-dashboard-tab/indicator-dashboard-tab.component';
import { IndicatorDashboardComponent } from './indicator-dashboard.component';
import { IndicatorCarouselComponent } from './indicator-carousel/indicator-carousel.component';
import { IndicatorCarouselItemDirective } from './indicator-carousel/indicator-carousel-item.directive';
import { IndicatorDashboardFiltersComponent } from './indicator-dashboard-chart/indicator-dashboard-filters/indicator-dashboard-filters.component';
import { IndicatorDashboardService } from '@app/core/services/http/indicator-dashboard.service';
import { NgxSliderModule } from '@angular-slider/ngx-slider';
import { AutoCompleteModule } from '@common/modules/auto-complete/auto-complete.module';
import { TabChartGroupFilterPipe } from './indicator-dashboard-tab/chart-group-tag-filter.pipe';
import { ValueCardComponent } from './value-card/value-card.component';
import { IndicatorDashboardGaugeComponent } from './indicator-dashboard-gauge/indicator-dashboard-gauge.component';
import { MergeEchartOptionsPipe } from './indicator-dashboard-chart/merge-options.pipe';
@NgModule({
    imports: [
        CommonUiModule,
        CommonFormsModule,
        ConfirmationDialogModule,
        ListingModule,
        TextFilterModule,
        FormattingModule,
        IndicatorDashboardRoutingModule,
        EditorActionsModule,
        MatCardModule,
        UserSettingsModule,
        NgxSliderModule,
        AutoCompleteModule,
        NgxEchartsModule.forRoot({
            echarts: () => import('echarts')
        }),
    ],
    declarations: [
        IndicatorDashboardComponent,
        IndicatorDashboardTabComponent,
        IndicatorDashboardChartComponent,
        IndicatorCarouselComponent,
        IndicatorCarouselItemDirective,
        IndicatorDashboardFiltersComponent,
        ValueCardComponent,
        IndicatorDashboardGaugeComponent,


        //pipes
        TabChartGroupFilterPipe,
        MergeEchartOptionsPipe
    ],
    providers: [
        IndicatorDashboardService
    ],
    exports:[
        IndicatorDashboardChartComponent,
        IndicatorDashboardGaugeComponent,
        ValueCardComponent
    ]
})
export class IndicatorDashboardModule { }
