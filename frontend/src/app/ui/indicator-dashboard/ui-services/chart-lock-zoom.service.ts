import { Injectable, OnDestroy } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Injectable()
export class ChartLockZoomService implements OnDestroy {


    private _lockSubject$ = new BehaviorSubject<boolean>(true);
    public graphsLocked$ = this._lockSubject$.asObservable();


    public lockGraphs(): void{
        this._lockSubject$.next(true);
    }

    public unlockGraphs(): void{
        this._lockSubject$.next(false);
    }



    ngOnDestroy(): void {
        this._lockSubject$.complete();
    }

}