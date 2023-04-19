package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.tools.elastic.query.Aggregation.MetricAggregateType;
import org.elasticsearch.search.sort.SortOrder;

public class AggregationMetricSortField {
	private String field;
	private MetricAggregateType metricAggregateType;
	private SortOrder order = SortOrder.ASC;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public MetricAggregateType getMetricAggregateType() {
		return metricAggregateType;
	}

	public void setMetricAggregateType(MetricAggregateType metricAggregateType) {
		this.metricAggregateType = metricAggregateType;
	}

	public SortOrder getOrder() {
		return order;
	}

	public void setOrder(SortOrder order) {
		this.order = order;
	}
}
