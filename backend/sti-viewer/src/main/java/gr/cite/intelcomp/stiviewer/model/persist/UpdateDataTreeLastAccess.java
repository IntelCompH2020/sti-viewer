package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public class UpdateDataTreeLastAccess {
	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String configId;

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}
}
