import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Dataset } from '@app/core/model/dataset/dataset.model';
import { DetailItem } from '@app/core/model/master-item/detail-item.model';
import { MasterItem } from '@app/core/model/master-item/master-item.model';
import { MasterItemService } from '@app/core/services/http/master-item.service';
import { BaseEditorResolver } from '@common/base/base-editor.resolver';
import { Guid } from '@common/types/guid';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Injectable()
export class MasterItemEditorResolver extends BaseEditorResolver{

  constructor(private masterItemService: MasterItemService) {
    super();
  }

  public static lookupFields(): string[]{
    return [
			...BaseEditorResolver.lookupFields(),
			nameof<MasterItem>(x => x.title),
			nameof<MasterItem>(x => x.notes),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.id),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.title),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.decimal),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.dateTime),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.time),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.date),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.dataset) + '.' + nameof<Dataset>(x => x.id),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.dataset) + '.' + nameof<Dataset>(x => x.title),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.isActive),
			nameof<MasterItem>(x => x.details) + '.' + nameof<DetailItem>(x => x.hash)
		]
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    const fields = [
        ...MasterItemEditorResolver.lookupFields()
		];
    return this.masterItemService.getSingle(Guid.parse(route.paramMap.get('id')), fields ).pipe(takeUntil(this._destroyed));
  }
}
