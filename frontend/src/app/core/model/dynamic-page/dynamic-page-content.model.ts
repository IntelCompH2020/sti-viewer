import { DynamicPageType } from '@app/core/enum/dynamic-page-type.enum';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';
import { Guid } from '@common/types/guid';
import { DynamicPage } from './dynamic-page.model';


export interface DynamicPageContent extends BaseEntity {
	page: DynamicPage;
	title: string;
	content: string;
	language: string;
}


export interface DynamicPageContentPersist extends BaseEntityPersist {
	pageId: Guid;
	title: string;
	content: string;
	language: string;
}

export interface DynamicPageContentData  {
	id: Guid;
	type: DynamicPageType;
	title: string;
	content: string;
}

export interface DynamicPageContentRequest {
	id: Guid;
	language: string;
}
