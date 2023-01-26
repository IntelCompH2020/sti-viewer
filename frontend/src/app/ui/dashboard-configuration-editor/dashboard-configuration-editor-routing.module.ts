import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';
import { DataTreeConfigurationEditorWrapperComponent } from './browse-data-tree-editor/editor-wrapper/data-tree-configuration-editor-wrapper.component';
import { DashboardConfigurationEditorComponent } from './editor/dashboard-configuration-editor.component';
import { DashboardConfigurationListingComponent } from './listing/dashboard-configuration-listing.component';
import { SearchConfigurationEditorComponent } from './search-configuration-editor/search-configuration-editor.component';

const routes: Routes = [
	{
		path: '',
		component: DashboardConfigurationListingComponent,
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.BrowseUserSettings]
			}
		},
	},
	{
		path: 'dashboard/new',
		component: DashboardConfigurationEditorComponent,
		canDeactivate:[PendingChangesGuard],
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditUserSettings]
			}
		},
	},
	{
		path: 'dashboard/:id',
		component: DashboardConfigurationEditorComponent,
		canDeactivate:[PendingChangesGuard],
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditUserSettings]
			}
		},
	},
	{
		path: 'browse-data-tree/new',
		component:	DataTreeConfigurationEditorWrapperComponent,
		canDeactivate:[PendingChangesGuard],
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditUserSettings]
			}
		},
	},
	{
		path: 'browse-data-tree/:id',
		component:	DataTreeConfigurationEditorWrapperComponent,
		canDeactivate:[PendingChangesGuard],
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditUserSettings]
			}
		},
	},
	{
		path: 'search-configuration/new',
		component:	SearchConfigurationEditorComponent,
		canDeactivate:[PendingChangesGuard],
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditUserSettings]
			}
		},
	},
	{
		path: 'search-configuration/:id',
		component:	SearchConfigurationEditorComponent,
		canDeactivate:[PendingChangesGuard],
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditUserSettings]
			}
		},
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class DashboardConfigurationEditorRoutingModule { }
