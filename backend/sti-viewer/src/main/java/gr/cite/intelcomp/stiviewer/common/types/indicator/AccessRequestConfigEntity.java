package gr.cite.intelcomp.stiviewer.common.types.indicator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessRequestConfigEntity {

	private List<FilterColumnConfigEntity> filterColumns;

	public List<FilterColumnConfigEntity> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<FilterColumnConfigEntity> filterColumns) {
		this.filterColumns = filterColumns;
	}
}
