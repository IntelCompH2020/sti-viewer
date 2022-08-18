package gr.cite.intelcomp.stiviewer.model.elasticreport;

import org.elasticsearch.search.sort.SortOrder;

public class CompositeSource {
	private String field;
	public SortOrder order = SortOrder.ASC;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public SortOrder getOrder() {
		return order;
	}

	public void setOrder(SortOrder order) {
		this.order = order;
	}
}
