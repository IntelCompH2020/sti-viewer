import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { TenantRequest } from '@app/core/model/tenant-request/tenant.request.model';
import { TenantRequestService } from '@app/core/services/http/tenant-request.service';
import { BaseEditorResolver } from '@common/base/base-editor.resolver';
import { Guid } from '@common/types/guid';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Injectable()
export class TenantRequestEditorResolver extends BaseEditorResolver {

  constructor(private tenantRequestService: TenantRequestService) {
	super();
  }

  public static lookupFields(): string[] {
	return [
		...BaseEditorResolver.lookupFields(),
		nameof<TenantRequest>(x => x.message),
		nameof<TenantRequest>(x => x.status),
		nameof<TenantRequest>(x => x.forUser.subjectId),
		nameof<TenantRequest>(x => x.forUser.id),
		nameof<TenantRequest>(x => x.email),
		nameof<TenantRequest>(x => x.hash),
		nameof<TenantRequest>(x => x.forUser.firstName),
		nameof<TenantRequest>(x => x.forUser.lastName)
	];
  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

	const fields = [
		...TenantRequestEditorResolver.lookupFields(),
	];
	return this.tenantRequestService.getSingle(Guid.parse(route.paramMap.get('id')), fields).pipe(takeUntil(this._destroyed));
  }
}
