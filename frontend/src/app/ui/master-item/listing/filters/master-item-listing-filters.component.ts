import { Component, EventEmitter, Input, OnInit, Output, OnChanges, SimpleChanges } from '@angular/core';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { MasterItemFilter } from '@app/core/query/master-item.lookup';
import { BaseComponent } from '@common/base/base.component';
import { MatCheckboxChange } from '@angular/material/checkbox';

@Component({
	selector: 'app-master-item-listing-filters',
	templateUrl: './master-item-listing-filters.component.html',
	styleUrls: ['./master-item-listing-filters.component.scss']
})
export class MasterItemListingFiltersComponent extends BaseComponent implements OnInit, OnChanges {

	@Input() filter: MasterItemFilter;
	@Output() filterChange = new EventEmitter<MasterItemFilter>();
	// panelExpanded = false;
	filterSelections: { [key: string]: boolean } = {};

	constructor(
		public enumUtils: AppEnumUtils
	) { super(); }

	ngOnInit() {
		//this.panelExpanded = !this.areHiddenFieldsEmpty();
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['filter']) { this.visibleFields(this.filter); }
	}

	onFilterChange() {
		this.filterChange.emit(this.filter);
	}

	// private areHiddenFieldsEmpty(): boolean {
	// 	return (!this.filter.isActive || this.filter.isActive.length === 0 || (this.filter.isActive.length === 1 && this.filter.isActive[0] === IsActive.Active));
	// }

	//
	// Filter getters / setters
	// Implement here any custom logic regarding how these fields are applied to the lookup.
	//
	visibleFields(filter: MasterItemFilter) {
		this.filterSelections['isActiveOption'] = (filter.isActive && filter.isActive.length !== 0) ? true : false;
		this.filterSelections['likeOption'] = (filter.like && filter.like.length !== 0) ? true : false;
	}

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