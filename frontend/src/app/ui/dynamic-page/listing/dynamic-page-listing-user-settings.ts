import { Serializable } from '@common/types/json/serializable';
import { IsActive } from '@app/core/enum/is-active.enum';
import { DynamicPage } from "@app/core/model/dynamic-page/dynamic-page.model";
import { DynamicPageLookup } from "@app/core/query/dynamic-page.lookup";
import { Lookup } from '@common/model/lookup';
import { UserSettingsInformation, UserSettingsLookupBuilder } from '@user-service/core/model/user-settings.model';
import { nameof } from 'ts-simple-nameof';
import { DynamicPageContent } from '@app/core/model/dynamic-page/dynamic-page-content.model';

export class DynamicPageListingUserSettings implements Serializable<DynamicPageListingUserSettings>, UserSettingsLookupBuilder<DynamicPageLookup> {

	private like: string;
	private isActive: IsActive[] = [IsActive.Active];
	private order: Lookup.Ordering = { items: [nameof<DynamicPage>(x => x.createdAt)] };
	private project: Lookup.FieldDirectives = {
		fields: [
			nameof<DynamicPage>(x => x.id),
			nameof<DynamicPage>((x) => x.pageContents) + "." + nameof<DynamicPageContent>((x) => x.title),
			nameof<DynamicPage>(x => x.createdAt)
		]
	};

	static getUserSettingsInformation(): UserSettingsInformation<DynamicPageListingUserSettings> {
		return {
			key: 'DynamicPageListingUserSettings',
			type: DynamicPageListingUserSettings
		};
	}

	public fromJSONObject(item: any): DynamicPageListingUserSettings {
		this.like = item.like;
		this.isActive = item.isActive;
		this.order = item.order;
		this.project = item.project;
		return this;
	}

	public update(lookup: DynamicPageLookup) {
		this.like = lookup.like;
		this.isActive = lookup.isActive;
		this.order = lookup.order;
		this.project = lookup.project;
	}

	public apply(lookup: DynamicPageLookup): DynamicPageLookup {
		lookup.like = this.like;
		lookup.isActive = this.isActive;
		lookup.order = this.order;
		lookup.project = this.project;
		return lookup;
	}
}
