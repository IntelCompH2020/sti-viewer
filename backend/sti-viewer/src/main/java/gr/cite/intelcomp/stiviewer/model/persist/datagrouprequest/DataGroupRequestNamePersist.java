package gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;


public class DataGroupRequestNamePersist implements Serializable {

	@ValidId(message = "{validation.invalidid}")
	@NotNull(message = "{validation.empty}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	@Size(max = 500, message = "{validation.largerthanmax}")
	private String name;


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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
