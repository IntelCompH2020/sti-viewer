import { NgModule } from '@angular/core';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { ConfirmationDeleteDialogComponent } from './confirmation-delete-dialog/confirmation-delete-dialog.component';

@NgModule({
    imports: [CommonUiModule],
    declarations: [ConfirmationDialogComponent, ConfirmationDeleteDialogComponent],
    exports: [ConfirmationDialogComponent, ConfirmationDeleteDialogComponent]
})
export class ConfirmationDialogModule {
	constructor() { }
}
