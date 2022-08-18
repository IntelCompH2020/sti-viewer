import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { IndicatorReportRoutingModule } from './indicator-report-routing.module';
import { IndicatorReportComponent } from './indicator-report.component';
import { MatCardModule } from '@angular/material/card';
import { IndicatorDetailsComponent } from './indicator-details/indicator-details.component';
import {MatTreeModule} from '@angular/material/tree';
@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		FormattingModule,
		IndicatorReportRoutingModule,
		EditorActionsModule,
		MatCardModule,
		UserSettingsModule,
		MatTreeModule
	],
	declarations: [
		IndicatorReportComponent,
  IndicatorDetailsComponent
	]
})
export class IndicatorReportModule { }
