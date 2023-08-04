import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';

export class ExternalTokenLookup extends Lookup implements ExternalTokenFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	expiresAtFrom: Date;
	expiresAtTo: Date;

	constructor() {
		super();
	}
}

export interface ExternalTokenFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	expiresAtFrom: Date;
	expiresAtTo: Date;
	isActive: IsActive[];
}
