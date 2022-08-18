package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class CoverageEntity {
	public static final class Fields {
		public static final String label = "label";
		public static final String min = "min";
		public static final String max = "max";
	}

	@Field(value = Fields.label, type = FieldType.Text)
	private String label;

	@Field(value = Fields.min, type = FieldType.Double)
	private Double min;

	@Field(value = Fields.max, type = FieldType.Double)
	private Double max;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}
}
