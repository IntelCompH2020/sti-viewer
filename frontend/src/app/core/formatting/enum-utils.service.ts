import { Injectable } from '@angular/core';
import { BlueprintRequestState } from '@app/core/enum/blueprint-request-state.enum';
import { BlueprintTemplateKey } from '@app/core/enum/blueprint-template-key.enum';
import { BlueprintTemplateType } from '@app/core/enum/blueprint-template-type.enum';
import { IsActive } from '@app/core/enum/is-active.enum';
import { LanguageType } from '@app/core/enum/language-type.enum';
import { RoleType } from '@app/core/enum/role-type.enum';
import { BaseEnumUtilsService } from '@common/base/base-enum-utils.service';
import { TranslateService } from '@ngx-translate/core';
import { DynamicPageType } from '../enum/dynamic-page-type.enum';
import { DynamicPageVisibility } from '../enum/dynamic-page-visibility.enum';

@Injectable()
export class AppEnumUtils extends BaseEnumUtilsService {
	constructor(private language: TranslateService) { super(); }

	public toRoleTypeString(value: RoleType): string {
		switch (value) {
			case RoleType.Admin: return this.language.instant('APP.TYPES.APP-ROLE.ADMIN');
			case RoleType.User: return this.language.instant('APP.TYPES.APP-ROLE.USER');
			case RoleType.DatasetAdmin: return this.language.instant('APP.TYPES.APP-ROLE.DATASET-ADMIN');
			case RoleType.DatasetViewer: return this.language.instant('APP.TYPES.APP-ROLE.DATASET-VIEWER');
			case RoleType.AccessAdmin: return this.language.instant('APP.TYPES.APP-ROLE.ACCESS-ADMIN');
			case RoleType.AccessViewer: return this.language.instant('APP.TYPES.APP-ROLE.ACCESS-VIEWER');
			default: return '';
		}
	}

	public toIsActiveString(value: IsActive): string {
		switch (value) {
			case IsActive.Active: return this.language.instant('APP.TYPES.IS-ACTIVE.ACTIVE');
			case IsActive.Inactive: return this.language.instant('APP.TYPES.IS-ACTIVE.INACTIVE');
			default: return '';
		}
	}

	public toPageVisibilityString(value: DynamicPageVisibility): string {
		switch (value) {
			case DynamicPageVisibility.Authenticated: return this.language.instant("APP.TYPES.PAGE-VISIBILITY.AUTHENTICATED");
			case DynamicPageVisibility.HasRole: return this.language.instant("APP.TYPES.PAGE-VISIBILITY.HAS-ROLE");
			case DynamicPageVisibility.Owner: return this.language.instant("APP.TYPES.PAGE-VISIBILITY.OWNER");
			case DynamicPageVisibility.Hidden: return this.language.instant("APP.TYPES.PAGE-VISIBILITY.HIDDEN");
			default: return '';
		}
	}

	public toPageTypeString(value: DynamicPageType): string {
		switch (value) {
			case DynamicPageType.External: return this.language.instant("APP.TYPES.PAGE-TYPE.EXTERNAL");
			case DynamicPageType.Simple: return this.language.instant("APP.TYPES.PAGE-TYPE.SIMPLE");
			default: return '';
		}
	}

	public toLanguageTypeString(value: LanguageType): string {
		switch (value) {
			case LanguageType.English: return this.language.instant('APP.TYPES.LANGUAGE-TYPE.ENGLISH');
			case LanguageType.Greek: return this.language.instant('APP.TYPES.LANGUAGE-TYPE.GREEK');
			default: return '';
		}
	}

	public toBlueprintRequestStateString(value: BlueprintRequestState): string {
		switch (value) {
			case BlueprintRequestState.Completed: return this.language.instant('APP.TYPES.BLUEPRINT-REQUEST-STATE.COMPLETED');
			case BlueprintRequestState.Error: return this.language.instant('APP.TYPES.BLUEPRINT-REQUEST-STATE.ERROR');
			case BlueprintRequestState.Send: return this.language.instant('APP.TYPES.BLUEPRINT-REQUEST-STATE.SEND');
			default: return '';
		}
	}

	public toBlueprintTemplateTypeString(value: BlueprintTemplateType): string {
		switch (value) {
			case BlueprintTemplateType.Docx: return this.language.instant('APP.TYPES.BLUEPRINT-TEMPLATE-TYPE.DOCX');
			case BlueprintTemplateType.Xlsx: return this.language.instant('APP.TYPES.BLUEPRINT-TEMPLATE-TYPE.XLSX');
			case BlueprintTemplateType.Pptx: return this.language.instant('APP.TYPES.BLUEPRINT-TEMPLATE-TYPE.PPTX');
			default: return '';
		}
	}

	public toBlueprintTemplateKeyString(value: BlueprintTemplateKey): string {
		switch (value) {
			case BlueprintTemplateKey.DatasetListingReport: return this.language.instant('APP.TYPES.BLUEPRINT-TEMPLATE-KEY.DATASET-LISTING');
			default: return '';
		}
	}
}
