package gr.cite.intelcomp.stiviewer.common.types.externaltoken;

import gr.cite.intelcomp.stiviewer.authorization.HierarchyIndicatorColumnAccess;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.*;
import gr.cite.tools.data.query.QueryFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class IndicatorPointQueryDefinitionEntity {
    private Collection<UUID> ids;
    private Collection<UUID> indicatorIds;
    private Collection<UUID> batchIds;
    private Collection<IndicatorPointKeywordFilter> keywordFilters;
    private Collection<IndicatorPointKeywordFilter> keywordExcludedFilters;
    private Collection<IndicatorPointDateFilter> dateFilters;
    private Collection<IndicatorPointIntegerFilter> integerFilters;
    private Collection<IndicatorPointDoubleFilter> doubleFilters;
    private Collection<IndicatorPointDateRangeFilter> dateRangeFilters;
    private Collection<IndicatorPointIntegerRangeFilter> integerRangeFilters;
    private Collection<IndicatorPointDoubleRangeFilter> doubleRangeFilters;
    private Collection<String> groupHashes;
    private IndicatorPointLikeFilter fieldLikeFilter;
    private List<HierarchyIndicatorColumnAccess> indicatorColumnAccesses;
    private List<UUID> allowedIndicatorIds;

    public Collection<UUID> getIds() {
        return ids;
    }

    public void setIds(Collection<UUID> ids) {
        this.ids = ids;
    }

    public Collection<UUID> getIndicatorIds() {
        return indicatorIds;
    }

    public void setIndicatorIds(Collection<UUID> indicatorIds) {
        this.indicatorIds = indicatorIds;
    }

    public Collection<UUID> getBatchIds() {
        return batchIds;
    }

    public void setBatchIds(Collection<UUID> batchIds) {
        this.batchIds = batchIds;
    }

    public Collection<IndicatorPointKeywordFilter> getKeywordFilters() {
        return keywordFilters;
    }

    public void setKeywordFilters(Collection<IndicatorPointKeywordFilter> keywordFilters) {
        this.keywordFilters = keywordFilters;
    }

    public Collection<IndicatorPointKeywordFilter> getKeywordExcludedFilters() {
        return keywordExcludedFilters;
    }

    public void setKeywordExcludedFilters(Collection<IndicatorPointKeywordFilter> keywordExcludedFilters) {
        this.keywordExcludedFilters = keywordExcludedFilters;
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

    public Collection<String> getGroupHashes() {
        return groupHashes;
    }

    public void setGroupHashes(Collection<String> groupHashes) {
        this.groupHashes = groupHashes;
    }

    public IndicatorPointLikeFilter getFieldLikeFilter() {
        return fieldLikeFilter;
    }

    public void setFieldLikeFilter(IndicatorPointLikeFilter fieldLikeFilter) {
        this.fieldLikeFilter = fieldLikeFilter;
    }

    public List<HierarchyIndicatorColumnAccess> getIndicatorColumnAccesses() {
        return indicatorColumnAccesses;
    }

    public void setIndicatorColumnAccesses(List<HierarchyIndicatorColumnAccess> indicatorColumnAccesses) {
        this.indicatorColumnAccesses = indicatorColumnAccesses;
    }

    public List<UUID> getAllowedIndicatorIds() {
        return allowedIndicatorIds;
    }

    public void setAllowedIndicatorIds(List<UUID> allowedIndicatorIds) {
        this.allowedIndicatorIds = allowedIndicatorIds;
    }
}
