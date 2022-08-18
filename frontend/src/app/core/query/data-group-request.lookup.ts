import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';
import { IsActive } from '../enum/is-active.enum';

export class DataGroupRequestLookup extends Lookup implements DataGroupRequestFilter {
	ids: Guid[];
	isActive: IsActive[];

	constructor() {
		super();
	}
}

export interface DataGroupRequestFilter {
	ids: Guid[];
}
