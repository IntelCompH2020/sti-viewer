import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';


export interface Dataset extends BaseEntity {
	title: string;
	notes: string;
}

export interface DatasetPersist extends BaseEntityPersist {
	title: string;
	notes: string;
}
