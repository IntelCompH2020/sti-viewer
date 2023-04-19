import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';
import { DynamicPageType } from '../enum/dynamic-page-type.enum';
import { DynamicPageVisibility } from '../enum/dynamic-page-visibility.enum';

export class DynamicPageLookup extends Lookup implements DynamicPageFilter {
	ids: Guid[];
	like: string;
	excludedIds: Guid[];
	creatorIds: Guid[];
	defaultLanguages: string[];
	visibility: DynamicPageVisibility[];
	type: DynamicPageType[];
	isActive: IsActive[];

	constructor() {
		super();
	}
}

export interface DynamicPageFilter {
	ids: Guid[];
	like: string;
	excludedIds: Guid[];
	creatorIds: Guid[];
	defaultLanguages: string[];
	visibility: DynamicPageVisibility[];
	type: DynamicPageType[];
	isActive: IsActive[];
}
