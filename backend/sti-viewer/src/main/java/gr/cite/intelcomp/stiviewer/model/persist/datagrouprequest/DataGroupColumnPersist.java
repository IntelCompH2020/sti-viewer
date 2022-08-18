package gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DataGroupColumnPersist {
	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String fieldCode;

	@NotNull(message = "{validation.empty}")
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
