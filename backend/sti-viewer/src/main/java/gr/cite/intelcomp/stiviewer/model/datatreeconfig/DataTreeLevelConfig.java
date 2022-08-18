package gr.cite.intelcomp.stiviewer.model.datatreeconfig;

import java.util.List;

public class DataTreeLevelConfig {
	private DataTreeDataField field;
	public static final String _field = "field";
	private int order;
	public static final String _order = "order";

	private Boolean supportSubLevel;
	public static final String _supportSubLevel = "supportSubLevel";

	private List<String> supportedDashboards;
	public static final String _supportedDashboards = "supportedDashboards";

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
}




