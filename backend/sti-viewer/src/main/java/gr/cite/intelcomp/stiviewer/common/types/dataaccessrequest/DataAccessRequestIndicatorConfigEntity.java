package gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAccessRequestIndicatorConfigEntity implements Serializable {
	private UUID id;

	private List<DataAccessRequestFilterColumnEntity> filterColumns;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public List<DataAccessRequestFilterColumnEntity> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<DataAccessRequestFilterColumnEntity> filterColumns) {
		this.filterColumns = filterColumns;
	}
}


