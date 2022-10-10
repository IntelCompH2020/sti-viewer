package gr.cite.intelcomp.stiviewer.model.datatreeconfig;

import java.util.List;

public class DataTreeLevelItem {
	private String value;
	public static final String _value = "value";

	private List<String> supportedDashboards;
	public static final String _supportedDashboards = "supportedDashboards";

	private Boolean supportSubLevel;
	public static final String _supportSubLevel = "supportSubLevel";

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
