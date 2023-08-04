import { Serializable } from '@common/types/json/serializable';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { UserSettingsInformation, UserSettingsLookupBuilder } from '@user-service/core/model/user-settings.model';
import { nameof } from 'ts-simple-nameof';
import { ExternalTokenLookup } from '@app/core/query/external-token.lookup';
import { ExternalToken } from '@app/core/model/external-token/external-token.model';

export class ExternalTokenListingUserSettings implements Serializable<ExternalTokenListingUserSettings>, UserSettingsLookupBuilder<ExternalTokenLookup> {

	private like: string;
	private isActive: IsActive[] = [IsActive.Active];
	private order: Lookup.Ordering = { items: [nameof<ExternalToken>(x => x.id)] };
	private project: Lookup.FieldDirectives = {
		fields: [
			nameof<ExternalToken>(x => x.id),
			nameof<ExternalToken>(x => x.name),
			nameof<ExternalToken>(x => x.expiresAt),
			nameof<ExternalToken>(x => x.createdAt),
		]
	};

	static getUserSettingsInformation(): UserSettingsInformation<ExternalTokenListingUserSettings> {
		return {
			key: 'ExternalTokenListingUserSettings',
			type: ExternalTokenListingUserSettings
		};
	}

	public fromJSONObject(item: any): ExternalTokenListingUserSettings {
		this.like = item.like;
		this.isActive = item.isActive;
		this.order = item.order;
		this.project = item.project;
		return this;
	}

	public update(lookup: ExternalTokenLookup) {
		this.like = lookup.like;
		this.isActive = lookup.isActive;
		this.order = lookup.order;
		this.project = lookup.project;
	}

	public apply(lookup: ExternalTokenLookup): ExternalTokenLookup {
		lookup.like = this.like;
		lookup.isActive = this.isActive;
		lookup.order = this.order;
		lookup.project = this.project;
		return lookup;
	}
}
