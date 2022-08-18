import { NgModule } from '@angular/core';
import { DownloadReportRoutingModule } from '@app/ui/download-report/download-report-routing.module';
import { DownloadReportComponent } from '@app/ui/download-report/download-report.component';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { AutoCompleteModule } from '@common/modules/auto-complete/auto-complete.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { CommonUiModule } from '@common/ui/common-ui.module';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		ConfirmationDialogModule,
		ListingModule,
		TextFilterModule,
		DownloadReportRoutingModule,
		AutoCompleteModule,
		UserSettingsModule
	],
	declarations: [
		DownloadReportComponent
	]
})
export class DownloadReportModule { }
