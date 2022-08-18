import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";

export interface Metric{
    type: MetricAggregateType;
    field: string;
}