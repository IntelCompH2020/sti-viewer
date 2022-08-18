package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

import gr.cite.tools.elastic.query.CompareOperator;

public class IndicatorPointDoubleFilter {
	private String field;
	private Double value;

	private CompareOperator compareOperator;

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

	public CompareOperator getCompareOperator() {
		return compareOperator;
	}

	public void setCompareOperator(CompareOperator compareOperator) {
		this.compareOperator = compareOperator;
	}
}

