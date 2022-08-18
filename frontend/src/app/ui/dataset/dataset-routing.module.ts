import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { DatasetEditorResolver } from '@app/ui/dataset/editor/dataset-editor.resolver';
import { DatasetEditorComponent } from '@app/ui/dataset/editor/dataset-editor.component';
import { DatasetListingComponent } from '@app/ui/dataset/listing/dataset-listing.component';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';

const routes: Routes = [
	{
		path: '',
		component: DatasetListingComponent,
		canActivate: [AuthGuard]
	},
	{
		path: 'new',
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditDataset]
			}
		},
		component: DatasetEditorComponent,
		canDeactivate: [PendingChangesGuard],
	},
	{
		path: ':id',
		canActivate: [AuthGuard],
		component: DatasetEditorComponent,
		canDeactivate: [PendingChangesGuard],
		resolve: {
			'entity': DatasetEditorResolver
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
	providers: [DatasetEditorResolver]
})
export class DatasetRoutingModule { }
