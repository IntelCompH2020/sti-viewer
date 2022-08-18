import { DataAccessRequestStatus } from '@app/core/enum/data-access-request-status.enum';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';
import { Guid } from '@common/types/guid';
import { IndicatorGroup } from '../indicator-group/indicator-group.model';
import { Indicator } from '../indicator/indicator.model';
import { Tenant } from '../tenant/tenant.model';
import { User } from '../user/user.model';


export interface DataAccessRequest extends BaseEntity {
    user: User;
    status: DataAccessRequestStatus;
    config: DataAccessRequestConfig;
    tenant: Tenant;
}

export interface DataAccessRequestConfig {
    indicators: DataAccessRequestIndicatorConfig[];
    indicatorGroups: DataAccessRequestIndicatorGroupConfig[];
}
export interface DataAccessRequestIndicatorConfig {
    indicator: Indicator;
    filterColumns: DataAccessFilterColumn[];
}
export interface DataAccessFilterColumn {
    column: string;
    values: string[];
}
export interface DataAccessRequestPersist extends BaseEntityPersist {
    user: User;
    status: DataAccessRequestStatus;
    config: DataAccessRequestConfigPersist;
}

export interface DataAccessRequestStatusPersist extends BaseEntityPersist {
    status: DataAccessRequestStatus;
    config: DataAccessRequestConfigPersist;
}
export interface DataAccessRequestConfigPersist {
    indicators: DataAccessRequestIndicatorConfigPersist[];
    indicatorGroups: DataAccessRequestIndicatorGroupConfigPersist[]
}
export interface DataAccessRequestIndicatorConfigPersist {
    id: Guid;
    filterColumns: DataAccessFilterColumnPersist[];
}
export interface DataAccessFilterColumnPersist {
    column: string;
    values: string[];
}

export interface DataAccessRequestIndicatorGroupConfigPersist{
    groupId: Guid;
    filterColumns: DataAccessRequestFilterColumnConfig[];
}

export interface DataAccessRequestIndicatorGroupConfig{
    indicatorGroup: IndicatorGroup;
    filterColumns: DataAccessRequestFilterColumnConfig[];
}

export interface DataAccessRequestFilterColumnConfig{
    column: string;
    values: string[];
}