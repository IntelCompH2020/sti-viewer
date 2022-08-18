import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { IndicatorDashboardComponent } from './indicator-dashboard.component';

const routes: Routes = [
	{
		path: '',
		component: IndicatorDashboardComponent,
		canActivate: [AuthGuard]
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class IndicatorDashboardRoutingModule { }
