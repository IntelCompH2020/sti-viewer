import { Injectable } from '@angular/core';
import { BrowseDataTreeConfigModel, BrowseDataTreeLevelConfigModel } from '@app/core/model/data-tree/browse-data-tree-config.model';
import { BrowseDataTreeLevelModel } from '@app/core/model/data-tree/browse-data-tree-level.model';
import { FieldModel } from '@app/core/model/indicator-config/indicator-report-level-config';
import { IndicatorReportLevelLookup } from '@app/core/query/indicator-report-level-lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Observable, of} from 'rxjs';
import { nameof } from 'ts-simple-nameof';

@Injectable({
  providedIn: 'root'
})
export class DataTreeService {
  private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/data-tree-config`; }

  constructor(private http: BaseHttpService,
    private installationConfiguration: InstallationConfigurationService) { }

  getMyConfigs(): Observable<BrowseDataTreeConfigModel[]> {
    const url = `${this.apiBase}/my-configs`;


    const f = [
        nameof<BrowseDataTreeConfigModel>(x => x.id),
        nameof<BrowseDataTreeConfigModel>(x => x.name),
        [nameof<BrowseDataTreeConfigModel>(x => x.levelConfigs), nameof<BrowseDataTreeLevelConfigModel>(x => x.field), nameof<FieldModel>(x => x.code)].join('.'),
        [nameof<BrowseDataTreeConfigModel>(x => x.levelConfigs), nameof<BrowseDataTreeLevelConfigModel>(x => x.field), nameof<FieldModel>(x => x.name)].join('.'),
        [nameof<BrowseDataTreeConfigModel>(x => x.levelConfigs), nameof<BrowseDataTreeLevelConfigModel>(x => x.supportSubLevel)].join('.'),
        [nameof<BrowseDataTreeConfigModel>(x => x.levelConfigs), nameof<BrowseDataTreeLevelConfigModel>(x => x.supportedDashboards)].join('.'),
    ]
    return this.http.get(url, {params: {
        f
    }});
}

queryLevel(lookup: IndicatorReportLevelLookup): Observable<QueryResult<BrowseDataTreeLevelModel>>{
      const url = `${this.apiBase}/query-level`;
      
      return this.http.post(url, lookup);
  }

}
