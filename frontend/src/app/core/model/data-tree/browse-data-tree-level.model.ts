export interface BrowseDataTreeLevelModel {
	field: BrowseDataFieldModel;
	order: number;
	supportSubLevel?: boolean;
	supportedDashboards: string[];
	items: BrowseDataTreeLevelItemModel[];

}

export interface BrowseDataTreeLevelItemModel {
	value: string;
	supportedDashboards: string[];
	supportSubLevel: boolean;
}

export interface BrowseDataFieldModel {
	code: string;
	name: string;
}
