package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

public class IndicatorPointIntegerRangeFilter {
	private String field;
	private Integer from;
	private Integer to;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTo() {
		return to;
	}

	public void setTo(Integer to) {
		this.to = to;
	}
}

