package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.model.indicatorgroup.FilterColumn;

import java.util.List;
import java.util.UUID;

public class IndicatorGroup {
	private UUID id;
	public static final String _id = "id";
	private String name;
	public static final String _name = "name";
	
	private String code;
	public static final String _code = "code";

	private List<Indicator> indicators;
	public static final String _indicators = "indicators";

	private List<FilterColumn> filterColumns;
	public static final String _filterColumns = "filterColumns";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Indicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<Indicator> indicators) {
		this.indicators = indicators;
	}

	public List<FilterColumn> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<FilterColumn> filterColumns) {
		this.filterColumns = filterColumns;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
