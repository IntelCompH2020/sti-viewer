package gr.cite.intelcomp.stiviewer.model.datatreeconfig;

import java.util.List;

public class DataTreeLevelDashboardOverride {
	private List<DataTreeLevelDashboardOverrideFieldRequirement> requirements;
	public static final String _requirements = "requirements";
	private List<String> supportedDashboards;
	public static final String _supportedDashboards = "supportedDashboards";
	private Boolean supportSubLevel;
	public static final String _supportSubLevel = "supportSubLevel";

	public List<DataTreeLevelDashboardOverrideFieldRequirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<DataTreeLevelDashboardOverrideFieldRequirement> requirements) {
		this.requirements = requirements;
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


