import { animate, query, stagger, style, transition, trigger } from "@angular/animations";

export const GENERAL_ANIMATIONS = [
    trigger('fadeIn',[
        transition(':enter',[
            style({
                opacity:0
            }),
            animate('800ms ease-out', style({opacity:1}))
        ])
    ]),
    trigger('leftFlyIn',[
        transition(':enter',[
            style({
                transform:'translateX(-100%)',
                opacity:0
            }),
            animate('600ms ease-out', style({transform:'translateX(0)', opacity:1}))
        ])
    ]),
    trigger('listAnimation', [
        transition('* => *', [ // each time the binding value changes
          query(':enter', [
            style({
                transform:'translateX(-100%)',
                opacity:0
            }),
            // stagger(40, [
            //     animate('500ms ease-out', style({transform:'translateX(0)', opacity:1}))
            // ])
            // stagger(40, [
                animate('500ms ease-out', style({transform:'translateX(0)', opacity:1}))
            // ])
          ], { optional: true})
        ])
    ]),
    trigger('listAnimationSequential', [
        transition('* => *', [ // each time the binding value changes
          query(':enter', [
            style({
                opacity:0
            }),
            stagger(50, [
                animate('800ms ease-out', style({opacity:1}))
            ])
          ], { optional: true})
        ])
    ])
]