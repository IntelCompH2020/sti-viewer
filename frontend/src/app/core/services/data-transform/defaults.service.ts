import { Injectable } from '@angular/core';
import { IndicatorDashboardConfig, IndicatorDashboardTabConfig, TabBlockConfig, TabBlockType } from '@app/ui/indicator-dashboard/indicator-dashboard-config';


@Injectable({
    providedIn : 'root'
})
export class DefaultsService{

    /**
     * Pure function that applies defaults in the returned dashboard config
     * 
     * @param dashboardConfig 
     * @returns 
     */
    public enrichDashboardConfigWithDefaults(dashboardConfig: IndicatorDashboardConfig): IndicatorDashboardConfig{

        if(!dashboardConfig){
			return dashboardConfig;
		}

        // extract options that we want to default
		let { tabs,...rest} = dashboardConfig;

        // * defaulting TABS
		if(tabs?.length){
			tabs = tabs.map(tab => this.enrichDasbhoardTabConfigWithDefaults(tab))
		}

		return { tabs, ...rest };

    }

    /**
     * Pure function that applies defaults in the returned dashboard tab config
     * @param tab 
     * @returns 
     */
    public enrichDasbhoardTabConfigWithDefaults(tab: IndicatorDashboardTabConfig): IndicatorDashboardTabConfig{
        if( !tab ){
            return tab;
        }

        // extract fields that we would like to default
        let {chartGroups, ...rest} = tab;

        if(chartGroups?.length){
            chartGroups = chartGroups.map(group => this.enrichDashboardTabBlockConfig(group))
        }
        return {chartGroups, ...rest};
    }

    /**
     * Pure function
     * @param config 
     * @returns 
     */
    public enrichDashboardTabBlockConfig(config: TabBlockConfig): TabBlockConfig{
        if(!config){
            return config;
        }

        let { type, ...rest} = config;
        type = type || TabBlockType.ChartGroup;

        return {type, ...rest} as TabBlockConfig;
    }


}