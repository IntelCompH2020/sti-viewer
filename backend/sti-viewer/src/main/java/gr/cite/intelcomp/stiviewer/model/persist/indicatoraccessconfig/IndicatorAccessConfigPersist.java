package gr.cite.intelcomp.stiviewer.model.persist.indicatoraccessconfig;

import java.util.List;


public class IndicatorAccessConfigPersist {
	private List<FilterColumnConfigPersist> filterColumns;

	public List<FilterColumnConfigPersist> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<FilterColumnConfigPersist> filterColumns) {
		this.filterColumns = filterColumns;
	}
}
