package gr.cite.intelcomp.stiviewer.model.elasticreport;

import org.elasticsearch.search.sort.SortOrder;
import org.jetbrains.annotations.NotNull;

public class GroupingField {
	public String field;
	public SortOrder order = SortOrder.ASC;

	public String getField() {
		return field;
	}

	public void setField(@NotNull String field) {
		this.field = field;
	}

	public SortOrder getOrder() {
		return order;
	}

	public void setOrder(@NotNull SortOrder order) {
		this.order = order;
	}
}
