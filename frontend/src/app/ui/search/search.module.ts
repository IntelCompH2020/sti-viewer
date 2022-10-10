import { NgModule } from '@angular/core';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { SearchRoutingModule } from './search-routing.module';
import { SearchComponent } from './search.component';
import {MatPaginatorModule} from '@angular/material/paginator';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		SearchRoutingModule,
		MatPaginatorModule
	],
	declarations: [
		SearchComponent,
	]
})
export class SearchModule { }
