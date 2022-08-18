import { NgModule } from '@angular/core';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { EditorActionsModule } from '@app/ui/editor-actions/editor-actions.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { TenantRequestRoutingModule } from './tenant-request-routing.module';
import { TenantRequestEditorComponent } from './editor/tenant-request-editor.component';
import { TenantRequestListingFiltersComponent } from './listing/filters/tenant-request-listing-filters.component';
import { TenantRequestListingComponent } from './listing/tenant-request-listing.component';
import { ApproveDialogComponent } from './editor/approve-dialog/approve-dialog.component';



@NgModule({
    imports: [
        CommonUiModule,
        CommonFormsModule,
        ConfirmationDialogModule,
        ListingModule,
        TextFilterModule,
        FormattingModule,
        TenantRequestRoutingModule,
        EditorActionsModule,
        UserSettingsModule
    ],
    declarations: [
        TenantRequestListingComponent,
        TenantRequestEditorComponent,
        TenantRequestListingFiltersComponent,
        ApproveDialogComponent
    ]
})
export class TenantRequestModule { }
