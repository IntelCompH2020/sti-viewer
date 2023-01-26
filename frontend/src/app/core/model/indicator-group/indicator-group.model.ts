import { Guid } from "@common/types/guid";
import { Indicator } from "../indicator/indicator.model";

export interface IndicatorGroup{
    id: Guid;
    name: string;
    code: string;
    indicators: Indicator[];
    filterColumns: FilterColumn[];
}

export interface FilterColumn{
    code: string;
    dependsOnCode: string;
}