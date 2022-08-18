import { BucketAggregateType } from "@app/core/enum/bucket-aggregate-type.enum";
import { DateInterval } from "@app/core/enum/date-interval.enum";
import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";
import { Moment } from "moment";
import { Metric } from "../metic/metric.model";

export interface Bucket{
    type: BucketAggregateType;
    field: string;
    metrics: Metric[];
    having: AggregationMetricHaving;
}



export interface NestedBucket extends Bucket, Omit<Bucket, 'type'>{
    type: BucketAggregateType.Nested;
    bucket: Bucket;
}

export interface CompositeBucket extends Bucket, Omit<Bucket, 'type'>{
    type: BucketAggregateType.Composite;
    sources: CompositeSource[];
    dateHistogramSource: DataHistogramBucket;
    afterKey: Record<string, any>;
}

export interface DataHistogramBucket extends Bucket, Omit<Bucket, 'type'>{
    type: BucketAggregateType.DateHistogram;
    order: BucketOrder;
    interval: DateInterval;
    timezone: Moment;// todo fix this

}

export interface TermsBucket extends Bucket, Omit<Bucket, 'type'>{
    type: BucketAggregateType.Terms;
    order: BucketOrder;
}

export interface CompositeSource{
    field: string;
    order: BucketOrder;
}

type BucketOrder = 'ASC' | 'DESC';

export interface AggregationMetricHaving{
    field: string;
    metricAggregateType: MetricAggregateType;
    type: AggregationMetricHavingType;
    operator: AggregationMetricHavingOperator,
    value: number;
}


export enum AggregationMetricHavingType{
    Simple = 'Simple'
}

export enum AggregationMetricHavingOperator{
    Less = 'Less',
	LessEqual = 'LessEqual',
	Equal = 'Equal',
	Greater = 'Greater',
	GreaterEqual = 'GreaterEqual',
	NotEqual = 'NotEqual'
}