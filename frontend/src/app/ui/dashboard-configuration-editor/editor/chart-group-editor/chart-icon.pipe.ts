import { Pipe, PipeTransform } from "@angular/core";
import { IndicatorDashboardChartType } from "@app/ui/indicator-dashboard/indicator-dashboard-config";

@Pipe({
    name: 'chartIcon'
})
export class ChartIconPipe implements PipeTransform{
    transform(type: IndicatorDashboardChartType) {
        switch(type){
            case IndicatorDashboardChartType.Line:
                return 'show_chart'
                
            case IndicatorDashboardChartType.Bar:
                return 'bar_chart'

            case IndicatorDashboardChartType.Pie:
                return 'donut_small'

            case IndicatorDashboardChartType.PolarBar:
                return 'data_usage'

            case IndicatorDashboardChartType.Map:
                return 'public'

            case IndicatorDashboardChartType.Graph:
                return 'schema'

            case IndicatorDashboardChartType.TreeMap:
                return 'table'
            case IndicatorDashboardChartType.Sankey:
                return 'account_tree'

            default: 
                return 'help';
        }
    }
    
}