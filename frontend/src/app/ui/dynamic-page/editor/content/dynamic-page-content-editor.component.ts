import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { DynamicPage } from "@app/core/model/dynamic-page/dynamic-page.model";
import { DynamicPageService } from "@app/core/services/http/dynamic-page.service";
import { AuthService } from '@app/core/services/ui/auth.service';
import { DynamicPageEditorModel } from "@app/ui/dynamic-page/editor/dynamic-page-editor.model";
import { FormService } from '@common/forms/form-service';
import { LoggingService } from '@common/logging/logging-service';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { Guid } from '@common/types/guid';
import { TranslateService } from '@ngx-translate/core';
import { map, takeUntil } from 'rxjs/operators';
import { BaseEditor } from '@common/base/base-editor';
import { FilterService } from '@common/modules/text-filter/filter-service';
import { DatePipe } from '@angular/common';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { DynamicPageEditorResolver } from "@app/ui/dynamic-page/editor/dynamic-page-editor.resolver";
import { LanguageType } from '@app/core/enum/language-type.enum';
import { LanguageService } from '@user-service/services/language.service';
import { DynamicPageVisibility } from '@app/core/enum/dynamic-page-visibility.enum';
import { DynamicPageType } from '@app/core/enum/dynamic-page-type.enum';
import { BaseComponent } from '@common/base/base.component';

@Component({
	selector: "app-dynamic-page-content-editor",
	templateUrl: "./dynamic-page-content-editor.component.html",
	styleUrls: ["./dynamic-page-content-editor.component.scss"],
})
export class DynamicPageContentEditorComponent
	extends BaseComponent
	implements OnInit
{
	@Input() contentFormGroup = null;
	@Input() pageType = DynamicPageType.Simple;
	languageTypeValues: Array<LanguageType>;
	dynamicPageType = DynamicPageType;

	constructor(
		// BaseFormEditor injected dependencies
		protected dialog: MatDialog,
		public languageService: LanguageService,
		protected language: TranslateService,
		protected formService: FormService,
		protected router: Router,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected filterService: FilterService,
		protected route: ActivatedRoute,
		protected queryParamsService: QueryParamsService,
		// Rest dependencies. Inject any other needed deps here:
		public enumUtils: AppEnumUtils,
	) {
		super( );
	}

	ngOnInit(): void {
		this.languageTypeValues = this.enumUtils.getEnumValues(LanguageType);
	}
}
