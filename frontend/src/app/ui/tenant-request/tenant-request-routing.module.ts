import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';
import { TenantRequestEditorComponent } from './editor/tenant-request-editor.component';
import { TenantRequestEditorResolver } from './editor/tenant-request-editor.resolver';
import { TenantRequestListingComponent } from './listing/tenant-request-listing.component';


const routes: Routes = [
	{
		path: '',
		component: TenantRequestListingComponent,
		canActivate: [AuthGuard]
	},
	{
		path: 'new',
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.CreateTenantRequest]
			}
		},
		component: TenantRequestEditorComponent,
		canDeactivate: [PendingChangesGuard],
	},
	{
		path: ':id',
		canActivate: [AuthGuard],
		component: TenantRequestEditorComponent,
		canDeactivate: [PendingChangesGuard],
		resolve: {
			'entity': TenantRequestEditorResolver
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
	providers: [TenantRequestEditorResolver]
})
export class TenantRequestRoutingModule { }
