package gr.cite.notification.service.message.builder;

import gr.cite.notification.common.enums.NotificationContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequestScope
public class MessageBuilderFactory {

    private final Map<NotificationContactType, MessageBuilder> messageBuilders;

    @Autowired
    public MessageBuilderFactory(List<MessageBuilder> messageBuilders) {
        this.messageBuilders = messageBuilders.stream().collect(Collectors.toMap(MessageBuilder::supports, messageBuilder -> messageBuilder));
    }

    public MessageBuilder getFromType(NotificationContactType contactType) {
        return messageBuilders.getOrDefault(contactType, null);
    }
}
