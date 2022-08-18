import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { DataAccessRequestRoutingModule } from './data-access-request-routing.module';
import { DataAccessRequestEditorComponent } from './editor/data-access-request-editor.component';
import { DataAccessRequestListingComponent } from './listing/data-access-request-listing.component';
import { DataAccessRequestListingFiltersComponent } from './listing/filters/data-access-request-listing-filters.component';
import { AutoCompleteModule } from '@common/modules/auto-complete/auto-complete.module';

import { IndicatorColumnConfigComponent } from './editor/indicator-config/indicator-column-config/indicator-column-config.component';
import { IndicatorConfigComponent } from './editor/indicator-config/indicator-config.component';
import { IndicatorGroupService } from '@app/core/services/http/indicator-group.service';
import { IndicatorColumnsEditorComponent } from './editor/indicator-columns-editor/indicator-columns-editor.component';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		FormattingModule,
		DataAccessRequestRoutingModule,
		EditorActionsModule,
		UserSettingsModule,
		AutoCompleteModule
	],
	declarations: [
		DataAccessRequestListingComponent,
		DataAccessRequestEditorComponent,
		DataAccessRequestListingFiltersComponent,
		IndicatorConfigComponent,
		IndicatorColumnConfigComponent,
		IndicatorColumnsEditorComponent
	],
	providers:[
		IndicatorGroupService
	]
})
export class DataAccessRequestModule { }
