package gr.cite.notification.common.types.bookmark;

import java.util.List;
import java.util.Objects;

public class DashboardBookmark {
	private String dashboard;
	private List<DashboardBookmarkSelectedLevel> levels;
	private String displayName;

	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}

	public List<DashboardBookmarkSelectedLevel> getLevels() {
		return levels;
	}

	public void setLevels(List<DashboardBookmarkSelectedLevel> levels) {
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

		DashboardBookmark that = (DashboardBookmark) o;

		if (!Objects.equals(dashboard, that.dashboard) ||
				!Objects.equals(displayName, that.displayName)) return false;

		if (levels != null || that.levels != null) {
			if (levels == null || that.levels == null) return false;
			if (levels.size() != that.levels.size()) return false;
			for (DashboardBookmarkSelectedLevel level : levels) {
				boolean find = false;
				for (DashboardBookmarkSelectedLevel thatLevel : that.levels) {
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
			for (DashboardBookmarkSelectedLevel level : levels) {
				result = 31 * result + (level != null ? level.hashCode() : 0);
			}
		}
		result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
		return result;
	}
}


