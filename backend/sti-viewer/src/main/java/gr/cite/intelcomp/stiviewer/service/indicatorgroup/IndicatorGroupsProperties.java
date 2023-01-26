package gr.cite.intelcomp.stiviewer.service.indicatorgroup;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.UUID;

@ConfigurationProperties(prefix = "indicator-group")
public class IndicatorGroupsProperties {
	private List<IndicatorGroup> groups;

	public List<IndicatorGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<IndicatorGroup> indicatorGroups) {
		this.groups = indicatorGroups;
	}

	public static class IndicatorGroup {
		private UUID groupId;
		private String name;
		private String code;
		private String dashboardKey;
		private List<String> indicatorCodes;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getIndicatorCodes() {
			return indicatorCodes;
		}

		public void setIndicatorCodes(List<String> indicatorCodes) {
			this.indicatorCodes = indicatorCodes;
		}

		public UUID getGroupId() {
			return groupId;
		}

		public void setGroupId(UUID groupId) {
			this.groupId = groupId;
		}

		public String getDashboardKey() {
			return dashboardKey;
		}

		public void setDashboardKey(String dashboardKey) {
			this.dashboardKey = dashboardKey;
		}
	}
}
