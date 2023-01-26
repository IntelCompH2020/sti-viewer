import {Injectable} from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class DashboardUITagsService{
    private _activeChartTags = new Set<string>();

    private _activeChartTagsSubject$ = new BehaviorSubject<string[]>([...this._activeChartTags]);

    public activeChartTagsChanges$ = this._activeChartTagsSubject$.asObservable();

    public get activChartTagsSnapshot(): readonly string[]{
        return [...this._activeChartTags];
    }

    public addChartTag(tag: string): void {
        if(!tag){
            return;
        }

        const sizeBefore = this._activeChartTags.size;
        this._activeChartTags.add(tag);

        if(sizeBefore !== this._activeChartTags.size){
            this._notifyObservers();
        }
    }

    public removeChartTag(tag: string): void {
        const sizeBefore = this._activeChartTags.size;
        this._activeChartTags.delete(tag);

        if(sizeBefore !== this._activeChartTags.size){
            this._notifyObservers();
        }
    }


    public toggleChartTag(tag: string): void {

        if(this._activeChartTags.has(tag)){
            this.removeChartTag(tag);
            return;
        }

        this.addChartTag(tag);
        
    }




    private _notifyObservers():void{
        queueMicrotask(
            () => this._activeChartTagsSubject$.next([...this._activeChartTags])
        );
    }

}