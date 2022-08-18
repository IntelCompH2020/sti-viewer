import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";

export interface AggregateResponseModel{
    total: number;
    items: AggregateResponseItemModel[];
    hasMore?:boolean; // TODO ASK, dont see it in java equivalent
}

export interface AggregateResponseItemModel{
    group: AggregateResponseGroupModel;
    values: AggregateResponseValueModel[];
}

export interface AggregateResponseGroupModel{
    items: Record<string, string>; // todo first type should be an enumerated value
}

export interface AggregateResponseValueModel{
    field: string;
    value: number;
    aggregateType: MetricAggregateType;
}