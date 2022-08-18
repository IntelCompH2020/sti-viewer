import { Dataset } from '@app/core/model/dataset/dataset.model';
import { Guid } from '@common/types/guid';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';

export interface DetailItem extends BaseEntity {
	title: string;
	decimal: number;
	dateTime: Date;
	time: Date;
	date: Date;
	dataset: Dataset;
}

export interface DetailItemPersist extends BaseEntityPersist {
	title: string;
	decimal: number;
	dateTime: Date;
	time: Date;
	date: Date;
	datasetId: Guid;
}
