import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseComponent } from '@common/base/base.component';
import { FormService } from '@common/forms/form-service';
@Component({
	selector: 'app-approve-dialog.component',
	templateUrl: './approve-dialog.component.html',
	styleUrls: ['./approve-dialog.component.scss']
})

export class ApproveDialogComponent extends BaseComponent implements OnInit, AfterViewInit {
	formGroup: UntypedFormGroup = null;
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();
	public showInfoMessage = false;

	constructor(
		public dialogRef: MatDialogRef<ApproveDialogComponent>,
		private formService: FormService,
		private changeDetectorRef: ChangeDetectorRef
	) {
		super();
	}
	ngAfterViewInit() {
		this.changeDetectorRef.detectChanges();
	}
	ngOnInit(): void {
		this.formGroup = this.formBuilder.group({
			tenantName: ['', [Validators.required]],
			tenantCode: ['', [Validators.required]],
		});
	}

	formSubmit(): void {
		this.formService.touchAllFormFields(this.formGroup);
		if (!this.isFormValid()) { this.showInfoMessage = false; return; }

		this.dialogRef.close(this.formGroup.value);
	}

	public isFormValid(): Boolean {
		return this.formGroup.valid;
	}

	public cancel(): void {
		this.dialogRef.close(null);
	}

	public save() {
		this.clearErrorModel();
	}

	clearErrorModel() {
		this.formService.validateAllFormFields(this.formGroup);
	}
}
