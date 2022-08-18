package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

public class IndicatorPointDoubleRangeFilter {
	private String field;
	private Double from;
	private Double to;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Double getFrom() {
		return from;
	}

	public void setFrom(Double from) {
		this.from = from;
	}

	public Double getTo() {
		return to;
	}

	public void setTo(Double to) {
		this.to = to;
	}
}

