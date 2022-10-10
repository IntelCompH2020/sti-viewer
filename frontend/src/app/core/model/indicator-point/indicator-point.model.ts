import { Guid } from "@common/types/guid";
import { Moment } from "moment";
import { DataGroupColumn } from "../data-group-request/data-group-request.model";

export interface IndicatorPoint{
    id: Guid;
    timestamp: Moment;
    batchId: string;
    batchTimeStamp: Moment;
    groupHash: string;
    groupInfo: DataGroupInfo;
    properties: Record<string, any>;
} 

export interface DataGroupInfo{
    columns: DataGroupInfoColumn[];
}


export interface DataGroupInfoColumn{
    fieldCode: string;
    values: string[];
}