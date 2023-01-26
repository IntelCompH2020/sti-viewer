import { Pipe, PipeTransform } from '@angular/core';
import { IndicatorDashboardChartGroupConfig, IndicatorDashboardTabConfig, TabBlockConfig, TabBlockType } from '../indicator-dashboard-config';

@Pipe({ name: 'tabChartGroupFilter' })
export class TabChartGroupFilterPipe implements PipeTransform {
	constructor() { }

	public transform(value: IndicatorDashboardTabConfig, activeTags: string[]): TabBlockConfig[] {


		const chartGroups = value.chartGroups;;

		if(!chartGroups?.length){
			return chartGroups;
		}
		if(!activeTags?.length){
			return chartGroups;
		}

		return chartGroups.map(
			group => {

				if(!group){
					return group;
				}

				if(group.type === TabBlockType.ChartGroup){
					let { charts, ...rest } = group as IndicatorDashboardChartGroupConfig;;

					charts = charts?.filter(
						chart => chart.tags?.attachedTags?.some(tag => activeTags.includes(tag))
					);
					if(!charts?.length) return null;

					return { ...rest, charts }

				}
				
				return group;

			}
		).filter(x => !!x)



		// * LEGACY
		// const chartGroups = this.tabBlockToChartGroupBlock.transform(value.chartGroups);

		// if(!chartGroups?.length){
		// 	return chartGroups;
		// }
		// if(!activeTags?.length){
		// 	return chartGroups;
		// }

		// return chartGroups.map(
		// 	({charts, ...rest}) => ({
		// 		...rest,
		// 		charts: charts?.filter(
		// 			chart => chart.tags?.attachedTags?.some(tag => activeTags.includes(tag))
		// 		)
		// 	})
		// ).filter(chartGroup => !!chartGroup?.charts?.length);


		
	}
}
