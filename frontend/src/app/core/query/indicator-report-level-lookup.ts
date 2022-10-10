import { Lookup } from "@common/model/lookup";
import { IndicatorPointLookup } from "./indicator-point.lookup";


export class IndicatorReportLevelLookup extends Lookup implements IndicatorReportLevelLockupFilter {
    configId: string;
    selectedLevels: string[];
    filters: IndicatorPointLookup;
    constructor() {
        super();
    }
}

export interface IndicatorReportLevelLockupFilter {
    configId: string;
    selectedLevels: string[];
    filters: IndicatorPointLookup;
}

// export interface SelectedLevel {
//     code: string;
//     value: string;
// }

export interface LevelFilters {
    like: string;
}
