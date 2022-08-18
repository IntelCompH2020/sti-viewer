package gr.cite.intelcomp.stiviewer.authorization.indicatorpoint;

import java.util.List;

public class IndicatorColumnAccess {
	private String field;
	private List<String> values;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
}
