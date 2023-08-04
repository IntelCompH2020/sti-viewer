package gr.cite.intelcomp.stiviewer.model.persist.externaltoken;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ExternalTokenChangePersist {
	@NotNull(message = "{validation.empty}")
	@ValidId(message = "{validation.invalidid}")
	private UUID id;


	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
