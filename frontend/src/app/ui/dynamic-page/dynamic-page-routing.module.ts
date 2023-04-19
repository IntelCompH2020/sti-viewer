import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { DynamicPageEditorResolver } from '@app/ui/dynamic-page/editor/dynamic-page-editor.resolver';
import { DynamicPageEditorComponent } from '@app/ui/dynamic-page/editor/dynamic-page-editor.component';
import { DynamicPageListingComponent } from '@app/ui/dynamic-page/listing/dynamic-page-listing.component';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';

const routes: Routes = [
	{
		path: '',
		component: DynamicPageListingComponent,
		canActivate: [AuthGuard]
	},
	{
		path: 'new',
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditDynamicPage]
			}
		},
		component: DynamicPageEditorComponent,
		canDeactivate: [PendingChangesGuard],
	},
	{
		path: ':id',
		canActivate: [AuthGuard],
		component: DynamicPageEditorComponent,
		canDeactivate: [PendingChangesGuard],
		resolve: {
			'entity': DynamicPageEditorResolver
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
	providers: [DynamicPageEditorResolver]
})
export class DynamicPageRoutingModule { }
