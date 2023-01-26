import { animate, query, stagger, style, transition, trigger } from "@angular/animations";

export const GENERAL_ANIMATIONS = [
    trigger('fadeIn',[
        transition(':enter',[
            style({
                opacity:0
            }),
            animate('800ms ease', style({opacity:1}))
        ])
    ]),
    trigger('fadeOut',[
        transition(':leave',[
            style({
                opacity:1,
                width: '100%',
                position: 'absolute'
            }),
            animate('600ms ease', style({opacity:0}))
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
    ]),
    trigger('listAnimationSequentialScaleIn', [
        transition('* => *', [ // each time the binding value changes
          query(':enter', [
            style({
                opacity:0,
                transform:'scale(0)'
            }),
            stagger(50, [
                animate('600ms cubic-bezier(.41,.9,.36,1.44)', style({
                    opacity:1,
                    transform:'scale(1)'
                }))
            ])
          ], { optional: true})
        ])
    ])
]