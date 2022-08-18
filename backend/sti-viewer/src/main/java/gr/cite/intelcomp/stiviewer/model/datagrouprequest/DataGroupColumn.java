package gr.cite.intelcomp.stiviewer.model.datagrouprequest;

import java.util.List;

public class DataGroupColumn {
	private String fieldCode;
	public static final String _fieldCode = "fieldCode";

	private List<String> values;
	public static final String _values = "values";

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
