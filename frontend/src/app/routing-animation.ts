import { animate, group, query, style, transition, trigger } from "@angular/animations";

export const ROUTING_ANIMATIONS = trigger('routingAnimation', [


    transition("*=>*", [


        group([
            query(':enter', [
                style({
                    // position: 'absolute',
                    // width: '100%',
                    // height: '100%',
                    // filter: 'grayscale(1)',
                    // transform:'translateX(-100%)',
                    opacity: 0
                }),
                animate('400ms 400ms ease', 
                style({ 
                    // transform:'translateX(0)',
                    opacity: 1,
                    // filter: 'grayscale(0)',
                }))
              ], { optional: true}
            ),
            query(':leave', [
                style({
                    position: 'absolute',
                    // filter: 'grayscale(1)',
                    width: '100%',
                    height: '100%',
                    // transform:'translateX(0)',
                    opacity: 1
                }),
                animate('400ms ease', 
                style({ 
                    // transform:'translateX(100%)',
                    opacity: 0
                }))
              ], { optional: true}
            )

        ])
        
    ])
])