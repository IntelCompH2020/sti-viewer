package gr.cite.notification.service.message.infobuilder;

import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.service.message.model.MessageInfo;

public interface MessageInfoBuilderService {

    MessageInfo buildMessageInfo(NotificationEntity notification);
}
