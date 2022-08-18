package gr.cite.intelcomp.stiviewer.common.types.indicatoraccessconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatorAccessConfigEntity implements Serializable {
	private List<FilterColumnConfigEntity> filterColumns;

	public List<FilterColumnConfigEntity> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<FilterColumnConfigEntity> filterColumns) {
		this.filterColumns = filterColumns;
	}
}
