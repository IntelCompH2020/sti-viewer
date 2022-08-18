package gr.cite.intelcomp.stiviewer.common.types.datatreeconfig;

import java.util.List;

public class DataTreeLevelEntity {
	private DataFieldEntity field;
	private int order;
	private Boolean supportSubLevel;
	private List<String> supportedDashboards;
	private List<DataTreeLevelItemEntity> items;

	public DataFieldEntity getField() {
		return field;
	}

	public void setField(DataFieldEntity field) {
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

	public List<DataTreeLevelItemEntity> getItems() {
		return items;
	}

	public void setItems(List<DataTreeLevelItemEntity> items) {
		this.items = items;
	}
}

