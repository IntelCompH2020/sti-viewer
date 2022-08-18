import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';
import { UserLookup } from '@user-service/core/query/user.lookup';
import { BookmarkType } from '../enum/bookmark-type.enum';

export class BookmarkLookup extends Lookup implements BookmarkFilter {
    like: string;
	isActive: IsActive[];
    types: BookmarkType[];
	ids: Guid[];
	userIds: Guid[];
    userSubQuery: UserLookup;
    
	constructor() {
        super();
	}
}

export interface BookmarkFilter {
    like: string;
	isActive: IsActive[];
    types: BookmarkType[];
	ids: Guid[];
	userIds: Guid[];
    userSubQuery: UserLookup;
}
