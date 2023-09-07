package gr.cite.intelcomp.stiviewer.elastic.query.lookup;

import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointKeywordFilter;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointLikeFilter;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyValidationException;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IndicatorPointDistinctLookup {

    private IndicatorPointLookup indicatorPointQuery;
    private String like;
    private List<String> excludedValues;
    private List<UUID> indicatorIds;
    private String field;
    private SortOrder order;
    private int batchSize;
    private boolean viewNotApprovedValues;
    private Map<String, Object> afterKey;

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public Map<String, Object> getAfterKey() {
        return afterKey;
    }

    public void setAfterKey(Map<String, Object> afterKey) {
        this.afterKey = afterKey;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public List<UUID> getIndicatorIds() {
        return indicatorIds;
    }

    public void setIndicatorIds(List<UUID> indicatorIds) {
        this.indicatorIds = indicatorIds;
    }

    public IndicatorPointLookup getIndicatorPointQuery() {
        return indicatorPointQuery;
    }

    public void setIndicatorPointQuery(IndicatorPointLookup indicatorPointQuery) {
        this.indicatorPointQuery = indicatorPointQuery;
    }

    public boolean isViewNotApprovedValues() {
        return viewNotApprovedValues;
    }

    public void setViewNotApprovedValues(boolean viewNotApprovedValues) {
        this.viewNotApprovedValues = viewNotApprovedValues;
    }

    public List<String> getExcludedValues() {
        return excludedValues;
    }

    public void setExcludedValues(List<String> excludedValues) {
        this.excludedValues = excludedValues;
    }

    public IndicatorPointQuery enrich(QueryFactory queryFactory) {
        IndicatorPointQuery query = indicatorPointQuery == null ? queryFactory.query(IndicatorPointQuery.class) : indicatorPointQuery.enrich(queryFactory);
        if (this.field == null || this.field.isBlank())
            throw new MyValidationException("field required");
        if (this.indicatorIds == null || this.indicatorIds.isEmpty())
            throw new MyValidationException("indicatorIds required");

        if (this.like != null)
            query.fieldLikeFilter(new IndicatorPointLikeFilter(List.of(this.field), this.like.replace('%', '*')));
        if (this.excludedValues != null)
            query.keywordExcludedFilters(new IndicatorPointKeywordFilter(this.field, this.excludedValues));

        return query;
    }

}
