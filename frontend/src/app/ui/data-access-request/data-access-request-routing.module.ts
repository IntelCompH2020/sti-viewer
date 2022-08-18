import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';
import { DataAccessRequestEditorComponent } from './editor/data-access-request-editor.component';
import { DataAccessRequestEditorResolver } from './editor/data-access-request-editor.resolver';
import { DataAccessRequestListingComponent } from './listing/data-access-request-listing.component';

const routes: Routes = [
	{
		path: '',
		component: DataAccessRequestListingComponent,
		canActivate: [AuthGuard]
	},
	{
		path: 'new',
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditDataAccessRequest]
			}
		},
		component: DataAccessRequestEditorComponent,
		canDeactivate: [PendingChangesGuard],
	},
	{
		path: ':id',
		canActivate: [AuthGuard],
		component: DataAccessRequestEditorComponent,
		canDeactivate: [PendingChangesGuard],
		resolve: {
			'entity': DataAccessRequestEditorResolver
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
	providers: [DataAccessRequestEditorResolver]
})
export class DataAccessRequestRoutingModule { }
