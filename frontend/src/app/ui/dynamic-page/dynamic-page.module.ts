import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { DynamicPageRoutingModule } from "@app/ui/dynamic-page/dynamic-page-routing.module";
import { DynamicPageEditorComponent } from "@app/ui/dynamic-page/editor/dynamic-page-editor.component";
import { DynamicPageListingComponent } from "@app/ui/dynamic-page/listing/dynamic-page-listing.component";
import { DynamicPageListingFiltersComponent } from "@app/ui/dynamic-page/listing/filters/dynamic-page-listing-filters.component";
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { DynamicPageContentEditorComponent } from './editor/content/dynamic-page-content-editor.component';
import { EditorModule, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		FormattingModule,
		DynamicPageRoutingModule,
		EditorActionsModule,
		UserSettingsModule,
		EditorModule,
	],
	declarations: [
		DynamicPageListingComponent,
		DynamicPageEditorComponent,
		DynamicPageListingFiltersComponent,
		DynamicPageContentEditorComponent
	],
	providers: [
	  { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }
	]
})
export class DynamicPageModule { }
