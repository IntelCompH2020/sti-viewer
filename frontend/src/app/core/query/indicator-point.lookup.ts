import { Guid } from "@common/types/guid";
import { Moment } from "moment";

export class IndicatorPointLookup  implements IndicatorPointLookupFilter{
    ids: Guid[];
    keywordFilters:IndicatorPointKeywordFilter[];
    groupHashes?: string[];
    dateFilters?: IndicatorPointDateFilter[];
    integerFilters?: IndicatorPointIntegerFilter[];
    doubleFilters?: IndicatorPointDoubleFilter[];
    dateRangeFilters?: IndicatorPointDateRangeFilter[];
    integerRangeFilters?: IndicatorPointIntegerRangeFilter[];
    doubleRangeFilters?: IndicatorPointDoubleRangeFilter[];
    fieldLikeFilter?: IndicatorPointLikeFilter[];
    
	constructor() {
	}
}

export interface IndicatorPointLookupFilter {
    ids: Guid[]
    keywordFilters: IndicatorPointKeywordFilter[];
    groupHashes?: string[];
    dateFilters?: IndicatorPointDateFilter[];
    integerFilters?: IndicatorPointIntegerFilter[];
    doubleFilters?: IndicatorPointDoubleFilter[];
    dateRangeFilters?: IndicatorPointDateRangeFilter[];
    integerRangeFilters?: IndicatorPointIntegerRangeFilter[];
    doubleRangeFilters?: IndicatorPointDoubleRangeFilter[];
    fieldLikeFilter?: IndicatorPointLikeFilter[];
}

export interface IndicatorPointKeywordFilter{
    field: string;
    values: string[];
}


export interface IndicatorPointDateFilter{
    field: string;
    value: Moment;
}

export interface IndicatorPointIntegerFilter{
    field: string;
    value: number;
}

export interface IndicatorPointDoubleFilter{
    field: string;
    value: number;
}

export interface IndicatorPointDateRangeFilter{
    field: string;
    from: Moment;
    to : Moment;
}

export interface IndicatorPointIntegerRangeFilter{
    field: string;
    from: number;
    to : number;
}

export interface IndicatorPointDoubleRangeFilter{
    field: string;
    from: number;
    to : number;
}

export interface IndicatorPointLikeFilter{
    fields: string[];
    like: string;
}