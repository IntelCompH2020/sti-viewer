package gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest;

import gr.cite.intelcomp.stiviewer.common.enums.DataGroupRequestStatus;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;


@FieldNotNullIfOtherSet(notNullField = "id", otherSetField = "hash", failOn = "hash", message = "{validation.hashempty}")
public class DataGroupRequestPersist implements Serializable {

	@ValidId(message = "{validation.invalidid}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	private DataGroupRequestStatus status;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	@Size(max = 500, message = "{validation.largerthanmax}")
	private String name;

	@Valid
	private DataGroupRequestConfigPersist config;

	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public DataGroupRequestStatus getStatus() {
		return status;
	}

	public DataGroupRequestConfigPersist getConfig() {
		return config;
	}

	public void setConfig(DataGroupRequestConfigPersist config) {
		this.config = config;
	}

	public void setStatus(DataGroupRequestStatus status) {
		this.status = status;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
