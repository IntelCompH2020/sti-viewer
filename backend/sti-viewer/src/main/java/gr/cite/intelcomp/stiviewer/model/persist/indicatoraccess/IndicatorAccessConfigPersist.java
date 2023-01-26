package gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class IndicatorAccessConfigPersist {
	@Valid
	private List<FilterColumnConfigPersist> globalFilterColumns;
	@Valid
	private Map<UUID, List<FilterColumnConfigPersist>> groupFilterColumns;

	public List<FilterColumnConfigPersist> getGlobalFilterColumns() {
		return globalFilterColumns;
	}

	public void setGlobalFilterColumns(List<FilterColumnConfigPersist> globalFilterColumns) {
		this.globalFilterColumns = globalFilterColumns;
	}

	public Map<UUID, List<FilterColumnConfigPersist>> getGroupFilterColumns() {
		return groupFilterColumns;
	}

	public void setGroupFilterColumns(Map<UUID, List<FilterColumnConfigPersist>> groupFilterColumns) {
		this.groupFilterColumns = groupFilterColumns;
	}
}
