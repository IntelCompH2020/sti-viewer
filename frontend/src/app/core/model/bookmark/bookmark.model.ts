import { BookmarkType } from "@app/core/enum/bookmark-type.enum";
import { IsActive } from "@app/core/enum/is-active.enum";
import { Guid } from "@common/types/guid";
import { Moment } from "moment";
import { User } from "../user/user.model";

export interface Bookmark{
    id: Guid;
    user: User;
    name: string;
    value: string;
    isActive: IsActive;
    type: BookmarkType;
    createdAt?: Moment;
    updatedAt?: Moment;
}


export interface MyBookmarkPersist{
    id?: Guid;
    name: string;
    value: string;
    type: BookmarkType; // TODO SPECIAL FILE
    hash?: string;
}