package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.tools.data.query.Lookup;

import java.util.List;

public class IndicatorReportLevelLookup extends Lookup {

	private String configId;

	private List<SelectedLevel> selectedLevels;

	private LevelFilters levelFilters;

	public List<SelectedLevel> getSelectedLevels() {
		return selectedLevels;
	}

	public void setSelectedLevels(List<SelectedLevel> selectedLevels) {
		this.selectedLevels = selectedLevels;
	}

	public LevelFilters getLevelFilters() {
		return levelFilters;
	}

	public void setLevelFilters(LevelFilters levelFilters) {
		this.levelFilters = levelFilters;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public static class SelectedLevel {

		private String code;

		private String value;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class LevelFilters extends Lookup {
		private String like;

		public String getLike() {
			return like;
		}

		public void setLike(String like) {
			this.like = like;
		}
	}
}
