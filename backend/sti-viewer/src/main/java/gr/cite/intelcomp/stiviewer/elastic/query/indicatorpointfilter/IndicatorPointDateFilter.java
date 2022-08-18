package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

import gr.cite.tools.elastic.query.CompareOperator;

import java.time.Instant;

public class IndicatorPointDateFilter {
	private String field;
	private Instant value;

	private CompareOperator compareOperator;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Instant getValue() {
		return value;
	}

	public void setValue(Instant value) {
		this.value = value;
	}

	public CompareOperator getCompareOperator() {
		return compareOperator;
	}

	public void setCompareOperator(CompareOperator compareOperator) {
		this.compareOperator = compareOperator;
	}
}
