import { Bucket } from "../model/bucket/bucket.model";
import { Metric } from "../model/metic/metric.model";
import { IndicatorPointLookup } from "./indicator-point.lookup";

export class IndicatorPointReportLookup  implements IndicatorPointReportLookupFilter{
    filters: IndicatorPointLookup;
    metrics: Metric[];
    bucket: Bucket;

	constructor() {
	}
}

export interface IndicatorPointReportLookupFilter {
    filters: IndicatorPointLookup;
	metrics: Metric[];
    bucket: Bucket;
}
