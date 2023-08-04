import { Component, Injectable, OnInit } from '@angular/core';
import { MatPaginatorIntl, PageEvent } from '@angular/material/paginator';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { ElasticOrderEnum } from '@app/core/enum/elastic-order.enum';
import { IndicatorPointKeywordFilter, IndicatorPointLookup } from '@app/core/query/indicator-point.lookup';
import { IndicatorPointDistinctLookup } from '@app/core/query/IndicatorPointDistinctLookup';
import { IndicatorPointService } from '@app/core/services/http/indicator-point.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseComponent } from '@common/base/base.component';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import {filter, map, takeUntil} from 'rxjs/operators';
import { IndicatorQueryParams } from '../indicator-dashboard/indicator-dashboard.component';
import { SearchConfiguration, SearchViewConfigType } from './search-config.model';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';


@Injectable()
export class CustomMatPaginatorIntl extends MatPaginatorIntl {

	constructor(private language: TranslateService){
		super();

		this.itemsPerPageLabel = this.language.instant('APP.SEARCH-COMPONENT.PAGINATOR.ITEMS-PER-PAGE');
		this.nextPageLabel     = this.language.instant('APP.SEARCH-COMPONENT.PAGINATOR.NEXT-PAGE');
		this.previousPageLabel = this.language.instant('APP.SEARCH-COMPONENT.PAGINATOR.PREVIOUS-PAGE');
	}


};
@Component({
	selector: 'app-search',
	templateUrl: './search.component.html',
	styleUrls: ['./search.component.scss'],
	animations: GENERAL_ANIMATIONS,
	providers: [
		{
			provide: MatPaginatorIntl, useClass: CustomMatPaginatorIntl
		}
	]
})
export class SearchComponent extends BaseComponent implements OnInit{


	protected results: SearchResult[] = [];

	protected searchTerm: string = '';
	protected previousSearchTerm: string = '';

	protected searchConfiguration: SearchConfiguration;

	private DEFAULT_PAGE_SIZE = 10;
	protected pageSize = this.DEFAULT_PAGE_SIZE;
	protected pageSizeOptions = [5, 10, 25, 100];
	protected currentPage = 0;
	protected totalResultsCount = 0;



	constructor(
		private indicatorPointService: IndicatorPointService,
		private installationConfigurationService: InstallationConfigurationService,
		private router: Router,
		private queryParamsService: QueryParamsService,
		private activeRoute: ActivatedRoute
	) {
		super();




		// register for querparams changes
		combineLatest([
			this.loadConfiguration(),
			this.activeRoute.queryParams
		])
		.pipe(
			filter(([configuration]) => !!configuration),//make sure configuration loaded
			takeUntil(this._destroyed)
		)
		.subscribe(([_configuration, queryparams]) =>{
			const q = queryparams?.q;
			if(q){
				this.searchTerm = q;
			}else{
				this.searchTerm = ''
			}

			const page = queryparams.page;
			if(!isNaN(page) && Number.parseInt(page) >=0){
				this.currentPage = Number.parseInt(page);
			}else{
				this.currentPage = 0;
			}

			const pageSize = queryparams.size;

			if(!isNaN(pageSize) && this.pageSizeOptions.includes(Number.parseInt(pageSize))){
				this.pageSize = Number.parseInt(pageSize);
			}else{
				this.pageSize = this.DEFAULT_PAGE_SIZE;
			}

			this.queryIndicators();
		});
	}

	ngOnInit(): void {
	}



	protected search(): void{


		const queryParams: Params = {
			q: this.searchTerm,
		};

		if(this.pageSize !== this.DEFAULT_PAGE_SIZE){
			queryParams.size = this.pageSize;
		}
		this.router.navigate([], {queryParams, relativeTo: this.activeRoute});
	}



	protected navigate(field: FieldInfo): void{
		this.navigateToDashboard(
			{
				keywordFilters:[
					{
						field: field.fieldCode,
						values: [field.payload]
					}
				],
				title: field.textValue
			})
	}

	protected onPageChange(pageEvent: PageEvent): void{


		const pageSizeChanged = this.pageSize !== pageEvent.pageSize;

		this.router.navigate([], {queryParams: {
			q: this.searchTerm,
			page: pageSizeChanged ? 0 : pageEvent.pageIndex,
			size: pageEvent.pageSize
		}, relativeTo: this.activeRoute});
	}


	// * PRIVATE


