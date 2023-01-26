import { IndicatorGroup } from "../indicator-group/indicator-group.model";

export interface PortofolioConfig{
    indicatorGroup: IndicatorGroup;
    code: string;
    name: string;
    columns: PortofolioColumnConfig[];
    defaultDashboards: string[];
}


export interface PortofolioColumnConfig{
    field: DataField;
    major: boolean;
    defaultDashboards: string[];
    order: number;
    dashboardOverrides: PortofolioColumnDashboardOverride[];
}


export interface DataField{
    code: string;
    name: string;
}


export interface PortofolioColumnDashboardOverride{
    requirements: PortofolioColumnDashboardOverrideFieldRequirement[];
    supportedDashboards: string[];
}


export interface PortofolioColumnDashboardOverrideFieldRequirement{
    value: string;
}