import { Component, Input } from '@angular/core';


@Component({
    selector: 'app-value-card',
    templateUrl:'./value-card.component.html',
    styleUrls: [
        './value-card.component.scss'
    ]
})
export class ValueCardComponent{

    @Input()
    value: string | number;

    @Input()
    description: string;
    
}