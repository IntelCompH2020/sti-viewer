import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmbeddedDashboardComponent } from './embedded-dashboard.component';

const routes: Routes = [
	{
		path: '',
		component: EmbeddedDashboardComponent,
	}
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class EmbeddedDashboardRoutingModule { }
