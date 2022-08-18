package gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


public class DataAccessRequestIndicatorConfigPersist {

	@ValidId(message = "{validation.invalidid}")
	@NotNull(message = "{validation.empty}")
	private UUID id;

	private List<FilterColumnConfigPersist> filterColumns;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public List<FilterColumnConfigPersist> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<FilterColumnConfigPersist> filterColumns) {
		this.filterColumns = filterColumns;
	}
}
