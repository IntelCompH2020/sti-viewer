import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';


export interface Indicator extends BaseEntity {
  code: string;
  name: string;
  description: string;
  config: AccessRequestConfig;
}
export interface AccessRequestConfig extends BaseEntity {
  filterColumns: FilterColumnConfig[];
}

export interface FilterColumnConfig extends BaseEntity {
  code: string;
}
export interface IndicatorPersist extends BaseEntityPersist {
  code: string;
  name: string;
  description: string;
  config: AccessRequestConfigPersist;
}

export interface AccessRequestConfigPersist extends BaseEntityPersist {
  filterColumns: FilterColumnConfigPersist[];
}

export interface FilterColumnConfigPersist extends BaseEntityPersist {
  code: string;
}