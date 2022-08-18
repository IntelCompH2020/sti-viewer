import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { IndicatorRoutingModule } from './indicator-routing.module';
import { IndicatorListingComponent } from './listing/indicator-listing.component';
import { IndicatorEditorComponent } from './editor/indicator-editor.component';
import { IndicatorListingFiltersComponent } from './listing/filters/indicator-listing-filters.component';
import { AccessRequestConfigComponent } from './editor/access-request-config/access-request-config.component';


@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		FormattingModule,
		IndicatorRoutingModule,
		EditorActionsModule,
		UserSettingsModule
	],
	declarations: [
		IndicatorListingComponent,
		IndicatorEditorComponent,
		IndicatorListingFiltersComponent,
		AccessRequestConfigComponent
	]
})
export class IndicatorModule { }
