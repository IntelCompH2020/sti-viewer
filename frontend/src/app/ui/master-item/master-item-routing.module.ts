import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { MasterItemEditorResolver } from '@app/ui/master-item/editor/master-item-editor.resolver';
import { MasterItemEditorComponent } from '@app/ui/master-item/editor/master-item-editor.component';
import { MasterItemListingComponent } from '@app/ui/master-item/listing/master-item-listing.component';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';

const routes: Routes = [
	{
		path: '',
		component: MasterItemListingComponent,
		canActivate: [AuthGuard]
	},
	{
		path: 'new',
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditMasterItem]
			}
		},
		component: MasterItemEditorComponent,
		canDeactivate: [PendingChangesGuard],
	},
	{
		path: ':id',
		canActivate: [AuthGuard],
		component: MasterItemEditorComponent,
		canDeactivate: [PendingChangesGuard],
		resolve: {
			'entity': MasterItemEditorResolver
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
	providers: [MasterItemEditorResolver]
})
export class MasterItemRoutingModule { }
