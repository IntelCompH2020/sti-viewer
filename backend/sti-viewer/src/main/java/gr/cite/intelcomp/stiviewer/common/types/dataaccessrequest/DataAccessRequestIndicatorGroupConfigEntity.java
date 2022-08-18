package gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAccessRequestIndicatorGroupConfigEntity implements Serializable {
	private UUID groupId;
	private List<DataAccessRequestFilterColumnEntity> filterColumns;

	public UUID getGroupId() {
		return groupId;
	}

	public void setGroupId(UUID groupId) {
		this.groupId = groupId;
	}

	public List<DataAccessRequestFilterColumnEntity> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<DataAccessRequestFilterColumnEntity> filterColumns) {
		this.filterColumns = filterColumns;
	}
}
