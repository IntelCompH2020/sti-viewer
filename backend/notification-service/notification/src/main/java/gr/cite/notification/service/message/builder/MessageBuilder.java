package gr.cite.notification.service.message.builder;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.service.message.model.Message;

public interface MessageBuilder {
    Message buildMessage(NotificationEntity notification);

    NotificationContactType supports();
}
