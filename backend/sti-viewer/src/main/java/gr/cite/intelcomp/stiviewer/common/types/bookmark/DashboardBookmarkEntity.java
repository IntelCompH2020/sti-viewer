package gr.cite.intelcomp.stiviewer.common.types.bookmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointKeywordFilter;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardBookmarkEntity {
	private String dashboard;
	private List<IndicatorPointKeywordFilter> keywordFilters;
	private String groupHash;
	private String displayName;

	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}

	public List<IndicatorPointKeywordFilter> getKeywordFilters() {
		return keywordFilters;
	}

	public void setKeywordFilters(List<IndicatorPointKeywordFilter> keywordFilters) {
		this.keywordFilters = keywordFilters;
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

		if (keywordFilters != null || that.keywordFilters != null) {
			if (keywordFilters == null || that.keywordFilters == null) return false;
			if (keywordFilters.size() != that.keywordFilters.size()) return false;
			for (IndicatorPointKeywordFilter keywordFilter : keywordFilters) {
				boolean find = false;
				for (IndicatorPointKeywordFilter thatKeywordFilter : that.keywordFilters) {
					if (Objects.equals(keywordFilter, thatKeywordFilter)) {
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
		if (keywordFilters != null) {
			for (IndicatorPointKeywordFilter keywordFilter : keywordFilters) {
				result = 31 * result + (keywordFilter != null ? keywordFilter.hashCode() : 0);
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


