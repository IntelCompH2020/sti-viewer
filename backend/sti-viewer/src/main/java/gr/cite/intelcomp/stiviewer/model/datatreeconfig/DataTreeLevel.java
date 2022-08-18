package gr.cite.intelcomp.stiviewer.model.datatreeconfig;


import java.util.List;

public class DataTreeLevel {
	private DataTreeDataField field;
	public static final String _field = "field";
	private int order;
	public static final String _order = "order";

	private Boolean supportSubLevel;
	public static final String _supportSubLevel = "supportSubLevel";

	private List<String> supportedDashboards;
	public static final String _supportedDashboards = "supportedDashboards";

	public List<DataTreeLevelItem> items;
	public static final String _items = "items";

	public DataTreeDataField getField() {
		return field;
	}

	public void setField(DataTreeDataField field) {
		this.field = field;
	}

	public Boolean getSupportSubLevel() {
		return supportSubLevel;
	}


	public void setSupportSubLevel(Boolean supportSubLevel) {
		this.supportSubLevel = supportSubLevel;
	}

	public List<String> getSupportedDashboards() {
		return supportedDashboards;
	}

	public void setSupportedDashboards(List<String> supportedDashboards) {
		this.supportedDashboards = supportedDashboards;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<DataTreeLevelItem> getItems() {
		return items;
	}

	public void setItems(List<DataTreeLevelItem> items) {
		this.items = items;
	}
}