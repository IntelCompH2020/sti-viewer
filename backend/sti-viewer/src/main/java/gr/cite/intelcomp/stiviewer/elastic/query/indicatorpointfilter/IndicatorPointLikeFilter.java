package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

import java.util.List;

public class IndicatorPointLikeFilter {
	private List<String> fields;
	private String like;

	public IndicatorPointLikeFilter() {
	}

	public IndicatorPointLikeFilter(List<String> fields, String like) {
		this.fields = fields;
		this.like = like;
	}

	public List<String> getFields() {
		return fields;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
}
