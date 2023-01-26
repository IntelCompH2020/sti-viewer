import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { MetricAggregateType } from "@app/core/enum/metric-aggregate-type.enum";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";

@Component({
    templateUrl: './metric-config-editor.component.html',
    selector: 'metric-config-editor'
})
export class MetricConfigEditorComponent{

    @Input()
    formGroup: FormGroup;

    MetricAggregateTypes = this.enumUtils.getEnumValues<MetricAggregateType>(MetricAggregateType)

    constructor(
        private enumUtils: AppEnumUtils
    ){

    }

}