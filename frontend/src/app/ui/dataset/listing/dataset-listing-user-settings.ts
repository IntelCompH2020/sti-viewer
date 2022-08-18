import { Serializable } from '@common/types/json/serializable';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Dataset } from '@app/core/model/dataset/dataset.model';
import { DatasetLookup } from '@app/core/query/dataset.lookup';
import { Lookup } from '@common/model/lookup';
import { UserSettingsInformation, UserSettingsLookupBuilder } from '@user-service/core/model/user-settings.model';
import { nameof } from 'ts-simple-nameof';

export class DatasetListingUserSettings implements Serializable<DatasetListingUserSettings>, UserSettingsLookupBuilder<DatasetLookup> {

	private like: string;
	private isActive: IsActive[] = [IsActive.Active];
	private order: Lookup.Ordering = { items: [nameof<Dataset>(x => x.title)] };
	private project: Lookup.FieldDirectives = {
		fields: [
			nameof<Dataset>(x => x.id),
			nameof<Dataset>(x => x.title),
			nameof<Dataset>(x => x.notes),
			nameof<Dataset>(x => x.createdAt)
		]
	};

	static getUserSettingsInformation(): UserSettingsInformation<DatasetListingUserSettings> {
		return {
			key: 'DatasetListingUserSettings',
			type: DatasetListingUserSettings
		};
	}

	public fromJSONObject(item: any): DatasetListingUserSettings {
		this.like = item.like;
		this.isActive = item.isActive;
		this.order = item.order;
		this.project = item.project;
		return this;
	}

	public update(lookup: DatasetLookup) {
		this.like = lookup.like;
		this.isActive = lookup.isActive;
		this.order = lookup.order;
		this.project = lookup.project;
	}

	public apply(lookup: DatasetLookup): DatasetLookup {
		lookup.like = this.like;
		lookup.isActive = this.isActive;
		lookup.order = this.order;
		lookup.project = this.project;
		return lookup;
	}
}
