package gr.cite.notification.service.contact.extractor;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.service.contact.model.Contact;

public interface ContactExtractor {

    Contact extract(NotificationEntity notification);

    NotificationContactType supports();
}
