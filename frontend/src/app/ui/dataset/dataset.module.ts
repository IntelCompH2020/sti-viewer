import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { DatasetRoutingModule } from '@app/ui/dataset/dataset-routing.module';
import { DatasetEditorComponent } from '@app/ui/dataset/editor/dataset-editor.component';
import { DatasetListingComponent } from '@app/ui/dataset/listing/dataset-listing.component';
import { DatasetListingFiltersComponent } from '@app/ui/dataset/listing/filters/dataset-listing-filters.component';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		FormattingModule,
		DatasetRoutingModule,
		EditorActionsModule,
		UserSettingsModule
	],
	declarations: [
		DatasetListingComponent,
		DatasetEditorComponent,
		DatasetListingFiltersComponent
	]
})
export class DatasetModule { }
