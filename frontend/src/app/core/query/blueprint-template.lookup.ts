import { BlueprintTemplateKey } from '@app/core/enum/blueprint-template-key.enum';
import { BlueprintTemplateType } from '@app/core/enum/blueprint-template-type.enum';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';

export class BlueprintTemplateLookup extends Lookup implements BlueprintTemplateFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	templateTypes: BlueprintTemplateType[];
	userIds: Guid[];
	languages: string[];
	templateKeyIds: BlueprintTemplateKey[];
	includeDefault: boolean;

	constructor() {
		super();
	}
}

export interface BlueprintTemplateFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	templateTypes: BlueprintTemplateType[];
	userIds: Guid[];
	languages: string[];
	templateKeyIds: BlueprintTemplateKey[];
	includeDefault: boolean;
}
