import { Lookup } from "@common/model/lookup";
import { Guid } from "@common/types/guid";
import { IsActive } from "../enum/is-active.enum";
import { IndicatorFilter } from "./indicator.lookup";

export class IndicatorElasticLookup extends Lookup implements IndicatorFilter {
    ids: Guid[];
    excludedIds: Guid[];
    like: string;
    isActive: IsActive[];

    constructor() {
        super();
    }
}

export interface IndicatorElasticFilter {
    ids: Guid[];
    excludedIds: Guid[];
    like: string;
    isActive: IsActive[];
}