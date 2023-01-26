import { DataAccessRequestStatus } from "@app/core/enum/data-access-request-status.enum";
import { IndicatorGroup } from "./indicator-group.model";

export interface IndicatorGroupAccessConfigView{
    group: IndicatorGroup;
    filterColumns: IndicatorGroupAccessColumnConfigView[];
}

export interface IndicatorGroupAccessColumnConfigView{
    code: string;
    items: IndicatorGroupAccessColumnConfigItemView[];
}

export interface IndicatorGroupAccessColumnConfigItemView{
    value: string;
    status: DataAccessRequestStatus;
}
