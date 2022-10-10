import { Guid } from '@common/types/guid';

interface SearchViewConfig {
	fields: {
		title: string;
		code: string;
		type: SearchViewConfigType;
		dashboardFilterCode?: string;
	}[];
}

export enum SearchViewConfigType {
	Title = 'title',
	Subtitle = 'subtitle',
	Text = 'text'
}


export interface SearchConfiguration {
	id: string;
	dictinctField: string;
	searchFields: string[];
	indicatorIds: Guid[];
	staticFilters: {
		keywordsFilters: {field: string, value: string[]}[];
	};
	viewConfig: SearchViewConfig;
	supportedDashboards: string[];
	dashboardFilters: {
		staticFilters: {
			keywordsFilters: {field: string, value: string[]}[];
		}
	};
}
