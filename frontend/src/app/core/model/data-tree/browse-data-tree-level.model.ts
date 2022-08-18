export interface BrowseDataTreeLevelModel{
    field: BrowseDataFieldModel;
    order: number;
    supportSubLevel?: boolean;
    supportedDashboards: string[];
    items: BrowseDataTreeLevelItemModel[];

}

export interface BrowseDataTreeLevelItemModel{
    value: string;
}

export interface BrowseDataFieldModel{
    code: string;
    name: string;
}