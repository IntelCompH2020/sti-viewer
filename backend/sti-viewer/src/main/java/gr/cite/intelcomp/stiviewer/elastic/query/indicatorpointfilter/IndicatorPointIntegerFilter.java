package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

import gr.cite.tools.elastic.query.CompareOperator;

public class IndicatorPointIntegerFilter {
	private String field;
	private Integer value;

	private CompareOperator compareOperator;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public CompareOperator getCompareOperator() {
		return compareOperator;
	}

	public void setCompareOperator(CompareOperator compareOperator) {
		this.compareOperator = compareOperator;
	}
}

