package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.tools.elastic.query.Aggregation.MetricAggregateType;

public class Metric {
	private MetricAggregateType type;
	private String field;

	public MetricAggregateType getType() {
		return type;
	}

	public void setType(MetricAggregateType type) {
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
}


