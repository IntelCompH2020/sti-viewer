package gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class DataGroupInfoColumnEntity {
	public static final class Fields {
		public static final String fieldCode = "field_code";
		public static final String values = "values";
	}

	@Field(value = Fields.fieldCode, type = FieldType.Keyword)
	private String fieldCode;

	@Field(value = Fields.values, type = FieldType.Keyword)
	private List<String> values;

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
}
