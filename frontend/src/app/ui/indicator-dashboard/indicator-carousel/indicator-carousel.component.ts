import { Component, ContentChildren, OnInit, QueryList, } from '@angular/core';
import { IndicatorCarouselItemDirective } from './indicator-carousel-item.directive';

@Component({
  selector: 'app-indicator-carousel',
  templateUrl: './indicator-carousel.component.html',
  styleUrls: ['./indicator-carousel.component.scss'],
})
export class IndicatorCarouselComponent implements OnInit {

  @ContentChildren(IndicatorCarouselItemDirective) indicators : QueryList<IndicatorCarouselItemDirective>;

  slideIndex = 0;

  get slide(){
    return {
      transform: `translateX(-${this.slideIndex * 100}%)`
    }
  }

  constructor() { }

  ngOnInit(): void {
  }


  setSelectedSlide(index: number){
    this.slideIndex = index;
  }
}
