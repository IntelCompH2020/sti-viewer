import { HttpEvent, HttpHandler, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { BaseInterceptor } from '@common/http/interceptors/base.interceptor';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { Observable } from 'rxjs';

@Injectable()
export class ApiKeyInterceptor extends BaseInterceptor {

	constructor(
		public installationConfiguration: InstallationConfigurationService,
		private authService: AuthService) { super(installationConfiguration); }

	get type(): InterceptorType { return InterceptorType.ApiKeyInterceptor; }

	interceptRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		if (!this.authService.getApiKeyHeaderName() || !this.authService.getApiKey()) { return next.handle(req); }

		req = req.clone({
			headers: req.headers.set(this.authService.getApiKeyHeaderName(), this.authService.getApiKey())
		});
		return next.handle(req);
	}
}
