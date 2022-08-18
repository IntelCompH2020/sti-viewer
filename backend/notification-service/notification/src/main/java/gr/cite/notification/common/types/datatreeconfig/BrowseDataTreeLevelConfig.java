package gr.cite.notification.common.types.datatreeconfig;

import java.util.List;

public class BrowseDataTreeLevelConfig {
	private BrowseDataField field;
	private int order;
	private Boolean supportSubLevel;
	private List<String> supportedDashboards;

	public BrowseDataField getField() {
		return field;
	}

	public void setField(BrowseDataField field) {
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

