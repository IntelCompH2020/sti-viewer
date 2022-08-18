import { NgModule } from '@angular/core';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { MyDataAccessRequestRoutingModule } from './my-data-access-request-routing.module';
import { IndicatorGroupService } from '@app/core/services/http/indicator-group.service';
import { MyIndicatorColumnsEditorComponent } from './my-indicator-columns-editor/my-indicator-columns-editor.component';
import { MyDataAccessRequestComponent } from './my-data-access-requests.component';
import { GroupGeneratorComponent } from './group-generator/group-generator.component';
import { DataGroupRequestService } from '@app/core/services/http/data-group-request.service';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		MyDataAccessRequestRoutingModule,
	],
	declarations: [
		MyIndicatorColumnsEditorComponent,
		MyDataAccessRequestComponent,
		GroupGeneratorComponent
	],
	providers:[
		IndicatorGroupService,
		DataGroupRequestService
	]
})
export class MyDataAccessRequestModule { }
