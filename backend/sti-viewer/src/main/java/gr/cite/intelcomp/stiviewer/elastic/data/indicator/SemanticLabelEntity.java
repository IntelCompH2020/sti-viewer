package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class SemanticLabelEntity {
	public static final class Fields {
		public static final String label = "label";
	}

	@Field(value = Fields.label, type = FieldType.Keyword)
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
