package gr.cite.notification.service.contact.extractor;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.service.contact.model.Contact;
import gr.cite.notification.service.contact.model.InAppContact;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InAppContactExtractor implements ContactExtractor{
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(InAppContactExtractor.class));
    @Override
    public Contact extract(NotificationEntity notification) {
        if (notification.getUserId() == null)
        {
            logger.error("Could not retrieve contact information for notification " + notification.getId());
            return null;
        }
        logger.trace("extracting from notification");
        return new InAppContact(notification.getUserId());
    }

    @Override
    public NotificationContactType supports() {
        return NotificationContactType.IN_APP;
    }
}
