import { animate, query, stagger, style, transition, trigger } from '@angular/animations';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { LanguageType } from '@app/core/enum/language-type.enum';
import { ThemeType } from '@app/core/enum/theme-type.enum';
import { AuthService } from '@app/core/services/ui/auth.service';
import { ThemingService } from '@app/core/services/ui/theming.service';
import { BaseComponent } from '@common/base/base.component';
import { InAppNotificationListingDialogComponent } from '@notification-service/ui/inapp-notification/listing-dialog/inapp-notification-listing-dialog.component';
import { UserService } from '@user-service/services/http/user.service';
import { LanguageService } from '@user-service/services/language.service';

@Component({
  selector: 'app-side-navigation',
  templateUrl: './side-navigation.component.html',
  styleUrls: ['./side-navigation.component.scss'],
  animations:[
	trigger('navigationListAnimation', [
        transition('* => true', [
          query('.navigation-description', [
            style({
                opacity:0
            }),
            stagger(40, [
                animate('500ms ease-out', style({opacity:1}))
            ])
          ], { optional: true})
        ]),
    ])
  ]
})
export class SideNavigationComponent extends BaseComponent implements OnInit {
  progressIndication = false;
	themeTypes = ThemeType;
	languageTypes = LanguageType;
	inAppNotificationDialog: MatDialogRef<InAppNotificationListingDialogComponent> = null;
	inAppNotificationCount = 0;
	@Input()
	expanded = true;

	@Output()
	onNavigate  = new EventEmitter<void>();

	constructor(
		public authService: AuthService,
		public router: Router,
		private route: ActivatedRoute,
		public themingService: ThemingService,
		public languageService: LanguageService,
		private userService: UserService
	) {
		super();
	}

	ngOnInit() {

	}

	public isAuthenticated(): boolean {
		return this.authService.currentAccountIsAuthenticated();
	}

	handleNavigation():void{
		this.onNavigate.emit();
	}

}
