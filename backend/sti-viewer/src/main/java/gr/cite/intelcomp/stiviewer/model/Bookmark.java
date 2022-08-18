package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.BookmarkType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class Bookmark {
	public final static String _id = "id";
	private UUID id;

	public final static String _user = "user";
	private User User;

	public final static String _name = "name";
	private String name;

	public final static String _hashCode = "hashCode";
	private String hashCode;

	public final static String _value = "value";
	private String value;

	public final static String _isActive = "isActive";
	private IsActive isActive;

	public final static String _type = "type";
	private BookmarkType type;

	public final static String _createdAt = "createdAt";
	private Instant createdAt;

	public final static String _updatedAt = "updatedAt";
	private Instant updatedAt;

	public final static String _hash = "hash";
	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}


	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
	}

	public gr.cite.intelcomp.stiviewer.model.User getUser() {
		return User;
	}

	public void setUser(gr.cite.intelcomp.stiviewer.model.User user) {
		User = user;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
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

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}
}
