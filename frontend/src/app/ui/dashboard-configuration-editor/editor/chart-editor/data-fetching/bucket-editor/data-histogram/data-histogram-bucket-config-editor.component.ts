import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { DateInterval } from "@app/core/enum/date-interval.enum";
import { AppEnumUtils } from "@app/core/formatting/enum-utils.service";

@Component({
    templateUrl: './data-histogram-bucket-config-editor.component.html',
    selector: 'data-histogram-bucket-config-editor'
})
export class DataHistogramBucketConfigEditorComponent{

    @Input()
    formGroup: FormGroup;


    DateIntervals : DateInterval[] = this.enumUtils.getEnumValues<DateInterval>(DateInterval);

    constructor(
        private enumUtils: AppEnumUtils
    ){

    }

}