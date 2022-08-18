package gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;


public class DataAccessRequestPersist implements Serializable {

	@ValidId(message = "{validation.invalidid}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	private DataAccessRequestStatus status;

	private DataAccessRequestConfigPersist config;

	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public DataAccessRequestStatus getStatus() {
		return status;
	}

	public DataAccessRequestConfigPersist getConfig() {
		return config;
	}

	public void setConfig(DataAccessRequestConfigPersist config) {
		this.config = config;
	}

	public void setStatus(DataAccessRequestStatus status) {
		this.status = status;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
