package gr.cite.intelcomp.stiviewer.common.types.datatreeconfig;

import java.time.Instant;
import java.util.List;

public class DataTreeLevelItemEntity {
	private String value;	
	private Boolean hasNewData;

	private List<String> supportedDashboards;
	private Boolean supportSubLevel;

	public Boolean getHasNewData() {
		return hasNewData;
	}

	public void setHasNewData(Boolean hasNewData) {
		this.hasNewData = hasNewData;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getSupportedDashboards() {
		return supportedDashboards;
	}

	public void setSupportedDashboards(List<String> supportedDashboards) {
		this.supportedDashboards = supportedDashboards;
	}

	public Boolean getSupportSubLevel() {
		return supportSubLevel;
	}

	public void setSupportSubLevel(Boolean supportSubLevel) {
		this.supportSubLevel = supportSubLevel;
	}
}
