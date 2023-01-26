import { Component, Input, OnInit } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Component({
    selector:'app-serie-number-value-formatter',
    templateUrl:'./value-number-formattter.component.html'
})
export class SerieValueNumberFormattter implements OnInit{

    @Input()
    formGroup: FormGroup;

    constructor(){

    }
    ngOnInit(): void {
    }

}