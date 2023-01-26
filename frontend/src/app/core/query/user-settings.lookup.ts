import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';

export class UserSettingsLookup extends Lookup implements UserSettingsFilter {

	constructor() {
		super();
	}
    ids: Guid[];
    like: string;
}

export interface UserSettingsFilter {
	ids: Guid[];
	like: string;
}