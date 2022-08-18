import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';

export class DataAccessRequestLookup extends Lookup implements DataAccessRequestFilter {
	ids: Guid[];
	excludedIds: Guid[];

	constructor() {
		super();
	}
}

export interface DataAccessRequestFilter {
	ids: Guid[];
	excludedIds: Guid[];
}