package gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DataGroupInfoColumnPersist {

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
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
