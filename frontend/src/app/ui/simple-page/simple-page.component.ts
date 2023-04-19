import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { Dataset } from '@app/core/model/dataset/dataset.model';
import { DatasetService } from '@app/core/services/http/dataset.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { DatasetEditorModel } from '@app/ui/dataset/editor/dataset-editor.model';
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
import { BaseComponent } from '@common/base/base.component';
import { DynamicPageProviderService } from '@app/core/services/ui/dynamic-page.service';
import { DynamicPageContentData } from '@app/core/model/dynamic-page/dynamic-page-content.model';
import { LanguageService } from '@user-service/services/language.service';

@Component({
	selector: "app-simple-page",
	templateUrl: "./simple-page.component.html",
	styleUrls: ["./simple-page.component.scss"],
})
export class SimplePageComponent extends BaseComponent implements OnInit {
	pageData: DynamicPageContentData = null;
	pageId: Guid = null;
	constructor(
		private route: ActivatedRoute,
		public languageService: LanguageService,
		private extraPageService: DynamicPageProviderService
	) {
		super();
	}

	ngOnInit(): void {
		this.route.paramMap
			.pipe(takeUntil(this._destroyed))
			.subscribe((paramMap: ParamMap) => {
				const itemId = paramMap.get("id");
				this.pageId = Guid.parse(itemId);
				this.getItem();
			});
		this.extraPageService
			.getServiceResetObservable()
			.pipe(takeUntil(this._destroyed))
			.subscribe((data) => {
				this.getItem();
			});
	}

	getItem(): void {
		if (this.pageId === null) return;

		this.extraPageService
			.getPageContent(this.pageId)
			.pipe(
				map((data) => data as DynamicPageContentData),
				takeUntil(this._destroyed)
			)
			.subscribe(
				(data) => (this.pageData = data),
				(error) => (this.pageData = null)
			);
	}
}
