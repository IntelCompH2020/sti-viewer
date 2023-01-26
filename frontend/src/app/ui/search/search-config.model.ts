import { Guid } from '@common/types/guid';

export interface SearchViewConfig {
	fields: SearchViewFieldConfig[];
}

export interface SearchViewFieldConfig{
	title: string;
	code: string;
	type: SearchViewConfigType;
	dashboardFilterCode?: string;
}

export enum SearchViewConfigType {
	Title = 'title',
	Subtitle = 'subtitle',
	Text = 'text'
}

export interface SearchConfigurationStaticFilter{
	keywordsFilters: SearchConfigurationStaticKeywordFilter[];
}

export interface SearchConfigurationStaticKeywordFilter{
	field: string;
	value: string[];
}

export interface SearchConfigurationDashboardFilters{
	staticFilters: SearchConfigurationDashboardStaticFilters; 
}
export interface SearchConfigurationDashboardStaticFilters{
	keywordsFilters: SearchConfigurationDashboardStaticKeywordFilter[];
}

export interface SearchConfigurationDashboardStaticKeywordFilter{
	field: string;
	value: string[];
}

export interface SearchConfiguration {
	id: string;
	dictinctField: string;
	searchFields: string[];
	indicatorIds: Guid[];
	staticFilters: SearchConfigurationStaticFilter;
	viewConfig: SearchViewConfig;
	supportedDashboards: string[];
	dashboardFilters: SearchConfigurationDashboardFilters;
}
