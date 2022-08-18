package gr.cite.user.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class User {
	public final static String _id = "id";
	private UUID id;

	public final static String _firstName = "firstName";
	private String firstName;

	public final static String _lastName = "lastName";
	private String lastName;

	public final static String _timezone = "timezone";
	private String timezone;

	public final static String _culture = "culture";
	private String culture;

	public final static String _language = "language";
	private String language;

	public final static String _subjectId = "subjectId";
	private String subjectId;

	public final static String _isActive = "isActive";
	private int isActive;

	public final static String _createdAt = "createdAt";
	private Instant createdAt;

	public final static String _updatedAt = "updatedAt";
	private Instant updatedAt;

	public final static String _hash = "hash";
	private String hash;

	public final static String _tenantUsers = "tenantUsers";
	private List<TenantUser> tenantUsers;

	public final static String _contacts = "contacts";
	private List<UserContactInfo> contacts;


	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}


	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getCulture() {
		return culture;
	}

	public void setCulture(String culture) {
		this.culture = culture;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public List<TenantUser> getTenantUsers() {
		return tenantUsers;
	}

	public void setTenantUsers(List<TenantUser> tenantUsers) {
		this.tenantUsers = tenantUsers;
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

	public List<UserContactInfo> getContacts() {
		return contacts;
	}

	public void setContacts(List<UserContactInfo> contacts) {
		this.contacts = contacts;
	}

}
