import { HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpProgressEvent, HttpRequest, HttpResponse, HttpSentEvent, HttpUserEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@app/core/services/ui/auth.service';
import { BaseInterceptor } from '@common/http/interceptors/base.interceptor';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { from, Observable, throwError } from 'rxjs';
import { catchError, mergeMap } from 'rxjs/operators';

@Injectable()
export class UnauthorizedResponseInterceptor extends BaseInterceptor {

	constructor(
		public installationConfiguration: InstallationConfigurationService,
		public router: Router,
		private authService: AuthService) { super(installationConfiguration); }

	get type(): InterceptorType { return InterceptorType.UnauthorizedResponse; }

	private accountRefresh$: Observable<boolean> = null;

	interceptRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {
		return next.handle(req).pipe(
			catchError(error => {
				if (error instanceof HttpErrorResponse) {
					switch ((<HttpErrorResponse>error).status) {
						case 401:
							return this.handle401Error(req, next);
						default:
							return throwError(error);
					}
				} else {
					return throwError(error);
				}
			}));
	}

	private handle401Error(req: HttpRequest<any>, next: HttpHandler) {
		if (!this.accountRefresh$) {
			this.accountRefresh$ = from(this.authService.refreshToken().then(isRefreshed => {
				if (!isRefreshed) {
					this.logoutUser();
					return false;
				}
			}));
		}
		return this.accountRefresh$.pipe(mergeMap(account => this.repeatRequest(req, next)));
	}

	private repeatRequest(originalRequest: HttpRequest<any>, next: HttpHandler) {
		const newAuthenticationToken: String = this.authService.currentAuthenticationToken();
		const newRequest = originalRequest.clone({
			setHeaders: {
				Authorization: `Bearer ${newAuthenticationToken}`
			}
		});
		return next.handle(newRequest);
	}

	private logoutUser() {
		this.authService.clear();
		if (!this.isLoginRoute() && !this.isSignupRoute()) { this.router.navigate(['/unauthorized']); }
	}

	private isLoginRoute(): boolean {
		return this.router.isActive('login', false);
	}

	private isSignupRoute(): boolean {
		return this.router.isActive('signup-register', false) || this.router.isActive('signup-invitation', false);
	}
}
