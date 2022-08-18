package gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class DataAccessRequestIndicatorGroupConfigPersist {

	@ValidId(message = "{validation.invalidid}")
	@NotNull(message = "{validation.empty}")
	private UUID groupId;

	private List<FilterColumnConfigPersist> filterColumns;

	public UUID getGroupId() {
		return groupId;
	}

	public void setGroupId(UUID groupId) {
		this.groupId = groupId;
	}

	public List<FilterColumnConfigPersist> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<FilterColumnConfigPersist> filterColumns) {
		this.filterColumns = filterColumns;
	}
}
