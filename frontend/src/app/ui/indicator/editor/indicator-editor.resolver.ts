import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AccessRequestConfig, FilterColumnConfig, Indicator, IndicatorConfig } from '@app/core/model/indicator/indicator.model';
import { IndicatorService } from '@app/core/services/http/indicator.service';
import { BaseEditorResolver } from '@common/base/base-editor.resolver';
import { Guid } from '@common/types/guid';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Injectable()
export class IndicatorEditorResolver extends BaseEditorResolver {

  constructor(private indicatorService: IndicatorService) {
    super();
  }

  public static lookupFields(): string[] {
    return [
      ...BaseEditorResolver.lookupFields(),
      nameof<Indicator>(x => x.code),
      nameof<Indicator>(x => x.name),
      nameof<Indicator>(x => x.description),
      nameof<Indicator>(x => x.config) + '.'  + nameof<IndicatorConfig>(x => x.accessRequestConfig) + '.' + nameof<AccessRequestConfig>(x => x.filterColumns) + '.' + nameof<FilterColumnConfig>(x => x.code)
    ]
  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    const fields = [
      ...IndicatorEditorResolver.lookupFields(),
    ];
    return this.indicatorService.getSingle(Guid.parse(route.paramMap.get('id')), fields).pipe(takeUntil(this._destroyed));
  }
}
