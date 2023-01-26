import { Serializable } from '@common/types/json/serializable';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { UserSettingsInformation, UserSettingsLookupBuilder } from '@user-service/core/model/user-settings.model';
import { nameof } from 'ts-simple-nameof';
import { IndicatorLookup } from '@app/core/query/indicator.lookup';
import { Indicator } from '@app/core/model/indicator/indicator.model';

export class IndicatorListingUserSettings implements Serializable<IndicatorListingUserSettings>, UserSettingsLookupBuilder<IndicatorLookup> {

	private like: string;
	private isActive: IsActive[] = [IsActive.Active];
	private order: Lookup.Ordering = { items: [nameof<Indicator>(x => x.name)] };
	private project: Lookup.FieldDirectives = {

		// todo 
		fields: [
			nameof<Indicator>(x => x.id),
			nameof<Indicator>(x => x.code),
			nameof<Indicator>(x => x.name),
			nameof<Indicator>(x => x.description)
		]
	};

	static getUserSettingsInformation(): UserSettingsInformation<IndicatorListingUserSettings> {
		return {
			key: 'IndicatorListingUserSettings',
			type: IndicatorListingUserSettings
		};
	}

	public fromJSONObject(item: any): IndicatorListingUserSettings {
		this.like = item.like;
		this.isActive = item.isActive;
		this.order = item.order;
		this.project = item.project;
		return this;
	}

	public update(lookup: IndicatorLookup) {
		this.like = lookup.like;
		this.isActive = lookup.isActive;
		this.order = lookup.order;
		this.project = lookup.project;
	}

	public apply(lookup: IndicatorLookup): IndicatorLookup {
		lookup.like = this.like;
		lookup.isActive = this.isActive;
		lookup.order = this.order;
		lookup.project = this.project;
		return lookup;
	}
}
