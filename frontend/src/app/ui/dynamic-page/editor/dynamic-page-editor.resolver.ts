import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { DynamicPageContent } from '@app/core/model/dynamic-page/dynamic-page-content.model';
import { DynamicPage, DynamicPageConfig } from "@app/core/model/dynamic-page/dynamic-page.model";
import { DynamicPageService } from "@app/core/services/http/dynamic-page.service";
import { BaseEditorResolver } from '@common/base/base-editor.resolver';
import { Guid } from '@common/types/guid';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Injectable()
export class DynamicPageEditorResolver extends BaseEditorResolver{

  constructor(private dynamicPageService: DynamicPageService) {
    super();
  }

  public static lookupFields(): string[]{
    return [
		...BaseEditorResolver.lookupFields(),
		nameof<DynamicPage>((x) => x.creator),
		nameof<DynamicPage>((x) => x.type),
		nameof<DynamicPage>((x) => x.visibility),
		nameof<DynamicPage>((x) => x.defaultLanguage),
		nameof<DynamicPage>((x) => x.config) + '.' + nameof<DynamicPageConfig>((x) => x.allowedRoles),
		nameof<DynamicPage>((x) => x.config) + '.' + nameof<DynamicPageConfig>((x) => x.externalUrl),
		nameof<DynamicPage>((x) => x.config) + '.' + nameof<DynamicPageConfig>((x) => x.matIcon),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.content),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.createdAt),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.hash),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.id),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.isActive),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.language),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.page),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.title),
		nameof<DynamicPage>((x) => x.pageContents) + '.' + nameof<DynamicPageContent>((x) => x.updatedAt),
		nameof<DynamicPage>((x) => x.order),
	];
  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    const fields = [
			...DynamicPageEditorResolver.lookupFields(),
		];
    return this.dynamicPageService.getSingle(Guid.parse(route.paramMap.get('id')), fields ).pipe(takeUntil(this._destroyed));
  }
}
