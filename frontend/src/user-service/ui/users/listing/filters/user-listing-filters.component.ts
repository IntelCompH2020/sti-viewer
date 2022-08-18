import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { IsActive } from '@app/core/enum/is-active.enum';
import { DatasetFilter } from '@app/core/query/dataset.lookup';
import { BaseComponent } from '@common/base/base.component';
import { UserServiceEnumUtils } from '@user-service/core/formatting/enum-utils.service';
import { UserFilter } from '@user-service/core/query/user.lookup';

@Component({
	selector: 'app-user-listing-filters',
	templateUrl: './user-listing-filters.component.html',
	styleUrls: ['./user-listing-filters.component.scss']
})
export class UserListingFiltersComponent extends BaseComponent implements OnInit, OnChanges {

	@Input() filter: UserFilter;
	@Output() filterChange = new EventEmitter<UserFilter>();
	panelExpanded = false;
	filterSelections: { [key: string]: boolean } = {};
	constructor(
		public enumUtils: UserServiceEnumUtils
	) { super(); }

	ngOnInit() {
		this.panelExpanded = !this.areHiddenFieldsEmpty();
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['filter']) { this.panelExpanded = !this.areHiddenFieldsEmpty(); }
	}

	onFilterChange() {
		this.filterChange.emit(this.filter);
	}
	visibleFields(filter: DatasetFilter) {
		this.filterSelections['isActiveOption'] = (filter.isActive && filter.isActive.length !== 0) ? true : false;
		this.filterSelections['likeOption'] = (filter.like && filter.like.length !== 0) ? true : false;
	}
	private areHiddenFieldsEmpty(): boolean {
		return (!this.filter.isActive || this.filter.isActive.length === 0 || (this.filter.isActive.length === 1 && this.filter.isActive[0] === IsActive.Active))
			;
	}

	//
	// Filter getters / setters
	// Implement here any custom logic regarding how these fields are applied to the lookup.
	//
	get like(): string {
		return this.filter.like;
	}
	set like(value: string) {
		this.filter.like = value;
		this.filterChange.emit(this.filter);
	}

	get isActive(): boolean {
		return this.filter.isActive ? this.filter.isActive.includes(IsActive.Inactive) : true;
	}
	set isActive(value: boolean) {
		this.filter.isActive = value ? [IsActive.Active, IsActive.Inactive] : [IsActive.Active];
		this.filterChange.emit(this.filter);
	}


	selectAll() {
		Object.keys(this.filterSelections).forEach(key => {
			this.filterSelections[key] = true;
		});
	}

	deselectAll() {
		this.filter.isActive = undefined;
		this.filter.like = undefined;

		this.visibleFields(this.filter);
		this.filterChange.emit(this.filter);
	}

	areAllSelected(): boolean {
		if (Object.values(this.filterSelections).indexOf(false) === -1) {
			return true;
		} else { return false; }
	}

	areNoneSelected(): boolean {
		if (Object.values(this.filterSelections).indexOf(true) === -1) {
			return true;
		} else { return false; }
	}

	countOpenFilters(): number {
		const filtered = Object.entries(this.filterSelections).filter(([k, v]) => v === true);
		return filtered.length === 0 ? undefined : filtered.length;
	}

	checkBoxChanged(event: MatCheckboxChange, filter: string) {
		if (!event.checked && this.filter[filter] != null) {
			this.filter[filter] = filter !== 'isActive' ? undefined : [IsActive.Active];
			this.filterChange.emit(this.filter);
		}
	}
}
