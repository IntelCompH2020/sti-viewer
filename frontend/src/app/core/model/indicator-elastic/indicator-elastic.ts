import { IndicatorElasticBaseType } from "@app/core/enum/indicator-elastic-base-type.enum";
import { BaseEntity } from "@common/base/base-entity.model";
import { Guid } from "@common/types/guid";


export interface IndicatorElastic extends BaseEntity {
    id: Guid;
    schema: Schema;
}

export interface Schema extends BaseEntity {
    fields: Field[];
}

export interface Field extends BaseEntity {
    code: string;
    name: string;
    baseType: IndicatorElasticBaseType
}