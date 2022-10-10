package gr.cite.intelcomp.stiviewer.common.types.datatreeconfig;

import java.util.List;

public class DataTreeLevelDashboardOverrideEntity {
	private List<String> supportedDashboards;
	private Boolean supportSubLevel;
	private List<DataTreeLevelDashboardOverrideFieldRequirementEntity> requirements;

	public List<String> getSupportedDashboards() {
		return supportedDashboards;
	}

	public void setSupportedDashboards(List<String> supportedDashboards) {
		this.supportedDashboards = supportedDashboards;
	}

	public List<DataTreeLevelDashboardOverrideFieldRequirementEntity> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<DataTreeLevelDashboardOverrideFieldRequirementEntity> requirements) {
		this.requirements = requirements;
	}

	public Boolean getSupportSubLevel() {
		return supportSubLevel;
	}

	public void setSupportSubLevel(Boolean supportSubLevel) {
		this.supportSubLevel = supportSubLevel;
	}

	public boolean applies(List<DataTreeLevelDashboardOverrideFieldRequirementEntity> item) {
		if (item == null || this.requirements == null || this.requirements.isEmpty() || item.size() < this.requirements.size()) return false;
		for (DataTreeLevelDashboardOverrideFieldRequirementEntity requirement : this.requirements) {
			if (!item.stream().anyMatch(x -> x.equals(requirement))) return false;
		}
		return true;
	}
}
