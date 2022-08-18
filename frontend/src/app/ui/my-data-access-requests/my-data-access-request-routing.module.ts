import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { MyDataAccessRequestComponent } from './my-data-access-requests.component';

const routes: Routes = [
	{
		path: '',
		component: MyDataAccessRequestComponent,
		canActivate: [AuthGuard]
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class MyDataAccessRequestRoutingModule { }
