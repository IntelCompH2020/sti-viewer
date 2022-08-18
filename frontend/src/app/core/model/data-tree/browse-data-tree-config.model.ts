import { FieldModel } from "../indicator-config/indicator-report-level-config";

export interface BrowseDataTreeConfigModel{
    id: string;
    name: string;
    levelConfigs: BrowseDataTreeLevelConfigModel[];
}


export interface BrowseDataTreeLevelConfigModel{
    field: FieldModel;
    order: number;
    supportSubLevel?: boolean;
    supportedDashboards: string[];
}