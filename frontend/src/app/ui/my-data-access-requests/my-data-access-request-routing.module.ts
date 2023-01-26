import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';
import { MyDataAccessRequestResolver } from './my-data-access-request.resolver';
import { MyDataAccessRequestComponent } from './my-data-access-requests.component';
import { PortfolioAccessRequestComponent } from './portfolio-access-request/portfolio-access-request.component';

const routes: Routes = [
	{
		path: '',
		canActivate: [AuthGuard],
		resolve:{
			[MyDataAccessRequestResolver.RESOLVER_KEY] : MyDataAccessRequestResolver
		},
		children:[
			{
				path: '',
				component: MyDataAccessRequestComponent,

			},
			{
				path: 'request',
				component: PortfolioAccessRequestComponent,
				canDeactivate: [PendingChangesGuard],
			},
		]
		
	},
	
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class MyDataAccessRequestRoutingModule { }
