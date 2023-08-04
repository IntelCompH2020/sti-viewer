import { NgModule } from '@angular/core';
import { MatBadgeModule } from '@angular/material/badge';
import { MatCardModule } from '@angular/material/card';
import { MatTreeModule } from '@angular/material/tree';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { IndicatorDashboardModule } from '../indicator-dashboard/indicator-dashboard.module';
import { EmbeddedDashboardRoutingModule } from './embedded-dashboard-routing.module';
import { EmbeddedDashboardComponent } from './embedded-dashboard.component';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		FormattingModule,
		EmbeddedDashboardRoutingModule,
		EditorActionsModule,
		MatCardModule,
		UserSettingsModule,
		MatBadgeModule,
		MatTreeModule,
		IndicatorDashboardModule
	],
	declarations: [
		EmbeddedDashboardComponent,
	]
})
export class EmbeddedDashboardModule { }
