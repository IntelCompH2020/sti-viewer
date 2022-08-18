package gr.cite.notification.common.types.notification;

import gr.cite.notification.common.enums.ContactInfoType;

public class ContactPair {
	private ContactInfoType type;
	private String contact;

	public ContactPair(ContactInfoType type, String contact) {
		this.type = type;
		this.contact = contact;
	}

	public ContactPair() {
	}

	public ContactInfoType getType() {
		return type;
	}

	public void setType(ContactInfoType type) {
		this.type = type;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
}
