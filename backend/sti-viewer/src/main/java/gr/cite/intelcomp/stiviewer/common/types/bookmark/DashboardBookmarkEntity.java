package gr.cite.intelcomp.stiviewer.common.types.bookmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardBookmarkEntity {
	private String dashboard;
	private List<DashboardBookmarkSelectedLevelEntity> levels;
	private String groupHash;
	private String displayName;

	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}

	public List<DashboardBookmarkSelectedLevelEntity> getLevels() {
		return levels;
	}

	public void setLevels(List<DashboardBookmarkSelectedLevelEntity> levels) {
		this.levels = levels;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashboardBookmarkEntity that = (DashboardBookmarkEntity) o;

		if (!Objects.equals(dashboard, that.dashboard) ||
				!Objects.equals(groupHash, that.groupHash) ||
				!Objects.equals(displayName, that.displayName)) return false;

		if (levels != null || that.levels != null) {
			if (levels == null || that.levels == null) return false;
			if (levels.size() != that.levels.size()) return false;
			for (DashboardBookmarkSelectedLevelEntity level : levels) {
				boolean find = false;
				for (DashboardBookmarkSelectedLevelEntity thatLevel : that.levels) {
					if (Objects.equals(level, thatLevel)) {
						find = true;
						break;
					}
				}
				if (!find) return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = dashboard != null ? dashboard.hashCode() : 0;
		if (levels != null) {
			for (DashboardBookmarkSelectedLevelEntity level : levels) {
				result = 31 * result + (level != null ? level.hashCode() : 0);
			}
		}
		result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
		result = 31 * result + (groupHash != null ? groupHash.hashCode() : 0);
		return result;
	}

	public String getGroupHash() {
		return groupHash;
	}

	public void setGroupHash(String groupHash) {
		this.groupHash = groupHash;
	}
}


