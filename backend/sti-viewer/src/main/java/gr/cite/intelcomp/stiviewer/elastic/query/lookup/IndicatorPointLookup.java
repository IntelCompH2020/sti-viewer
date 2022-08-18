package gr.cite.intelcomp.stiviewer.elastic.query.lookup;

import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.*;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class IndicatorPointLookup extends Lookup {
	private List<UUID> ids;
	private List<String> groupHashes;
	private List<IndicatorPointKeywordFilter> keywordFilters;
	private Collection<IndicatorPointDateFilter> dateFilters;
	private Collection<IndicatorPointIntegerFilter> integerFilters;
	private Collection<IndicatorPointDoubleFilter> doubleFilters;
	private Collection<IndicatorPointDateRangeFilter> dateRangeFilters;
	private Collection<IndicatorPointIntegerRangeFilter> integerRangeFilters;
	private Collection<IndicatorPointDoubleRangeFilter> doubleRangeFilters;
	private IndicatorPointLikeFilter fieldLikeFilter;

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}

	public List<String> getGroupHashes() {
		return groupHashes;
	}

	public void setGroupHashes(List<String> groupHashes) {
		this.groupHashes = groupHashes;
	}

	public List<IndicatorPointKeywordFilter> getKeywordFilters() {
		return keywordFilters;
	}

	public void setKeywordFilters(List<IndicatorPointKeywordFilter> keywordFilters) {
		this.keywordFilters = keywordFilters;
	}

	public Collection<IndicatorPointDateFilter> getDateFilters() {
		return dateFilters;
	}

	public void setDateFilters(Collection<IndicatorPointDateFilter> dateFilters) {
		this.dateFilters = dateFilters;
	}

	public Collection<IndicatorPointIntegerFilter> getIntegerFilters() {
		return integerFilters;
	}

	public void setIntegerFilters(Collection<IndicatorPointIntegerFilter> integerFilters) {
		this.integerFilters = integerFilters;
	}

	public Collection<IndicatorPointDoubleFilter> getDoubleFilters() {
		return doubleFilters;
	}

	public void setDoubleFilters(Collection<IndicatorPointDoubleFilter> doubleFilters) {
		this.doubleFilters = doubleFilters;
	}

	public Collection<IndicatorPointDateRangeFilter> getDateRangeFilters() {
		return dateRangeFilters;
	}

	public void setDateRangeFilters(Collection<IndicatorPointDateRangeFilter> dateRangeFilters) {
		this.dateRangeFilters = dateRangeFilters;
	}

	public Collection<IndicatorPointIntegerRangeFilter> getIntegerRangeFilters() {
		return integerRangeFilters;
	}

	public void setIntegerRangeFilters(Collection<IndicatorPointIntegerRangeFilter> integerRangeFilters) {
		this.integerRangeFilters = integerRangeFilters;
	}

	public Collection<IndicatorPointDoubleRangeFilter> getDoubleRangeFilters() {
		return doubleRangeFilters;
	}

	public void setDoubleRangeFilters(Collection<IndicatorPointDoubleRangeFilter> doubleRangeFilters) {
		this.doubleRangeFilters = doubleRangeFilters;
	}

	public IndicatorPointLikeFilter getFieldLikeFilter() {
		return fieldLikeFilter;
	}

	public void setFieldLikeFilter(IndicatorPointLikeFilter fieldLikeFilter) {
		this.fieldLikeFilter = fieldLikeFilter;
	}

	public IndicatorPointQuery enrich(QueryFactory queryFactory) {
		IndicatorPointQuery query = queryFactory.query(IndicatorPointQuery.class);
		if (this.ids != null) query.ids(this.ids);
		if (this.groupHashes != null) query.groupHashes(this.groupHashes);
		if (this.keywordFilters != null) query.keywordFilters(this.keywordFilters);
		if (this.dateFilters != null) query.dateFilters(this.dateFilters);
		if (this.integerFilters != null) query.integerFilters(this.integerFilters);
		if (this.doubleFilters != null) query.doubleFilters(this.doubleFilters);
		if (this.dateRangeFilters != null) query.dateRangeFilters(this.dateRangeFilters);
		if (this.integerRangeFilters != null) query.integerRangeFilters(this.integerRangeFilters);
		if (this.doubleRangeFilters != null) query.doubleRangeFilters(this.doubleRangeFilters);
		if (this.fieldLikeFilter != null) query.fieldLikeFilter(this.fieldLikeFilter);

		this.enrichCommon(query);

		return query;
	}

}

