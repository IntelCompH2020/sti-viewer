import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { DetailItemEditorComponent } from '@app/ui/master-item/editor/details-editor/detail-items-editor.component';
import { MasterItemEditorComponent } from '@app/ui/master-item/editor/master-item-editor.component';
import { MasterItemListingFiltersComponent } from '@app/ui/master-item/listing/filters/master-item-listing-filters.component';
import { MasterItemListingComponent } from '@app/ui/master-item/listing/master-item-listing.component';
import { MasterItemRoutingModule } from '@app/ui/master-item/master-item-routing.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { AutoCompleteModule } from '@common/modules/auto-complete/auto-complete.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { TimePickerModule } from '@common/modules/time-picker/time-picker.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { MatDatetimepickerModule } from '@mat-datetimepicker/core';
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
		MasterItemRoutingModule,
		TimePickerModule,
		AutoCompleteModule,
		MatDatetimepickerModule,
		EditorActionsModule,
		UserSettingsModule
	],
	declarations: [
		MasterItemListingComponent,
		MasterItemEditorComponent,
		MasterItemListingFiltersComponent,
		DetailItemEditorComponent
	]
})
export class MasterItemModule { }
