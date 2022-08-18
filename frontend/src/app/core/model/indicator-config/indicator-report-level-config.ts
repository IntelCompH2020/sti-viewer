import { IndicatorFieldBaseType } from "@app/core/enum/indicator-field-base-type.enum";
import { Guid } from "@common/types/guid";
import { Indicator } from "../indicator/indicator.model";
import { ViewConfig } from "./indicator-config.model";

export interface IndicatorReportlevelConfig {
    indicator: Indicator;
    viewConfig: ViewConfig;
    field: FieldModel;
    items: IndicatorReportLevelConfigItem[];
}

export interface IndicatorReportLevelConfigItem {
    value: string;
    supportSubLevel: boolean;
    canReport: boolean;

}
export interface FieldModel {
    id: Guid
    code: string;
    name: string;
    label: string;
    description: string;
    baseType: IndicatorFieldBaseType;
    typeSemantics: string;
    typeId: string;
    altLabels: AltTextModel[];
    altDescriptions: AltTextModel[];
    valueRange: ValueRangeModel;
    subfieldOf: string;
    valueField: string;
    useAs: string;
    operations: OperatorModel[];
    validation: string;
}

export interface AltTextModel {
    lang: string;
    text: string;

}

export interface ValueRangeModel {
    min: number;
    max: number;

}

export interface OperatorModel {
    op: string;
}
