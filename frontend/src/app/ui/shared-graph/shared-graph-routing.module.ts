import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedGraphComponent } from './shared-graph.component';

const routes: Routes = [
	{
		path: '',
		component: SharedGraphComponent,
	}
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class SharedGraphRoutingModule { }
