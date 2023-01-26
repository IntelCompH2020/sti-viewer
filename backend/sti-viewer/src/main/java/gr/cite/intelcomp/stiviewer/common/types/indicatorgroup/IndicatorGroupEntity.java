package gr.cite.intelcomp.stiviewer.common.types.indicatorgroup;

import com.github.mustachejava.Code;

import java.util.List;
import java.util.UUID;

public class IndicatorGroupEntity {
	private UUID id;
	private String name;
	private String code;
	private List<UUID> indicatorIds;
	private List<FilterColumnEntity> filterColumns;

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

	public List<UUID> getIndicatorIds() {
		return indicatorIds;
	}

	public void setIndicatorIds(List<UUID> indicatorIds) {
		this.indicatorIds = indicatorIds;
	}

	public List<FilterColumnEntity> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<FilterColumnEntity> filterColumns) {
		this.filterColumns = filterColumns;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
