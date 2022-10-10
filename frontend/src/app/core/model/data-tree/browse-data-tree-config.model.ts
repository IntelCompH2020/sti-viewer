import { FieldModel } from '../indicator-config/indicator-report-level-config';

export interface BrowseDataTreeConfigModel {
	id: string;
	name: string;
	levelConfigs: BrowseDataTreeLevelConfigModel[];
	order: number;
	goTo: string;
}


export interface BrowseDataTreeLevelConfigModel {
	field: FieldModel;
	order: number;
	supportSubLevel?: boolean;
	defaultDashboards: string[];
	dashboardOverrides: BrowseDataTreeLevelDashboardOverrideModel[];
}

export interface BrowseDataTreeLevelDashboardOverrideModel {
	code: string;
	defaultDashboards: string[];
}
