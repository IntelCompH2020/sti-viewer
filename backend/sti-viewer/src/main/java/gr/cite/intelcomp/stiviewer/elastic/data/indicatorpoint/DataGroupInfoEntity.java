package gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class DataGroupInfoEntity {
	public static final class Fields {
		public static final String columns = "columns";
	}

	@Field(value = Fields.columns, type = FieldType.Nested)
	private List<DataGroupInfoColumnEntity> columns;

	public List<DataGroupInfoColumnEntity> getColumns() {
		return columns;
	}

	public void setColumns(List<DataGroupInfoColumnEntity> columns) {
		this.columns = columns;
	}
}
