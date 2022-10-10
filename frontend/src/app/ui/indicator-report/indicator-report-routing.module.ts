import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { IndicatorReportComponent } from './indicator-report.component';

const routes: Routes = [
	{
		path: '',
		component: IndicatorReportComponent,
		canActivate: [AuthGuard]
	},
	{
		path: 'dashboard',
		data: {
			authContext: {
				permissions: [AppPermission.ViewIndicatorReportsDashboardPage]
			}
		},
		loadChildren: () => import('@app/ui/indicator-dashboard/indicator-dashboard.module').then(m => m.IndicatorDashboardModule)
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class IndicatorReportRoutingModule { }
