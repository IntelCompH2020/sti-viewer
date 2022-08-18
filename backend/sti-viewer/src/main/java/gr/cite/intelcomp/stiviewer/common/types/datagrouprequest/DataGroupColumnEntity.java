package gr.cite.intelcomp.stiviewer.common.types.datagrouprequest;

import java.util.List;

public class DataGroupColumnEntity {

	private String fieldCode;
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
