import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { AuthService } from '@app/core/services/ui/auth.service';
import { BaseComponent } from '@common/base/base.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { UiNotificationService } from '@common/modules/notification/ui-notification-service';

@Component({
	selector: 'app-embedded-dashboard',
	templateUrl: './embedded-dashboard.component.html',
	styleUrls: ['./embedded-dashboard.component.scss'],
	animations: GENERAL_ANIMATIONS
})
export class EmbeddedDashboardComponent extends BaseComponent implements OnInit {
	hasApiKey = false;

	constructor(
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		private activaterRoute: ActivatedRoute,
		protected authService: AuthService

	) {
		super();
	}
	ngOnInit(): void {
		this.activaterRoute.queryParamMap.subscribe(x => {
			const apiKey = x.get('apikey');
			const tenant = x.get('tenant');
			this.hasApiKey = false;
			if (apiKey && apiKey.length > 0) {
				this.authService.selectedTenant(tenant);
				this.authService.setApiKey('x-sti-api-key', apiKey);
				this.hasApiKey = true;
			}
		});
	}

}
