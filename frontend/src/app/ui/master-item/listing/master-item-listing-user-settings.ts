import { Serializable } from '@common/types/json/serializable';
import { IsActive } from '@app/core/enum/is-active.enum';
import { MasterItem } from '@app/core/model/master-item/master-item.model';
import { MasterItemLookup } from '@app/core/query/master-item.lookup';
import { Lookup } from '@common/model/lookup';
import { UserSettingsInformation, UserSettingsLookupBuilder } from '@user-service/core/model/user-settings.model';
import { nameof } from 'ts-simple-nameof';

export class MasterItemListingUserSettings implements Serializable<MasterItemListingUserSettings>, UserSettingsLookupBuilder<MasterItemLookup> {

	like: string;
	isActive: IsActive[] = [IsActive.Active];
	order: Lookup.Ordering = { items: [nameof<MasterItem>(x => x.title)] };
	project: Lookup.FieldDirectives = {
		fields: [
			nameof<MasterItem>(x => x.id),
			nameof<MasterItem>(x => x.title),
			nameof<MasterItem>(x => x.notes),
			nameof<MasterItem>(x => x.createdAt)
		]
	};

	static getUserSettingsInformation(): UserSettingsInformation<MasterItemListingUserSettings> {
		return {
			key: 'MasterItemListingUserSettings',
			type: MasterItemListingUserSettings
		};
	}

	public fromJSONObject(item: any): MasterItemListingUserSettings {
		this.like = item.like;
		this.isActive = item.isActive;
		this.order = item.order;
		this.project = item.project;
		return this;
	}

	update(lookup: MasterItemLookup) {
		this.like = lookup.like;
		this.isActive = lookup.isActive;
		this.order = lookup.order;
		this.project = lookup.project;
	}

	apply(lookup: MasterItemLookup): MasterItemLookup {
		lookup.like = this.like;
		lookup.isActive = this.isActive;
		lookup.order = this.order;
		lookup.project = this.project;
		return lookup;
	}
}
