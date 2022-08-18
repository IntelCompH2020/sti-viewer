package gr.cite.notification.service.notify;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.service.contact.model.Contact;
import gr.cite.notification.service.message.model.Message;

public interface Notify {

    String notify(Contact contact, Message message);

    NotificationContactType supports();
}
