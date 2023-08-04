import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { IndicatorPointReportExternalTokenPersist } from "@app/core/model/external-token/external-token.model";
import { ExternalTokenService } from "@app/core/services/http/external-token.service";
import { BaseComponent } from "@common/base/base.component";
import { delayWhen, filter, map, takeUntil } from 'rxjs/operators';
import { IndicatorQueryParams } from "../indicator-dashboard.component";
import { QueryParamsService } from "@app/core/services/ui/query-params.service";
import { InstallationConfigurationService } from "@common/installation-configuration/installation-configuration.service";

@Component({
    templateUrl: './share-dialog.component.html'
})
export class ShareDialogComponent extends BaseComponent implements OnInit{
    formGroup: FormGroup
	url: string = null;

	constructor(
		@Inject(MAT_DIALOG_DATA) private data: ShareDialogData,
		private dialogRef: MatDialogRef<ShareDialogComponent>,
		private installationConfigurationService: InstallationConfigurationService,
		private queryParamsService: QueryParamsService,
		private externalTokenService: ExternalTokenService,
		private formBuilder: FormBuilder
	) {
		super();
		this.url = null;

		const expiresAt = new Date();
		expiresAt.setDate(expiresAt.getDate() + this.installationConfigurationService.sharedExpirationInDays);
		this.formGroup = this.formBuilder.group({
			name: [{ value: this.data.config.name, disabled: false }, [Validators.required]],
			lookups: [{ value: this.data.config.lookups, disabled: false }],
            expiresAt:  [{ value: this.data.config.expiresAt ? this.data.config.expiresAt : expiresAt, disabled: false },  [Validators.required]],
        })
    }
	ngOnInit(): void {

    }


    close(): void{
        this.dialogRef.close()
    }

    cancel(): void{
        this.dialogRef.close(false);
	}

	openUrl() {
		window.open(this.url, "_blank");
	}

	share(): void{
        const value = this.formGroup.value;
		if (this.data.chartId) {
			this.shareGraph(value);
		} else {
			this.shareDashboard(value);
		}
    }

	private shareGraph(config: IndicatorPointReportExternalTokenPersist): void {
		this.externalTokenService.persistIndicatorPointReport(config)
			.pipe(
				takeUntil(this._destroyed)
			)
			.subscribe(response => {
				const parmasString = this.queryParamsService.serializeObject<IndicatorQueryParams>(this.data.indicatorQueryParams);
				this.url = location.origin + this.installationConfigurationService.sharedGraphPath + "?token=" + encodeURIComponent(response.token) + "&dashboardId=" + this.data.indicatorQueryParams.dashboard + "&chartId=" + this.data.chartId + "&params=" + encodeURIComponent(parmasString);
			});
	}

	private shareDashboard(config: IndicatorPointReportExternalTokenPersist): void {
		this.externalTokenService.persistIndicatorPointReport(config)
			.pipe(
				takeUntil(this._destroyed)
			)
			.subscribe(response => {
				const parmasString = this.queryParamsService.serializeObject<IndicatorQueryParams>(this.data.indicatorQueryParams);
				this.url = location.origin + this.installationConfigurationService.sharedDashboardPath + "?token=" + encodeURIComponent(response.token) + "&dashboardId=" + this.data.indicatorQueryParams.dashboard + "&params=" + encodeURIComponent(parmasString);
			});
	}

}

export class ShareDialogData{
	config: IndicatorPointReportExternalTokenPersist;
	indicatorQueryParams: IndicatorQueryParams;
	chartId?: string;
}
