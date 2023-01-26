import { Injectable } from '@angular/core';
import { FilterColumn, IndicatorGroup } from '@app/core/model/indicator-group/indicator-group.model';
import { Indicator } from '@app/core/model/indicator/indicator.model';
import { DataField, PortofolioColumnConfig, PortofolioColumnDashboardOverride, PortofolioColumnDashboardOverrideFieldRequirement, PortofolioConfig } from '@app/core/model/portofolio-config/portofolio-config.model';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { Observable, of} from 'rxjs';
import { nameof } from 'ts-simple-nameof';

@Injectable({
  providedIn: 'root'
})
export class PortofolioConfigService {
  	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/portofolio-config`; }

  	constructor(private http: BaseHttpService,
	private installationConfiguration: InstallationConfigurationService) { }

  	getMyConfigs(): Observable<PortofolioConfig[]> {
		const url = `${this.apiBase}/my-configs`;

		return this.http.get(url, {params: {
			f: this.allFields
		}});
	}


	getMyConfigByKey(configKey: string): Observable<PortofolioConfig>{

		if(!configKey){

			console.warn('no config key provided for portfolio')
			return of(null);
		}

		const url = `${this.apiBase}/my-config/${configKey}`;

		return this.http.get(url, {params: {
			f: this.allFields
		}});


	}







	private allFields = [
		nameof<PortofolioConfig>(x => x.defaultDashboards),

		nameof<PortofolioConfig>(x => x.indicatorGroup),
		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.code)].join('.'),
		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.name)].join('.'),
		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.id)].join('.'),

		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.indicators)].join('.'),
		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.id)].join('.'),
		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.name)].join('.'),
		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.code)].join('.'),

		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.filterColumns)].join('.'),
		[nameof<PortofolioConfig>(x => x.indicatorGroup), nameof<IndicatorGroup>(x => x.filterColumns), nameof<FilterColumn>(x => x.code)].join('.'),


		nameof<PortofolioConfig>(x => x.code),
		nameof<PortofolioConfig>(x => x.name),
		nameof<PortofolioConfig>(x => x.columns),
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.dashboardOverrides)].join('.'),


		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.dashboardOverrides), nameof<PortofolioColumnDashboardOverride>(x => x.requirements)].join('.'),
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.dashboardOverrides), nameof<PortofolioColumnDashboardOverride>(x => x.requirements), nameof<PortofolioColumnDashboardOverrideFieldRequirement>(x => x.value)].join('.'),
		
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.dashboardOverrides), nameof<PortofolioColumnDashboardOverride>(x => x.supportedDashboards)].join('.'),
		
		
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.defaultDashboards)].join('.'),
		
		
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.field)].join('.'),
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.field), nameof<DataField>(x => x.code)].join('.'),
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.field), nameof<DataField>(x => x.name)].join('.'),
		
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.order)].join('.'),
		[nameof<PortofolioConfig>(x => x.columns), nameof<PortofolioColumnConfig>(x => x.major)].join('.'),

	];
}
