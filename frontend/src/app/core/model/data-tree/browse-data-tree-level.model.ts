export interface BrowseDataTreeLevelModel {
	field: BrowseDataFieldModel;
	order: number;
	supportSubLevel?: boolean;
	supportedDashboards: string[];
	items: BrowseDataTreeLevelItemModel[];

}

export interface BrowseDataTreeLevelItemModel {
	value: string;
	hasNewData: boolean;
	supportedDashboards: string[];
	supportSubLevel: boolean;
}

export interface BrowseDataFieldModel {
	code: string;
	name: string;
}


export interface UpdateDataTreeLastAccess {
	configId: string;
}