	private queryIndicators():void{

		const searchTerm = this.searchTerm;
		const lookup = this._buildLookup({ searchTerm: this.searchTerm, page: this.currentPage });

		if (this.searchConfiguration.dictinctField && this.searchConfiguration.dictinctField.length > 0) {
			const indicatorPointDistinctLookup = new IndicatorPointDistinctLookup();
			indicatorPointDistinctLookup.indicatorPointQuery = lookup;
			indicatorPointDistinctLookup.field = this.searchConfiguration.dictinctField;
			indicatorPointDistinctLookup.batchSize = 1000;
			indicatorPointDistinctLookup.indicatorIds = lookup.indicatorIds;
			indicatorPointDistinctLookup.order = ElasticOrderEnum.ASC;
			this.indicatorPointService
				.getIndicatorPointQueryDistinct(indicatorPointDistinctLookup)
				.pipe(takeUntil(this._destroyed))
				.subscribe((response) => {
					this.results = this._buildSearchResults(response.items.map(x => ({ [indicatorPointDistinctLookup.field]: x}) ));
					this.previousSearchTerm = searchTerm;
					this.totalResultsCount = response.count;
				});
		} else {
			this.indicatorPointService
				.query(lookup)
				.pipe(takeUntil(this._destroyed))
				.subscribe((response) => {
					this.results = this._buildSearchResults(response.items);
					this.previousSearchTerm = searchTerm;
					this.totalResultsCount = response.count;
				});
		}
	}

	private loadConfiguration(): Observable<SearchConfiguration>{
		const globalConfig = 'global_search_config';

		return this.indicatorPointService.getGlobalSearchConfig(globalConfig)
			.pipe(
				takeUntil(this._destroyed),
				map((configuration) => {
					this.searchConfiguration = configuration as unknown as SearchConfiguration
					return this.searchConfiguration;
				})
			);
	}

	private navigateToDashboard(options: {title?:string,keywordFilters: IndicatorPointKeywordFilter[]}):void{
		const { keywordFilters, title = '' } = options;

		this.router.navigate(['indicator-report','dashboard'], { queryParams: {


            params: this.queryParamsService.serializeObject<IndicatorQueryParams>({
            //   levels: [{
            //     code:this.column,
            //     value:field
            //   }],

              displayName: title,
              dashboard: this.searchConfiguration.supportedDashboards?.[0], // todo more than supported dashboards?
              keywordFilters: [
                ...(
					this.searchConfiguration
						?.dashboardFilters
						?.staticFilters
						?.keywordsFilters
						?.map(kf => ({field: kf.field, values: kf.value}))
					?? []
				),
				...keywordFilters

              ]
            })}
          });
	}



	private _buildLookup(options: {searchTerm: string, page?: number}): IndicatorPointLookup{
		const {searchTerm = '', page = 0} = options;
		const lookup = new IndicatorPointLookup();


		lookup.indicatorIds = this.searchConfiguration.indicatorIds;
		lookup.keywordFilters = this.searchConfiguration.staticFilters.keywordsFilters.map(filter =>({field: filter.field, values: filter.value}) );

		lookup.fieldLikeFilter = {
			fields: this.searchConfiguration.searchFields,
			like: searchTerm
		}

		lookup.project = {
			fields: [
				...this.searchConfiguration.viewConfig.fields.map(viewConfig => viewConfig.code),
				...this.searchConfiguration.viewConfig.fields.map(viewConfig => viewConfig.dashboardFilterCode).filter(x => x),
			]
		}

		lookup.order={
			items:[
				'-score'
			]
		}
		lookup.page={
			offset: page * this.pageSize,
			size: this.pageSize
		}
		lookup.metadata = {
			countAll: true
		}

		return lookup;
	}

	private _buildSearchResults(items: Record<string, string>[]): SearchResult[]{

		const titleKeys = this.searchConfiguration.viewConfig.fields.filter(x => x.type === SearchViewConfigType.Title);
		const subtitleKeys = this.searchConfiguration.viewConfig.fields.filter(x => x.type === SearchViewConfigType.Subtitle);
		const textKeys = this.searchConfiguration.viewConfig.fields.filter(x => x.type === SearchViewConfigType.Text);

		const searchResults: SearchResult[] = items.map(item => {
			const titles:FieldInfo[] = titleKeys.map(titleKey => ({textValue: `${item[titleKey.code]}`, payload:item[titleKey.dashboardFilterCode], fieldCode: titleKey.dashboardFilterCode}));
			const subtitles:FieldInfo[] = subtitleKeys.map(titleKey => ({textValue: `${titleKey.title} : ${item[titleKey.code]}`, payload:item[titleKey.dashboardFilterCode], fieldCode: titleKey.dashboardFilterCode}));
			const text = textKeys.map(titleKey => `${item[titleKey.code]}`).join( ' | ');


			return {
				subtitle: subtitles,
				text,
				title:titles,
				availableForPresentation: this.installationConfigurationService.availableForSearchPresentation?.includes(titles.map(x => x.textValue)?.join(''))
			};
		});


		return searchResults;
	}
}


interface SearchResult{
	title: FieldInfo[];
	subtitle: FieldInfo[];
	text: string;
	availableForPresentation?: boolean;
}


interface FieldInfo{
	textValue: string;
	payload: string;
	fieldCode: string;
}
