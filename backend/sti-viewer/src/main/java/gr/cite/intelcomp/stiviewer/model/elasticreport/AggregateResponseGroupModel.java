package gr.cite.intelcomp.stiviewer.model.elasticreport;

import java.util.Map;

public class AggregateResponseGroupModel {
	private Map<String, String> items;
	private Map<String, String> itemLabels;
	private Map<String, String> getItemLabels() { return itemLabels; }
	public Map<String, String> getItems() {
		return items;
	}

	public void setItems(Map<String, String> items) {
		this.items = items;
	}
	public void setItemLabels(Map<String, String> itemLabels) {
		this.itemLabels  = itemLabels;
	}
}
