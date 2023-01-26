import { Component, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    templateUrl:'./pie-chart-configuration.component.html',
    selector: 'app-dashboard-pie-chart-configuration'
})

export class PieChartConfigurationComponent{
    @Input()
    formGroup: FormGroup;
}