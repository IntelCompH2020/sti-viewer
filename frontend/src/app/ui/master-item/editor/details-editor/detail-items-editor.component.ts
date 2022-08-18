import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormArray } from '@angular/forms';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { Dataset } from '@app/core/model/dataset/dataset.model';
import { DatasetLookup } from '@app/core/query/dataset.lookup';
import { DatasetService } from '@app/core/services/http/dataset.service';
import { BaseComponent } from '@common/base/base.component';
import { SingleAutoCompleteConfiguration } from '@common/modules/auto-complete/single/single-auto-complete-configuration';
import { FilterService } from '@common/modules/text-filter/filter-service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Component({
	selector: 'app-detail-items-editor',
	templateUrl: './detail-items-editor.component.html',
	styleUrls: ['./detail-items-editor.component.scss']
})
export class DetailItemEditorComponent extends BaseComponent implements OnInit {

	@Input() detailsFormArray: UntypedFormArray = null;
	@Input() showRemoveButton = true;
	@Output() removeItemAt: EventEmitter<number> = new EventEmitter<number>();

	datasetAutocompleteConfiguration: SingleAutoCompleteConfiguration = {
		initialItems: this.initialDatasetItems.bind(this),
		filterFn: this.datasetFilterFn.bind(this),
		displayFn: (item: Dataset) => item.title,
		titleFn: (item: Dataset) => item.title,
	};

	constructor(
		// public authService: AuthService,
		// private dialog: MatDialog,
		// private route: ActivatedRoute,
		// private router: Router,
		// private language: TranslateService,
		public enumUtils: AppEnumUtils,
		// private formService: FormService,
		// private uiNotificationService: UiNotificationService,
		// private logger: LoggingService,
		// private httpErrorHandlingService: HttpErrorHandlingService
		private filterService: FilterService,
		private datasetService: DatasetService
	) {
		super();
	}

	ngOnInit(): void {

	}

	removeDetail(index: number) {
		this.removeItemAt.emit(index);
	}

	buildDatasetLookup(like?: string): DatasetLookup {
		const lookup: DatasetLookup = new DatasetLookup();
		lookup.isActive = [IsActive.Active];
		lookup.project = {
			fields: [
				nameof<Dataset>(x => x.id),
				nameof<Dataset>(x => x.title)
			]
		};
		if (like) { lookup.like = this.filterService.transformLike(like); }
		return lookup;
	}

	initialDatasetItems(): Observable<Dataset[]> {
		return this.datasetService.query(this.buildDatasetLookup()).pipe(map(x => x.items));
	}

	datasetFilterFn(searchQuery: string): Observable<Dataset[]> {
		return this.datasetService.query(this.buildDatasetLookup(searchQuery)).pipe(map(x => x.items));
	}
}
