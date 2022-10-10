import { Bucket } from "../model/bucket/bucket.model";
import { Metric } from "../model/metic/metric.model";
import { IndicatorPointLookup } from "./indicator-point.lookup";
import {Lookup} from '../../../common/model/lookup';

export class IndicatorPointReportLookup  implements IndicatorPointReportLookupFilter{
    filters: IndicatorPointLookup;
    metrics: Metric[];
    bucket: Bucket;
    isRawData?: boolean;
    rawDataRequest?: RawDataRequest;

	constructor() {
	}
}

export interface IndicatorPointReportLookupFilter {
    filters: IndicatorPointLookup;
	metrics: Metric[];
    bucket: Bucket;
}


export interface RawDataRequest{
    keyField: string;
    valueField: string;
    page?: Lookup.Paging;
    order: Lookup.Ordering;
}