package gr.cite.intelcomp.stiviewer.model.elasticreport;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AggregateResponseModel {
	private Map<String, Object> afterKey;
	private List<AggregateResponseItemModel> items;
	private double total;

	public AggregateResponseModel() {
		items = new ArrayList<>();
	}

	public Map<String, Object> getAfterKey() {
		return afterKey;
	}

	public void setAfterKey(Map<String, Object> afterKey) {
		this.afterKey = afterKey;
	}

	public Boolean getHasMore() {
		return afterKey != null && !afterKey.isEmpty();
	}

	public List<AggregateResponseItemModel> getItems() {
		return items;
	}

	public void setItems(List<AggregateResponseItemModel> items) {
		this.items = items;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
}
