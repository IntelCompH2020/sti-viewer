import { Serializable } from '@common/types/json/serializable';
import { DatasetLookup } from '@app/core/query/dataset.lookup';
import { Lookup } from '@common/model/lookup';
import { UserSettingsInformation, UserSettingsLookupBuilder } from '@user-service/core/model/user-settings.model';
import { nameof } from 'ts-simple-nameof';
import { DataAccessRequest } from '@app/core/model/data-access-request/data-access-request.model';

export class DataAccessRequestListingUserSettings implements Serializable<DataAccessRequestListingUserSettings>, UserSettingsLookupBuilder<DatasetLookup> {

	private order: Lookup.Ordering = { items: [nameof<DataAccessRequest>(x => x.createdAt)] };
	private project: Lookup.FieldDirectives = {
		fields: [
			nameof<DataAccessRequest>(x => x.id),
			nameof<DataAccessRequest>(x => x.createdAt)
		]
	};

	static getUserSettingsInformation(): UserSettingsInformation<DataAccessRequestListingUserSettings> {
		return {
			key: 'DataAccessRequestListingUserSettings',
			type: DataAccessRequestListingUserSettings
		};
	}

	public fromJSONObject(item: any): DataAccessRequestListingUserSettings {
		this.order = item.order;
		this.project = item.project;
		return this;
	}

	public update(lookup: DatasetLookup) {
		this.order = lookup.order;
		this.project = lookup.project;
	}

	public apply(lookup: DatasetLookup): DatasetLookup {
		lookup.order = this.order;
		lookup.project = this.project;
		return lookup;
	}
}
