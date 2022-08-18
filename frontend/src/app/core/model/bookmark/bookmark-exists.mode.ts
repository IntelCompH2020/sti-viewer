import { BookmarkType } from "@app/core/enum/bookmark-type.enum";

export interface BookmarkExistParams{
    value: string;
    type: BookmarkType
}