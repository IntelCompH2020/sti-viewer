export interface BrowseDataTreeConfigModel {
	id: string;
	name: string;
	levelConfigs: BrowseDataTreeLevelConfigModel[];
	order: number;
	goTo: string;
}


export interface BrowseDataTreeLevelConfigModel {
	field: BrowseDataFieldModel;
	order: number;
	supportSubLevel?: boolean;
	defaultDashboards: string[];
	dashboardOverrides: BrowseDataTreeLevelDashboardOverrideModel[];
}

export interface BrowseDataTreeLevelDashboardOverrideModel {
	// code: string;
	// defaultDashboards: string[];
	requirements: DataTreeLevelDashboardOverrideFieldRequirement[];
	supportedDashboards:string[];
	supportSubLevel: boolean;

}


export interface BrowseDataFieldModel{
	code: string;
	name: string;
}
export interface DataTreeLevelDashboardOverrideFieldRequirement{
	field: string;
	value: string;
}