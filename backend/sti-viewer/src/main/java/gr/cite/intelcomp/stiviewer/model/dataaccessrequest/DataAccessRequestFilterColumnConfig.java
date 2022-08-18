package gr.cite.intelcomp.stiviewer.model.dataaccessrequest;

import java.util.List;

public class DataAccessRequestFilterColumnConfig {

	private String column;
	public static final String _column = "column";

	private List<String> values;
	public static final String _values = "values";

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
}
