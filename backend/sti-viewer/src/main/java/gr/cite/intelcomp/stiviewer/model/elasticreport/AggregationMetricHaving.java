package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.tools.elastic.query.Aggregation.AggregationMetricHavingOperator;
import gr.cite.tools.elastic.query.Aggregation.AggregationMetricHavingType;
import gr.cite.tools.elastic.query.Aggregation.MetricAggregateType;

public class AggregationMetricHaving {
	private String field;
	private MetricAggregateType metricAggregateType;
	private AggregationMetricHavingType type;
	private AggregationMetricHavingOperator operator;
	private Double value;

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

	public AggregationMetricHavingType getType() {
		return type;
	}

	public void setType(AggregationMetricHavingType type) {
		this.type = type;
	}

	public AggregationMetricHavingOperator getOperator() {
		return operator;
	}

	public void setOperator(AggregationMetricHavingOperator operator) {
		this.operator = operator;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
