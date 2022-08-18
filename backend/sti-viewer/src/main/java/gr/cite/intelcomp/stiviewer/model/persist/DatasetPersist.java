package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@FieldNotNullIfOtherSet(notNullField = "id", otherSetField = "hash", failOn = "hash", message = "{validation.hashempty}")
public class DatasetPersist {
	@ValidId(message = "{validation.invalidid}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	@Size(max = 250, message = "{validation.largerthanmax}")
	private String title;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String notes;

	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
