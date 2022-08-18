package gr.cite.intelcomp.stiviewer.model.elasticreport;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.elasticsearch.search.sort.SortOrder;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("Terms")
public class Terms extends Bucket {
	public SortOrder order = SortOrder.ASC;

	public SortOrder getOrder() {
		return order;
	}

	public void setOrder(@NotNull SortOrder order) {
		this.order = order;
	}
}
