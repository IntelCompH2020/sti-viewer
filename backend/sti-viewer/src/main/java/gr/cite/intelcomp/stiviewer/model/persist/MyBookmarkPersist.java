package gr.cite.intelcomp.stiviewer.model.persist;


import gr.cite.intelcomp.stiviewer.common.enums.BookmarkType;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@FieldNotNullIfOtherSet(notNullField = MyBookmarkPersist._id, otherSetField = MyBookmarkPersist._hash, failOn = MyBookmarkPersist._hash, message = "{validation.hashempty}")
public class MyBookmarkPersist {

	@ValidId(message = "{validation.invalidid}")
	private UUID id;
	public final static String _id = "id";

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	@Size(max = 500, message = "{validation.largerthanmax}")
	private String name;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String value;

	@NotNull(message = "{validation.empty}")
	@Enumerated(EnumType.STRING)
	private BookmarkType type;

	private String hash;
	public final static String _hash = "hash";

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public BookmarkType getType() {
		return type;
	}

	public void setType(BookmarkType type) {
		this.type = type;
	}
}
