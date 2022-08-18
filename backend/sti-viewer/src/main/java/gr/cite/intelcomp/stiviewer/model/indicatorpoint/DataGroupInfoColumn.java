package gr.cite.intelcomp.stiviewer.model.indicatorpoint;

import java.util.List;

public class DataGroupInfoColumn {

	public final static String _fieldCode = "field_code";
	private String fieldCode;

	public final static String _values = "values";
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
