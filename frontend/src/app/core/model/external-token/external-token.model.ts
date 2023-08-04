import { IndicatorPointReportLookupFilter } from '@app/core/query/indicator-point-report.lookup';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';
import { Guid } from '@common/types/guid';
import { Tenant } from '../tenant/tenant.model';
import { User } from '../user/user.model';

export interface ExternalToken extends BaseEntity {
	tenant: Tenant;
	owner: User;
	name: User;
	expiresAt: Date;
}

export interface ExternalTokenExpirationPersist extends BaseEntityPersist {
	expiresAt: Date;
}

export interface ExternalTokenChangePersist extends BaseEntityPersist {
}


export interface ExternalTokenCreateResponse{
	expiresAt: Date;
	token: string;
}
export interface IndicatorPointReportExternalTokenPersist {
	expiresAt?: Date;
	name: string;
	lookups: IndicatorPointChartExternalTokenPersist[];
}

export interface IndicatorPointChartExternalTokenPersist{
	indicatorId: Guid;
	chartId: string;
	dashboardId: string;
	lookup: IndicatorPointReportLookupFilter;
}
