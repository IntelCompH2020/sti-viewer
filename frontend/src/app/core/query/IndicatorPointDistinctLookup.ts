
import { Guid } from "@common/types/guid";
import { ElasticOrderEnum } from "../enum/elastic-order.enum";
import { IndicatorPointLookup } from "./indicator-point.lookup";

export class IndicatorPointDistinctLookup {

    indicatorPointQuery: IndicatorPointLookup;
    like: string;
    excludedValues: string[];
    field: string;
    indicatorIds: Guid[];
    order: ElasticOrderEnum;
    batchSize: number;
    afterKey: Map<string, Object>;
    viewNotApprovedValues: boolean;
    constructor() {
    }
}
