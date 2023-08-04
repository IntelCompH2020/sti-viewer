import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';


export interface Indicator extends BaseEntity {
  code: string;
  name: string;
  description: string;
  config: IndicatorConfig;
}
export interface IndicatorConfig  {
	accessRequestConfig: AccessRequestConfig;
}

export interface AccessRequestConfig  {
	filterColumns: FilterColumnConfig[];
}

export interface FilterColumnConfig {
  	code: string;
  	dependsOnCode: string;
}


export interface FilterColumnConfig  {
  code: string;
}
export interface IndicatorPersist  {
  code: string;
  name: string;
  description: string;
  config: IndicatorConfigPersist;
}

export interface IndicatorConfigPersist  {
	accessRequestConfig: AccessRequestConfigPersist;
}

export interface AccessRequestConfigPersist {
	filterColumns: FilterColumnConfigPersist[];
}

export interface FilterColumnConfigPersist {
  	code: string;
  	dependsOnCode: string;
}
