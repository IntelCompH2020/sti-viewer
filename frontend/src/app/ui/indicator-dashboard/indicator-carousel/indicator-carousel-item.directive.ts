import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: '[appIndicatorCarouselItem]'
})
export class IndicatorCarouselItemDirective {

  constructor(private _elementRef: ElementRef) {

    const htmlElement = this._elementRef.nativeElement as HTMLElement;
    htmlElement.classList.add('col-12')

  }

}
