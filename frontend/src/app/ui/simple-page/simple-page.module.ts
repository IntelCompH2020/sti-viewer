import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { SimplePageComponent } from './simple-page.component';
import { SimplePageRoutingModule } from './simple-page-routing.module';
import { PipeService } from '@common/formatting/pipe.service';
import { SafeHtmlPipe } from '@app/core/formatting/pipes/safe-html.pipe';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		FormattingModule,
		SimplePageRoutingModule,
		EditorActionsModule,
		UserSettingsModule,
	],
	declarations: [SimplePageComponent],
})
export class SimplePageModule {}
