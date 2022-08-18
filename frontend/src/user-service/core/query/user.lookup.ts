import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';

export interface UserFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	firstName: string[];
	lastName: string[];
}

export class UserLookup extends Lookup implements UserFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	firstName: string[];
	lastName: string[];
	constructor() {
		super();
	}
}
