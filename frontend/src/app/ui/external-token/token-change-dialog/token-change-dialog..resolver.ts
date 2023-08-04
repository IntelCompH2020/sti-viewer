import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { ExternalToken } from '@app/core/model/external-token/external-token.model';
import { AccessRequestConfig, FilterColumnConfig, Indicator, IndicatorConfig } from '@app/core/model/indicator/indicator.model';
import { IndicatorService } from '@app/core/services/http/indicator.service';
import { BaseEditorResolver } from '@common/base/base-editor.resolver';
import { Guid } from '@common/types/guid';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Injectable()
export class TokenChangeDialogResolver extends BaseEditorResolver {

  constructor(private indicatorService: IndicatorService) {
    super();
  }

  public static lookupFields(): string[] {
    return [
      ...BaseEditorResolver.lookupFields(),
      nameof<ExternalToken>(x => x.name),
    ]
  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    const fields = [
      ...TokenChangeDialogResolver.lookupFields(),
    ];
    return this.indicatorService.getSingle(Guid.parse(route.paramMap.get('id')), fields).pipe(takeUntil(this._destroyed));
  }
}
