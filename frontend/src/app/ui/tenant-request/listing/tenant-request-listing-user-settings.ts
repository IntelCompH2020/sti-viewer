import { Serializable } from '@common/types/json/serializable';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Lookup } from '@common/model/lookup';
import { UserSettingsInformation, UserSettingsLookupBuilder } from '@user-service/core/model/user-settings.model';
import { nameof } from 'ts-simple-nameof';
import { TenantRequest } from '@app/core/model/tenant-request/tenant.request.model';
import { TenantRequestLookup } from '@app/core/query/tenant-request.lookup';

export class TenantRequestListingUserSettings implements Serializable<TenantRequestListingUserSettings>, UserSettingsLookupBuilder<TenantRequestLookup> {

	private like: string;
	private isActive: IsActive[] = [IsActive.Active];
	private order: Lookup.Ordering = { items: [nameof<TenantRequest>(x => x.id)] };
	private project: Lookup.FieldDirectives = {
		fields: [
			nameof<TenantRequest>(x => x.id),
			nameof<TenantRequest>(x => x.message),
			nameof<TenantRequest>(x => x.status),
			nameof<TenantRequest>(x => x.subjectId),
			nameof<TenantRequest>(x => x.email),
			nameof<TenantRequest>(x => x.firstName),
			nameof<TenantRequest>(x => x.lastName)
		]
	};

	static getUserSettingsInformation(): UserSettingsInformation<TenantRequestListingUserSettings> {
		return {
			key: 'TenantRequestListingUserSettings',
			type: TenantRequestListingUserSettings
		};
	}

	public fromJSONObject(item: any): TenantRequestListingUserSettings {
		this.like = item.like;
		this.isActive = item.isActive;
		this.order = item.order;
		this.project = item.project;
		return this;
	}

	public update(lookup: TenantRequestLookup) {
		this.like = lookup.like;
		this.isActive = lookup.isActive;
		this.order = lookup.order;
		this.project = lookup.project;
	}

	public apply(lookup: TenantRequestLookup): TenantRequestLookup {
		lookup.like = this.like;
		lookup.isActive = this.isActive;
		lookup.order = this.order;
		lookup.project = this.project;
		return lookup;
	}
}
