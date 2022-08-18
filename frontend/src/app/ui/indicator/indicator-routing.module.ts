import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';
import { IndicatorEditorComponent } from './editor/indicator-editor.component';
import { IndicatorEditorResolver } from './editor/indicator-editor.resolver';
import { IndicatorListingComponent } from './listing/indicator-listing.component';

const routes: Routes = [
	{
		path: '',
		component: IndicatorListingComponent,
		canActivate: [AuthGuard]
	},
	{
		path: 'new',
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditIndicator]
			}
		},
		component: IndicatorEditorComponent,
		canDeactivate: [PendingChangesGuard],
	},
	{
		path: ':id',
		canActivate: [AuthGuard],
		component: IndicatorEditorComponent,
		canDeactivate: [PendingChangesGuard],
		resolve: {
			'entity': IndicatorEditorResolver
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
	providers: [IndicatorEditorResolver]
})
export class IndicatorRoutingModule { }
