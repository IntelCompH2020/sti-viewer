import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { FormService } from '@common/forms/form-service';
import { LoggingService } from '@common/logging/logging-service';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { Guid } from '@common/types/guid';
import { TranslateService } from '@ngx-translate/core';
import { map, takeUntil } from 'rxjs/operators';
import { BaseEditor } from '@common/base/base-editor';
import { FilterService } from '@common/modules/text-filter/filter-service';
import { DatePipe } from '@angular/common';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { DataAccessRequestEditorModel} from './data-access-request-editor.model';
import { DataAccessFilterColumnPersist, DataAccessRequest, DataAccessRequestConfigPersist, DataAccessRequestIndicatorConfig, DataAccessRequestIndicatorConfigPersist, DataAccessRequestStatusPersist } from '@app/core/model/data-access-request/data-access-request.model';
import { DataAccessRequestService } from '@app/core/services/http/data-access-request.service';
import { DataAccessRequestEditorResolver } from './data-access-request-editor.resolver';
import { IsActive } from '@app/core/enum/is-active.enum';
import { DataAccessRequestStatus } from '@app/core/enum/data-access-request-status.enum';
import { IndicatorGroupService } from '@app/core/services/http/indicator-group.service';
import { IndicatorGroup } from '@app/core/model/indicator-group/indicator-group.model';
import { nameof } from 'ts-simple-nameof';
import { Indicator } from '@app/core/model/indicator/indicator.model';



@Component({
	selector: 'app-data-access-request-editor',
	templateUrl: './data-access-request-editor.component.html',
	styleUrls: ['./data-access-request-editor.component.scss']
})
export class DataAccessRequestEditorComponent extends BaseEditor<DataAccessRequestEditorModel, DataAccessRequest> implements OnInit {


	isNew = true;
	canEdit = false;
	canApprove = false;
	canReject = false;
	canWithdrawn = false;
	canSubmit = false;
	canInProgress = false;
	isDeleted = false;

	formGroup: UntypedFormGroup = null;

	dataAccessRequestStatus = DataAccessRequestStatus;
	saveClicked = false;
	get selectedIndicators(): Guid[] {
		return this.indicatorsConfigArray?.value?.map(x => x?.indicator?.id).filter(x => x) ?? [];
	}
	get indicatorsConfigArray(): UntypedFormArray {
		return this.formGroup?.get('indicatorsConfigArray') as UntypedFormArray;
	}
	get indicatorGroupsArray(): FormArray{
		return this.formGroup.get('indicatorGroupConfigs') as FormArray;
	}


