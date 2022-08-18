import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { DataAccessFilterColumn, DataAccessRequest, DataAccessRequestConfig, DataAccessRequestFilterColumnConfig, DataAccessRequestIndicatorConfig, DataAccessRequestIndicatorGroupConfig } from '@app/core/model/data-access-request/data-access-request.model';
import { IndicatorGroup } from '@app/core/model/indicator-group/indicator-group.model';
import { AccessRequestConfig, FilterColumnConfig, Indicator } from '@app/core/model/indicator/indicator.model';
import { DataAccessRequestService } from '@app/core/services/http/data-access-request.service';
import { BaseEditorResolver } from '@common/base/base-editor.resolver';
import { Guid } from '@common/types/guid';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Injectable()
export class DataAccessRequestEditorResolver extends BaseEditorResolver {

	constructor(private dataAccessRequestService: DataAccessRequestService) {
		super();
	}

	public static lookupFields(): string[] {
		return [
			...BaseEditorResolver.lookupFields(),
			nameof<DataAccessRequest>(x => x.user.id),
			nameof<DataAccessRequest>(x => x.status),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.indicator), nameof<Indicator>(x => x.id)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.indicator), nameof<Indicator>(x => x.name)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.indicator), nameof<Indicator>(x => x.code)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.indicator), nameof<Indicator>(x => x.description)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.indicator), nameof<Indicator>(x => x.config), nameof<AccessRequestConfig>(x => x.filterColumns), nameof<FilterColumnConfig>(x => x.code)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.filterColumns), nameof<DataAccessFilterColumn>(x => x.column)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicators), nameof<DataAccessRequestIndicatorConfig>(x => x.filterColumns), nameof<DataAccessFilterColumn>(x => x.values)].join("."),

			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicatorGroups), nameof<DataAccessRequestIndicatorGroupConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.id)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicatorGroups), nameof<DataAccessRequestIndicatorGroupConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.id)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicatorGroups), nameof<DataAccessRequestIndicatorGroupConfig>(x => x.filterColumns), nameof<DataAccessRequestFilterColumnConfig>(x => x.column)].join("."),
			[nameof<DataAccessRequest>(x => x.config), nameof<DataAccessRequestConfig>(x => x.indicatorGroups), nameof<DataAccessRequestIndicatorGroupConfig>(x => x.filterColumns), nameof<DataAccessRequestFilterColumnConfig>(x => x.values)].join("."),
		];
	}
	resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

		const fields = [
			...DataAccessRequestEditorResolver.lookupFields(),
		];
		return this.dataAccessRequestService.getSingle(Guid.parse(route.paramMap.get('id')), fields).pipe(takeUntil(this._destroyed));
	}
}
