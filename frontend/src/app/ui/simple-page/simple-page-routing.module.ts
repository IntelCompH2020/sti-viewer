import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { PendingChangesGuard } from '@common/forms/pending-form-changes/pending-form-changes-guard.service';
import { SimplePageComponent } from './simple-page.component';

const routes: Routes = [
	{
		path: ":id",
		canActivate: [AuthGuard],
		component: SimplePageComponent
	},
	{
		path: "**",
		loadChildren: () =>
			import("@common/page-not-found/page-not-found.module").then(
				(m) => m.PageNotFoundModule
			),
	},
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class SimplePageRoutingModule {}
