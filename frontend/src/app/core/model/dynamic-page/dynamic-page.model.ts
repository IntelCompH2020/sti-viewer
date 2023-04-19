import { DynamicPageType } from '@app/core/enum/dynamic-page-type.enum';
import { DynamicPageVisibility } from '@app/core/enum/dynamic-page-visibility.enum';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';
import { Guid } from '@common/types/guid';
import { User } from '../user/user.model';
import { DynamicPageContent, DynamicPageContentPersist } from './dynamic-page-content.model';

export interface DynamicPage extends BaseEntity {
	creator: User;
	order: number;
	type: DynamicPageType;
	visibility: DynamicPageVisibility;
	defaultLanguage: string;
	config: DynamicPageConfig;
	pageContents: DynamicPageContent[];
}

export interface DynamicPageConfig {
	allowedRoles: string[];
	externalUrl: string;
	matIcon: string;
}

export interface DynamicPagePersist extends BaseEntityPersist {
	order: number;
	type: DynamicPageType;
	visibility: DynamicPageVisibility;
	defaultLanguage: string;
	config: DynamicPageConfigPersist;
	pageContents: DynamicPageContentPersist[];
}

export interface DynamicPageConfigPersist {
	allowedRoles: string[];
	externalUrl: string;
	matIcon: string;
}

export interface DynamicPageMenuItem extends BaseEntity {
	id: Guid;
	order: number;
	type: DynamicPageType;
	title: string;
	externalUrl: string;
	matIcon: string;
}
