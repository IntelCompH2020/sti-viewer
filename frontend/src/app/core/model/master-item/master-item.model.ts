import { DetailItem, DetailItemPersist } from '@app/core/model/master-item/detail-item.model';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';

export interface MasterItem extends BaseEntity  {
	title: string;
	notes: string;
	details: DetailItem[];
}

export interface MasterItemPersist extends BaseEntityPersist {
	title: string;
	notes: string;
	details: DetailItemPersist[];
}
