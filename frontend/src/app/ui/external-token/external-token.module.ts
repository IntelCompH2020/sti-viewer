import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { ExternalTokenRoutingModule } from './external-token-routing.module';
import { ExternalTokenListingFiltersComponent } from './listing/filters/external-token-listing-filters.component';
import { ExternalTokenListingComponent } from './listing/external-token-listing.component';
import { TokenExpirationDialogComponent } from './token-expiration-dialog/token-expiration-dialog.component';
import { MatDatetimepickerModule } from '@mat-datetimepicker/core';
import { TokenChangeDialogComponent } from './token-change-dialog/token-change-dialog.component';
import { ClipboardModule } from '@angular/cdk/clipboard';



@NgModule({
    imports: [
        CommonUiModule,
        CommonFormsModule,
        ConfirmationDialogModule,
        ListingModule,
        TextFilterModule,
        FormattingModule,
        ExternalTokenRoutingModule,
        EditorActionsModule,
		MatDatetimepickerModule,
		ClipboardModule,
        UserSettingsModule
    ],
    declarations: [
        ExternalTokenListingComponent,
		ExternalTokenListingFiltersComponent,
		TokenExpirationDialogComponent,
		TokenChangeDialogComponent
    ]
})
export class ExternalTokenModule { }
