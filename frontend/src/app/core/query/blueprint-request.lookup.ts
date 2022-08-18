import { BlueprintRequestState } from '@app/core/enum/blueprint-request-state.enum';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';

export class BlueprintRequestLookup extends Lookup implements BlueprintRequestFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	state: BlueprintRequestState[];
	userIds: Guid[];

	constructor() {
		super();
	}
}

export interface BlueprintRequestFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	state: BlueprintRequestState[];
	userIds: Guid[];
}