	indicatorGroups: IndicatorGroup[];
	selectedGroupIds: Guid[];

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
		private dataAccessRequestService: DataAccessRequestService,
		private indicatorGroupService: IndicatorGroupService,
		private logger: LoggingService,
		private formBuilder: FormBuilder
	) {
		super(dialog, language, formService, router, uiNotificationService, httpErrorHandlingService, filterService, datePipe, route, queryParamsService);
	}

	ngOnInit(): void {
		super.ngOnInit();
		if(this.isNew){
			this.indicatorGroupService.getAll([
				nameof<IndicatorGroup>(x => x.id),
				nameof<IndicatorGroup>(x => x.name),
				[nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.code)].join('.'),
				[nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.name)].join('.'),
				[nameof<IndicatorGroup>(x => x.indicators), nameof<Indicator>(x => x.id)].join('.'),
				nameof<IndicatorGroup>(x => x.filterColumns)
			])
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) =>{
				this.indicatorGroups = result;
				if(result.length === 1){
					this.selectedGroupIds = [result[0].id]
					const fg = this._buildIndicatorGroup(result[0].id);
					this.indicatorGroupsArray.push(fg);
				}
			})
		}
	}


	private _buildIndicatorGroup(gId: Guid): FormGroup{
		const indicatorGroup = this.indicatorGroups.find(x => x.id?.toString() === gId?.toString());

		if(!indicatorGroup){
			console.warn('No groupId with specified id was found');
			return;
		}

		const indicatorIds = indicatorGroup.indicators.map(indicator => indicator.id);
		const groupId = indicatorGroup.id;
		const filterColumns = indicatorGroup.filterColumns;

		const fg = this.formBuilder.group({
			groupId: groupId,
			indicatorIds: [indicatorIds],
			filterColumns: this.formBuilder.array(
				filterColumns.map(fc => this.formBuilder.group({column: fc, values:[[]]}))
			)
		})

		return fg;
	}


	onChangeGroup(selectedGroupIds: Guid[] | string[]): void{

		selectedGroupIds = selectedGroupIds.map(x => x.toString());

		const toRemove: number[] = this.indicatorGroupsArray?.value?.reduce((aggr, current, index)=>{
			if(selectedGroupIds.includes(current.groupId)){
				return aggr;
			}
			return [...aggr, index]
		}, [] as number[]);


		toRemove?.reverse().forEach(index =>{
			this.indicatorGroupsArray.removeAt(index);
		});

		selectedGroupIds.forEach(guid =>{
			const fg = this._buildIndicatorGroup(Guid.parse(guid.toString()));
			this.indicatorGroupsArray.push(fg);
		})
	}
	getItem(itemId: Guid, successFunction: (item: DataAccessRequest) => void): void {
		this.dataAccessRequestService.getSingle(itemId, DataAccessRequestEditorResolver.lookupFields())
			.pipe(map(data => {
				return data;
			}), takeUntil(this._destroyed))
			.subscribe(
				data => successFunction(data),
				error => this.onCallbackError(error)
			);
	}

	prepareForm(data: DataAccessRequest): void {

		try {

			this.editorModel = data ? new DataAccessRequestEditorModel().fromModel(data) : new DataAccessRequestEditorModel();
			this.isDeleted = data ? data.isActive === IsActive.Inactive : false;
			this.isNew = data == null;


			if (this.isNew) {
				this.canEdit = this.authService.hasPermission(this.authService.permissionEnum.CreateDataAccessRequest);
				this.canSubmit = this.authService.hasPermission(this.authService.permissionEnum.CreateDataAccessRequest);
				this.canWithdrawn = false;
				this.canApprove = false;
				this.canReject = false;
				this.canInProgress = false;
			} else {
				switch (this.editorModel.status) {
					case DataAccessRequestStatus.NEW:
						this.canEdit = this.authService.userId() === data.user.id && this.authService.hasPermission(this.authService.permissionEnum.CreateDataAccessRequest);
						this.canWithdrawn = this.authService.userId() === data.user.id && this.authService.hasPermission(this.authService.permissionEnum.CreateDataAccessRequest);
						this.canSubmit = this.authService.userId() === data.user.id && this.authService.hasPermission(this.authService.permissionEnum.CreateDataAccessRequest);
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case DataAccessRequestStatus.APPROVED:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case DataAccessRequestStatus.DELETED:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case DataAccessRequestStatus.REJECTED:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case DataAccessRequestStatus.WITHDRAWN:
						this.canEdit = false;
						this.canWithdrawn = false;
						this.canSubmit = false;
						this.canApprove = false;
						this.canReject = false;
						this.canInProgress = false;
						break;
					case DataAccessRequestStatus.SUBMITTED:
						this.canEdit = false;
						this.canWithdrawn = this.authService.userId() === data.user.id;
						this.canSubmit = false;
						this.canApprove = this.authService.hasPermission(this.authService.permissionEnum.ApproveDataAccessRequest);
						this.canReject = this.authService.hasPermission(this.authService.permissionEnum.RejectDataAccessRequest);
						this.canInProgress = this.authService.hasPermission(this.authService.permissionEnum.EditDataAccessRequest);
						break;
					case DataAccessRequestStatus.IN_PROCESS:
						this.canEdit = false;
						this.canWithdrawn = this.authService.userId() === data.user.id;
						this.canSubmit = false;
						this.canApprove = this.authService.hasPermission(this.authService.permissionEnum.ApproveDataAccessRequest);
						this.canReject = this.authService.hasPermission(this.authService.permissionEnum.RejectDataAccessRequest);
						this.canInProgress = this.authService.hasPermission(this.authService.permissionEnum.EditDataAccessRequest);
						break;
				}
			}
			this.buildForm()
		} catch {
			this.logger.error('Could not parse DataAccessRequest: ' + data);
			this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.ERRORS.DEFAULT'), SnackBarNotificationLevel.Error);
		}
	}

	buildForm() {
		this.formGroup = this.editorModel.buildForm(null, this.isDeleted || !this.authService.hasPermission(AppPermission.EditDataAccessRequest));
	}

	refreshData(): void {
		this.getItem(this.editorModel.id, (data: DataAccessRequest) => this.prepareForm(data));
	}

	refreshOnNavigateToData(id?: Guid): void {
		if (this.isNew) {
			this.formGroup.markAsPristine();
			this.router.navigate(['/data-access-requests/' + (id ? id : this.editorModel.id)], { queryParams: { 'lookup': this.queryParamsService.serializeLookup(this.lookupParams), 'lv': ++this.lv }, replaceUrl: true });
		} else { this.internalRefreshData(); }
	}

	persistEntity(onSuccess?: (response) => void): void {
		this.dataAccessRequestService.persist(this.buildFormdataTosave())
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
	}

	public submit() {
		this.clearErrorModel();
		this.formService.touchAllFormFields(this.formGroup);
		if (!this.isFormValid()) {
			return;
		}
		this.dataAccessRequestService.persist(this.buildFormdataTosave())
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => this.onCallbackSuccess(complete),
				error => this.onCallbackError(error)
			);
	}

	buildFormdataTosave(): any {
		const formData = this.formService.getValue(this.formGroup.value);
		const tmpIndicators = formData.indicatorsConfigArray as DataAccessRequestIndicatorConfig[];
		const indicatorsToSave: DataAccessRequestIndicatorConfigPersist[] = [];
		tmpIndicators.forEach(x => {
			let filterColumnsTosave: DataAccessFilterColumnPersist[] = [];
			(x.filterColumns as DataAccessFilterColumnPersist[]).forEach(columnConfig => {
				if (columnConfig.column == null || typeof columnConfig.column === undefined) { return; }
				if (columnConfig.values == null || typeof columnConfig.values === undefined || columnConfig.values.length <= 0) { return; }
				filterColumnsTosave.push(columnConfig);
			});
			indicatorsToSave.push({ id: x?.indicator?.id, filterColumns: filterColumnsTosave })
		});
		formData.config = { 
			indicators: indicatorsToSave, 
			indicatorGroups: this.indicatorGroupsArray.value.map(x => {
				const {indicatorIds,...rest} = x;
				return rest;
			}
		) };
		formData.status = DataAccessRequestStatus.SUBMITTED;

		const {indicatorsConfigArray,indicatorGroupConfigs ,...fdata} = formData;

		return fdata;
	}
	public setStatus(status: DataAccessRequestStatus) {
		const formData = this.formService.getValue(this.formGroup.value);


		const ind = formData.indicatorsConfigArray.map((x: DataAccessRequestIndicatorConfig) => {
			return {
				id: x?.indicator?.id,
				filterColumns: x?.filterColumns
			} as DataAccessRequestIndicatorConfigPersist;
		});
		formData.config = { indicators: ind } as DataAccessRequestConfigPersist;

		const persist: DataAccessRequestStatusPersist = {
			id: formData.id,
			hash: formData.hash,
			status: status,
			config: formData.config
		};

		this.dataAccessRequestService.status(persist)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => this.onCallbackSuccess(complete),
				error => this.onCallbackError(error)
			);

	}
	clearErrorModel() {
		this.editorModel.validationErrorModel.clear();
		this.formService.validateAllFormFields(this.formGroup);
	}

	deleteConfig(index: number) {
		this.indicatorsConfigArray.removeAt(index);
	}

	addNewConfig() {
		let fg = new UntypedFormBuilder().group({ indicator: null, filterColumns: new UntypedFormBuilder().array([]) });
		this.indicatorsConfigArray.push(fg);
	}


	public toDescSortField(value: string): string {
		return '-' + value;
	}
}
