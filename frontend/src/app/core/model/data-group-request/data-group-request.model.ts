import { DataGroupRequestStatus } from "@app/core/enum/data-group-request-status.enum";
import { IsActive } from "@app/core/enum/is-active.enum";
import { Guid } from "@common/types/guid";
import { Moment } from "moment";
import { IndicatorGroup } from "../indicator-group/indicator-group.model";
import { Tenant } from "../tenant/tenant.model";
import { User } from "../user/user.model";

export interface DataGroupRequest{
    id: Guid;
    tenant: Tenant;
    groupHash: string;
    name: string;
    status: DataGroupRequestStatus;
    user: User;
    hash: string;
    isActive: IsActive;
    createAt: Moment;
    updatedAt: Moment;
    config: DataGroupRequestConfig;
}

export interface DataGroupRequestConfig{
    indicatorGroup: IndicatorGroup;
    groupColumns: DataGroupColumn[];
}

export interface DataGroupColumn{
    fieldCode: string;
    values: string[];
}

// * PERSIST

export interface DataGroupRequestPersist{
    id?: Guid;
    name: string;
    status: DataGroupRequestStatus;
    config: DataGroupRequestConfigPersist;
    hash?: string;
}

export interface DataGroupRequestConfigPersist{
    indicatorGroupId: Guid;
    groupColumns: DataGroupColumnPersist[];
}

export interface DataGroupColumnPersist{
    fieldCode: string;
    values: string[];
}