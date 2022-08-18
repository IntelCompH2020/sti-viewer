import { Lookup } from "@common/model/lookup";
import { Guid } from "@common/types/guid";


export class IndicatorReportLevelLookup extends Lookup implements IndicatorReportLevelLockupFilter {
    // indicatorId: Guid;
    configId: string;
    selectedLevels: SelectedLevel[];
    levelFilter: LevelFilters;
    constructor() {
        super();
    }
}

export interface IndicatorReportLevelLockupFilter {
    // indicatorId: Guid;
    configId: string;
    selectedLevels: SelectedLevel[];
    levelFilter: LevelFilters;
}

export interface SelectedLevel {
    code: string;
    value: string;
}

export interface LevelFilters {
    like: string;
}
