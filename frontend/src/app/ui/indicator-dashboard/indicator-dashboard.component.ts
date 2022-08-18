import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BookmarkType } from '@app/core/enum/bookmark-type.enum';
import { Bookmark } from '@app/core/model/bookmark/bookmark.model';
import { IndicatorPointKeywordFilter } from '@app/core/query/indicator-point.lookup';
import { SelectedLevel } from '@app/core/query/indicator-report-level-lookup';
import { BookmarkService } from '@app/core/services/http/bookmark.service';
import { IndicatorDashboardService } from '@app/core/services/http/indicator-dashboard.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { of } from 'rxjs';
import { switchMap, takeUntil, tap } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { IndicatorDashboardConfig } from './indicator-dashboard-config';


const DASHBOARD_CONFIG:IndicatorDashboardConfig = require('./dashboard-config2.json');

@Component({
	selector: 'app-indicator-dashboard',
	templateUrl: './indicator-dashboard.component.html',
	styleUrls: ['./indicator-dashboard.component.css']
})
export class IndicatorDashboardComponent extends BaseComponent implements OnInit {
	selectedTabIndex = -1;

	dashboardConfig:IndicatorDashboardConfig;
	keywordFilters: IndicatorPointKeywordFilter[];


	indicatorQueryParams: IndicatorQueryParams;

	bookmark: Partial<Bookmark>;

	bookmarks: Bookmark[];

	constructor(
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private _activaterRoute: ActivatedRoute,
		private _queryParamsService: QueryParamsService,
		private _dashboardService: IndicatorDashboardService,
		private bookmarkService: BookmarkService
		) {
		super();

		this._activaterRoute.queryParams.pipe(
			switchMap(
				params => {				
					this.bookmark = null;
					if(params.params){
						this.indicatorQueryParams = this._queryParamsService.deserializeObject<IndicatorQueryParams>(params.params);
						this._validateBookmark();
						return this._dashboardService.getDashboard(this.indicatorQueryParams.dashboard).pipe(tap((config) => console.log('%c Dashboard configuration', 'color:green', config)));
					}
					return of(DASHBOARD_CONFIG);
			}),
			takeUntil(this._destroyed)
		)
		.subscribe(
			(configuration: IndicatorDashboardConfig) => {
				this.dashboardConfig = configuration;
				this.selectedTabIndex = 0;
			},
			error => {
				// this.dashboardConfig = DASHBOARD_CONFIG; 
			}
			
		)

		
	}



	saveBookmark(): void{
		this.bookmarkService.persist({
			name: this.indicatorQueryParams.displayName,
			type: BookmarkType.Dashboard,
			value: JSON.stringify(this.indicatorQueryParams),
		},
		[
			nameof<Bookmark>(x => x.id)
		])
		.pipe(
			takeUntil(this._destroyed)
		)
		.subscribe(response => {
			this._validateBookmark();
		})
	}


	deleteBookmark(): void{
		if (this.bookmark){
			this.bookmarkService.delete(this.bookmark.id)
			.pipe(takeUntil(this._destroyed))
			.subscribe(() => this._validateBookmark());
		}

	}

	toggleBookmark():void{
		
		if (this.bookmark){
			this.deleteBookmark();
			return;
		}
		this.saveBookmark();
	}
	ngOnInit(): void {

	}

	selectTab(index: number) {
		this.selectedTabIndex = index;
	}


	private _validateBookmark():void{
						
		// get bookmark
		this.bookmark = null;
		this.bookmarkService.exists({
			type: BookmarkType.Dashboard,
			value: this._queryParamsService.serializeObject(this.indicatorQueryParams),
		},
		[
			nameof<Bookmark>(x => x.id)
		]
		).pipe(takeUntil(this._destroyed)).subscribe((bookmark) =>{
			if(bookmark){
				this.bookmark = bookmark
			}
		})
	}
}


export interface IndicatorQueryParams{
	dashboard: string;
	levels: SelectedLevel[];
	displayName: string;
	groupHash?: string;
}