import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
	selector: 'app-confirmation-dialog',
	templateUrl: './confirmation-delete-dialog.component.html',
	styleUrls: ['./confirmation-delete-dialog.component.scss']
})
export class ConfirmationDeleteDialogComponent {

	constructor(
		public dialogRef: MatDialogRef<ConfirmationDeleteDialogComponent>,
	) {
	}

	cancel() {
		this.dialogRef.close(false);
	}

	confirm() {
		this.dialogRef.close(true);
	}
}
