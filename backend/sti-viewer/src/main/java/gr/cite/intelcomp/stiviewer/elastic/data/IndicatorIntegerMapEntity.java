package gr.cite.intelcomp.stiviewer.elastic.data;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "ic-sti-point-map")
public class IndicatorIntegerMapEntity {

	public static final class Fields {
		public static final String key = "key";
		public static final String val = "val";
	}

	@Field(value = Fields.key, type = FieldType.Keyword)
	private String key;

	@Field(value = Fields.val, type = FieldType.Integer)
	private Integer val;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getVal() {
		return val;
	}

	public void setVal(Integer val) {
		this.val = val;
	}
}

