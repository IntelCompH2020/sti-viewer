import { NgModule } from '@angular/core';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { MyDataAccessRequestRoutingModule } from './my-data-access-request-routing.module';
import { IndicatorGroupService } from '@app/core/services/http/indicator-group.service';
import { MyIndicatorColumnsEditorComponent } from './my-indicator-columns-editor/my-indicator-columns-editor.component';
import { MyDataAccessRequestComponent } from './my-data-access-requests.component';
import { GroupGeneratorComponent } from './group-generator/group-generator.component';
import { DataGroupRequestService } from '@app/core/services/http/data-group-request.service';
import { PortfolioAccessRequestComponent } from './portfolio-access-request/portfolio-access-request.component';
import { NewColumnRequesDialogComponent } from './portfolio-access-request/new-column-request-dialog/new-column-request-dialog.component';
import { ColumnsOrderedPipe } from './pipes/columns-ordered.pipe';
import { MyDataAccessRequestResolver } from './my-data-access-request.resolver';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		MyDataAccessRequestRoutingModule,
	],
	declarations: [
		MyIndicatorColumnsEditorComponent,
		MyDataAccessRequestComponent,
		GroupGeneratorComponent,
		PortfolioAccessRequestComponent,
		NewColumnRequesDialogComponent,
		ColumnsOrderedPipe
	],
	providers:[
		IndicatorGroupService,
		DataGroupRequestService,
		MyDataAccessRequestResolver
	]
})
export class MyDataAccessRequestModule { }
