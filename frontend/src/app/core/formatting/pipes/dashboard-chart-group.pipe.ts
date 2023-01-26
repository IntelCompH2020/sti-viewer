import { Pipe, PipeTransform } from '@angular/core';
import { IndicatorDashboardChartGroupConfig, IndicatorDashboardTabConfig, TabBlockConfig, TabBlockType } from '../../../ui/indicator-dashboard/indicator-dashboard-config';

@Pipe({ name: 'chartGroups' })
export class DashboardTabBlockToChartGroupBlockPipe implements PipeTransform {

	public transform(value: TabBlockConfig[]): IndicatorDashboardChartGroupConfig[] {
		return value?.filter(config => config.type === TabBlockType.ChartGroup) as IndicatorDashboardChartGroupConfig[];
	}
}
