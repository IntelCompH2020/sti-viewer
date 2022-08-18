import { NgModule } from '@angular/core';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ConfirmationDialogModule } from '@common/modules/confirmation-dialog/confirmation-dialog.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { IdpServiceFormattingModule } from '@idp-service/core/formatting/formatting.module';


import { UserProfileNotifierListModule } from '@notification-service/ui/user-profile/notifier-list/user-profile-notifier-list-editor.module';
import { UserServiceFormattingModule } from '@user-service/core/formatting/formatting.module';

import { AngularCropperjsModule } from 'angular-cropperjs';
import { UserProfileContactInfoEditorComponent } from './contact-info/contact-info-editor.component';
import { UserProfilePersonalInfoEditorComponent } from './personal/personal-info-editor.component';
import { UserProfileEditorComponent } from './profile/user-profile-editor.component';
import { UserProfileRoutingModule } from './user-profile-routing.module';
import { UserProfileComponent } from './user-profile.component';

@NgModule({
	imports: [
		UserProfileRoutingModule,
		CommonUiModule,
		CommonFormsModule,
		UserServiceFormattingModule,
		IdpServiceFormattingModule,
		UserProfileNotifierListModule,
		ConfirmationDialogModule,
		AngularCropperjsModule,
	],
	declarations: [
		UserProfileEditorComponent,
		UserProfileComponent,
		UserProfilePersonalInfoEditorComponent,
		UserProfileContactInfoEditorComponent
	]
})
export class UserProfileModule { }
