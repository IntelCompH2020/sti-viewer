import { Guid } from "@common/types/guid";
import { Indicator } from "../indicator/indicator.model";

export interface IndicatorGroup{
    id: Guid;
    name: string;
    dashboardKey: string;
    indicators: Indicator[];
    filterColumns: string[];
}