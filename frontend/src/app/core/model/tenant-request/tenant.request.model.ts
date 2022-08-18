import { TenantRequestStatus } from '@app/core/enum/teanant-request.enum';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';
import { User } from '../user/user.model';

export interface TenantRequest extends BaseEntity {
	message: string;
	status: TenantRequestStatus;
	email: string;
	forUser: User;
}

export interface TenantRequestPersist extends BaseEntityPersist {
	message: string;
	status: TenantRequestStatus;
	email: string;
}

export interface TenantRequestStatusPersist extends BaseEntityPersist {
	status: TenantRequestStatus;
	tenantName?: string;
	tenantCode?: string;
}
