import { BlueprintTemplateType } from '@app/core/enum/blueprint-template-type.enum';
import { Guid } from '@common/types/guid';
import { IsActive } from '@idp-service/core/enum/is-active.enum';

export interface BlueprintTemplate {
	id: Guid;
	fileRef: string;
	templateKey: Guid;
	name: string;
	extension: string;
	fullName: string;
	language: string;
	templateType: BlueprintTemplateType;
	isActive: IsActive;
	createdAt: Date;
	updatedAt: Date;
	hash: string;
}

export interface BlueprintTemplatePersist {
	id?: Guid;
	templateType: BlueprintTemplateType;
	language: string;
	hash?: string;
	templateKey: Guid;
	fileRef: string;
	name: string;
	extension: string;
}
