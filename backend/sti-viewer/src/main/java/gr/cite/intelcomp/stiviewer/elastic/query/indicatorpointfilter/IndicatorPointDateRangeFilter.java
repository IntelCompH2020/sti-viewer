package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

import java.time.Instant;

public class IndicatorPointDateRangeFilter {
	private String field;
	private Instant from;
	private Instant to;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Instant getFrom() {
		return from;
	}

	public void setFrom(Instant from) {
		this.from = from;
	}

	public Instant getTo() {
		return to;
	}

	public void setTo(Instant to) {
		this.to = to;
	}
}
