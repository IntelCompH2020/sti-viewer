package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.tools.elastic.query.Aggregation.MetricAggregateType;

public class AggregateResponseValueModel {
	private MetricAggregateType aggregateType;
	private String field;
	private Double value;

	public MetricAggregateType getAggregateType() {
		return aggregateType;
	}

	public void setAggregateType(MetricAggregateType aggregateType) {
		this.aggregateType = aggregateType;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
