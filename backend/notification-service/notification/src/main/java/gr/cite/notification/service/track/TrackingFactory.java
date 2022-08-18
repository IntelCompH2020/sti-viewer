package gr.cite.notification.service.track;

import gr.cite.notification.common.enums.NotificationContactType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TrackingFactory {

    private Map<NotificationContactType, Track> trackMap;

    public TrackingFactory(List<Track> notifies) {
        this.trackMap = notifies.stream().collect(Collectors.toMap(Track::supports, track -> track));
    }

    public Track fromContactType(NotificationContactType type) {
        return this.trackMap.getOrDefault(type, null);
    }
}
