import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Dataset } from '@app/core/model/dataset/dataset.model';
import { DatasetService } from '@app/core/services/http/dataset.service';
import { BaseEditorResolver } from '@common/base/base-editor.resolver';
import { Guid } from '@common/types/guid';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Injectable()
export class DatasetEditorResolver extends BaseEditorResolver{

  constructor(private datasetService: DatasetService) {
    super();
  }

  public static lookupFields(): string[]{
    return [
			...BaseEditorResolver.lookupFields(),
			nameof<Dataset>(x => x.title),
			nameof<Dataset>(x => x.notes),
		]
  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    const fields = [
			...DatasetEditorResolver.lookupFields(),
		];
    return this.datasetService.getSingle(Guid.parse(route.paramMap.get('id')), fields ).pipe(takeUntil(this._destroyed));
  }
}
