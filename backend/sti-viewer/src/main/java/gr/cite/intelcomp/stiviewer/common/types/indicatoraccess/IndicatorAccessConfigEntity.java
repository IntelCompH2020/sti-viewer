package gr.cite.intelcomp.stiviewer.common.types.indicatoraccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.elasticsearch.common.inject.Guice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatorAccessConfigEntity implements Serializable {
	private List<FilterColumnConfigEntity> globalFilterColumns;
	private Map<UUID, List<FilterColumnConfigEntity>> groupFilterColumns;

	public List<FilterColumnConfigEntity> getGlobalFilterColumns() {
		return globalFilterColumns;
	}

	public void setGlobalFilterColumns(List<FilterColumnConfigEntity> globalFilterColumns) {
		this.globalFilterColumns = globalFilterColumns;
	}

	public Map<UUID, List<FilterColumnConfigEntity>> getGroupFilterColumns() {
		return groupFilterColumns;
	}

	public void setGroupFilterColumns(Map<UUID, List<FilterColumnConfigEntity>> groupFilterColumns) {
		this.groupFilterColumns = groupFilterColumns;
	}
	
	public List<FilterColumnConfigEntity> getAllFilterColumns(){
		List<FilterColumnConfigEntity> filterColumnConfigEntities = new ArrayList<>();
		if (this.globalFilterColumns != null && this.globalFilterColumns.size() > 0) {
			for (FilterColumnConfigEntity filterColumnConfigEntity : this.globalFilterColumns) {
				this.mergeFilterColumns(filterColumnConfigEntities, filterColumnConfigEntity);
			}
		}
		if (this.groupFilterColumns != null && this.groupFilterColumns.size() > 0) {
			for (List<FilterColumnConfigEntity> filterColumnConfigEntityList : this.groupFilterColumns.values()) {
				if (filterColumnConfigEntityList != null){
					for (FilterColumnConfigEntity filterColumnConfigEntity : filterColumnConfigEntityList) {
						this.mergeFilterColumns(filterColumnConfigEntities, filterColumnConfigEntity);

					}
				}
			}
		}
		return filterColumnConfigEntities.stream().filter(x -> x.getValues() != null && !x.getValues().isEmpty()).collect(Collectors.toList());
	}
	
	private List<FilterColumnConfigEntity> mergeFilterColumns(List<FilterColumnConfigEntity> filterColumnConfigEntities, FilterColumnConfigEntity filterColumnConfigEntity){
		FilterColumnConfigEntity existingColumnConfig = filterColumnConfigEntities.stream().filter(x -> x.getColumn().equals(filterColumnConfigEntity.getColumn())).findFirst().orElse(null);
		if (existingColumnConfig != null  && existingColumnConfig.getValues() == null && existingColumnConfig.getValues().isEmpty()) return filterColumnConfigEntities;

		if (existingColumnConfig == null) {
			existingColumnConfig = new FilterColumnConfigEntity();
			existingColumnConfig.setColumn(filterColumnConfigEntity.getColumn());
			existingColumnConfig.setValues(new ArrayList<>());
			filterColumnConfigEntities.add(existingColumnConfig);
		}
		if (filterColumnConfigEntity.getValues() != null && !filterColumnConfigEntity.getValues().isEmpty()) {
			existingColumnConfig.getValues().addAll(filterColumnConfigEntity.getValues());
			existingColumnConfig.setValues(existingColumnConfig.getValues().stream().distinct().collect(Collectors.toList()));
		} else {
			existingColumnConfig.setValues(null);
		}
		return filterColumnConfigEntities;
	}
}
