package gr.cite.notification.service.notify;

import gr.cite.notification.common.enums.NotificationContactType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotifierFactory {

    private Map<NotificationContactType, Notify> notifyMap;

    public NotifierFactory(List<Notify> notifies) {
        this.notifyMap = notifies.stream().collect(Collectors.toMap(Notify::supports, notify -> notify));
    }

    public Notify fromContactType(NotificationContactType type) {
        return this.notifyMap.getOrDefault(type, null);
    }
}
