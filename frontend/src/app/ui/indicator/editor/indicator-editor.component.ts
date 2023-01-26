import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { AuthService } from '@app/core/services/ui/auth.service';
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
import { IndicatorEditorModel } from './indicator-editor.model';
import { Indicator } from '@app/core/model/indicator/indicator.model';
import { IndicatorService } from '@app/core/services/http/indicator.service';
import { IndicatorEditorResolver } from './indicator-editor.resolver';
import { IndicatorElasticLookup } from '@app/core/query/indicator-elastic.lookup';
import { nameof } from 'ts-simple-nameof';
import { Field, IndicatorElastic, Schema } from '@app/core/model/indicator-elastic/indicator-elastic';
import { IndicatorElasticBaseType } from '@app/core/enum/indicator-elastic-base-type.enum';
import { I } from '@angular/cdk/keycodes';

@Component({
	selector: 'app-indicator-editor',
	templateUrl: './indicator-editor.component.html',
	styleUrls: ['./indicator-editor.component.scss']
})
export class IndicatorEditorComponent extends BaseEditor<IndicatorEditorModel, Indicator> implements OnInit {

	isDeleted = false;
	saveClicked = false;
	formGroup: UntypedFormGroup = null;
	ITEMS_PER_PAGE: number;


	constructor(
		// BaseFormEditor injected dependencies
		protected dialog: MatDialog,
		protected language: TranslateService,
		protected formService: FormService,
		protected router: Router,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected filterService: FilterService,
		protected datePipe: DatePipe,
		protected route: ActivatedRoute,
		protected queryParamsService: QueryParamsService,
		// Rest dependencies. Inject any other needed deps here:
		public authService: AuthService,
		public enumUtils: AppEnumUtils,
		private indicatorService: IndicatorService,
		private logger: LoggingService
	) {
		super(dialog, language, formService, router, uiNotificationService, httpErrorHandlingService, filterService, datePipe, route, queryParamsService);
	}

	ngOnInit(): void {

		super.ngOnInit();

	}

	getItem(itemId: Guid, successFunction: (item: Indicator) => void): void {
		this.indicatorService.getSingle(itemId, IndicatorEditorResolver.lookupFields())
			.pipe(map(data => data as Indicator), takeUntil(this._destroyed))
			.subscribe(
				data => successFunction(data),
				error => this.onCallbackError(error)
			);
	}


	prepareForm(data: Indicator): void {
		try {

			this.editorModel = data ? new IndicatorEditorModel().fromModel(data) : new IndicatorEditorModel();
			if(this.editorModel?.id){
				this.getElasticColumns();
			}
			this.isDeleted = data ? data.isActive === IsActive.Inactive : false;
			this.buildForm();
		} catch {
			this.logger.error('Could not parse Dataset: ' + data);
			this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.ERRORS.DEFAULT'), SnackBarNotificationLevel.Error);
		}
	}
	getElasticColumns() {
		this.indicatorService.queryElastic(this.createIndicatorElasticLookup())
			.pipe(map(data => data.items[0] as unknown as IndicatorElastic), takeUntil(this._destroyed))
			.subscribe(
				data => {
					if (data) {
						this.editorModel.indicatorElastic = data;
						this.editorModel.indicatorElastic.schema.fields = this.editorModel?.indicatorElastic?.schema?.fields.filter(element => {
							if (element.baseType == IndicatorElasticBaseType.Keyword) {
								return element;
							}
						})
					}

				},
				error => this.onCallbackError(error)
			);
	}
	createIndicatorElasticLookup(): IndicatorElasticLookup {
		const lookup = new IndicatorElasticLookup();
		if(this.editorModel.id){
			lookup.ids = [this.editorModel.id];
		}
		lookup.metadata = { countAll: true };
		//lookup.page = { offset: 0, size: 100 };
		lookup.isActive = [IsActive.Active];
		//lookup.order = { items: [] };

		lookup.project = {
			fields: [
				nameof<IndicatorElastic>(x => x.id),
				[nameof<IndicatorElastic>(x => x.schema), nameof<Schema>(x => x.fields), nameof<Field>(x => x.code)].join('.'),
				[nameof<IndicatorElastic>(x => x.schema), nameof<Schema>(x => x.fields), nameof<Field>(x => x.baseType)].join('.'),
				[nameof<IndicatorElastic>(x => x.schema), nameof<Schema>(x => x.fields), nameof<Field>(x => x.name)].join('.')

			]
		};
		return lookup;
	}

	buildForm() {
		this.formGroup = this.editorModel.buildForm(null, this.isDeleted || !this.authService.hasPermission(AppPermission.EditIndicator));
	}

	refreshData(): void {
		this.getItem(this.editorModel.id, (data: Indicator) => this.prepareForm(data));
	}

	refreshOnNavigateToData(id?: Guid): void {
		if (this.isNew) {
			this.formGroup.markAsPristine();
			this.router.navigate(['/indicators/' + (id ? id : this.editorModel.id)], { queryParams: { 'lookup': this.queryParamsService.serializeLookup(this.lookupParams), 'lv': ++this.lv }, replaceUrl: true });
		} else { this.internalRefreshData(); }
	}

	persistEntity(onSuccess?: (response) => void): void {

		const formData = this.formService.getValue(this.formGroup.value);

		this.indicatorService.persist(formData)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => onSuccess ? onSuccess(complete) : this.onCallbackSuccess(complete),
				error => this.onCallbackError(error)
			);
	}

	formSubmit(): void {
		this.formService.touchAllFormFields(this.formGroup);
		if (!this.isFormValid()) {
			return;
		}

		this.persistEntity();
	}

	public delete() {
		const value = this.formGroup.value;
		if (value.id) {
			const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
				maxWidth: '300px',
				data: {
					message: this.language.instant('COMMONS.CONFIRMATION-DIALOG.DELETE-ITEM'),
					confirmButton: this.language.instant('COMMONS.CONFIRMATION-DIALOG.ACTIONS.CONFIRM'),
					cancelButton: this.language.instant('COMMONS.CONFIRMATION-DIALOG.ACTIONS.CANCEL')
				}
			});
			dialogRef.afterClosed().pipe(takeUntil(this._destroyed)).subscribe(result => {
				if (result) {
					this.indicatorService.delete(value.id).pipe(takeUntil(this._destroyed))
						.subscribe(
							complete => this.onCallbackSuccess(),
							error => this.onCallbackError(error)
						);
				}
			});
		}
	}

	clearErrorModel() {
		this.editorModel.validationErrorModel.clear();
		this.formService.validateAllFormFields(this.formGroup);
	}
}
