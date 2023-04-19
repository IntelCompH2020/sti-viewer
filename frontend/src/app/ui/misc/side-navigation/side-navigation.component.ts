import { animate, query, stagger, style, transition, trigger } from '@angular/animations';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { LanguageType } from '@app/core/enum/language-type.enum';
import { DynamicPageType } from '@app/core/enum/dynamic-page-type.enum';
import { ThemeType } from '@app/core/enum/theme-type.enum';
import { DynamicPageMenuItem } from '@app/core/model/dynamic-page/dynamic-page.model';
import { AuthService } from '@app/core/services/ui/auth.service';
import { DynamicPageProviderService } from '@app/core/services/ui/dynamic-page.service';
import { ThemingService } from '@app/core/services/ui/theming.service';
import { BaseComponent } from '@common/base/base.component';
import { Guid } from '@common/types/guid';
import { InAppNotificationListingDialogComponent } from '@notification-service/ui/inapp-notification/listing-dialog/inapp-notification-listing-dialog.component';
import { UserService } from '@user-service/services/http/user.service';
import { LanguageService } from '@user-service/services/language.service';
import { takeUntil } from "rxjs/operators";

@Component({
	selector: "app-side-navigation",
	templateUrl: "./side-navigation.component.html",
	styleUrls: ["./side-navigation.component.scss"],
	animations: [
		trigger("navigationListAnimation", [
			transition("* => true", [
				query(
					".navigation-description",
					[
						style({
							opacity: 0,
						}),
						stagger(40, [
							animate("500ms ease-out", style({ opacity: 1 })),
						]),
					],
					{ optional: true }
				),
			]),
		]),
	],
})
export class SideNavigationComponent extends BaseComponent implements OnInit {
	progressIndication = false;
	themeTypes = ThemeType;
	languageTypes = LanguageType;
	dynamicMenuItems: DynamicPageMenuItem[];
	pageTypeEnumValues = DynamicPageType;
	inAppNotificationDialog: MatDialogRef<InAppNotificationListingDialogComponent> =
		null;
	inAppNotificationCount = 0;
	@Input()
	expanded = true;

	@Output()
	onNavigate = new EventEmitter<void>();

	get hasTenant(): boolean {
		return !!this.authService.selectedTenant();
	}

	constructor(
		public authService: AuthService,
		public router: Router,
		private route: ActivatedRoute,
		public themingService: ThemingService,
		public languageService: LanguageService,
		private userService: UserService,
		private dynamicPageProviderService: DynamicPageProviderService
	) {
		super();
	}

	ngOnInit() {
		this.dynamicPageProviderService
			.getAllowedPageMenuItems()
			.pipe(takeUntil(this._destroyed))
			.subscribe((menuItems) => {
				this.dynamicMenuItems = menuItems;
			});
		this.dynamicPageProviderService
			.getMenuItemsChangedObservable()
			.pipe(takeUntil(this._destroyed))
			.subscribe((menuItems) => {
				this.dynamicMenuItems = menuItems;
			});
	}

	public isAuthenticated(): boolean {
		return this.authService.currentAccountIsAuthenticated();
	}

	handleNavigation(): void {
		this.onNavigate.emit();
	}

	getSimplePageUrl(page: DynamicPageMenuItem) {
		return "simple-page/" + page.id;
	}
	goToExternalLink(page: DynamicPageMenuItem): void {
		window.open(page.externalUrl, "_blank");
	}
}
