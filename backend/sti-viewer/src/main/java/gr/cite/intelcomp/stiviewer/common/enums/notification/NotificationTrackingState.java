package gr.cite.intelcomp.stiviewer.common.enums.notification;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum NotificationTrackingState {

    UNDEFINED(0),
    NA(1),
    QUEUED(2),
    SENT(3),
    DELIVERED(4),
    UNDELIVERED(5),
    FAILED(6),
    UNSENT(7);
    private static final Map<Integer, NotificationTrackingState> values = new HashMap<>();

    private final Integer mappedName;

    //For jackson parsing (used by MVC)
    @JsonValue
    public Integer getMappedName() {
        return mappedName;
    }

    static {
        for (NotificationTrackingState e : values()) {
            values.put(e.asInt(), e);
        }
    }

    private NotificationTrackingState(int mappedName) {
        this.mappedName = mappedName;
    }

    public Integer asInt() {
        return this.mappedName;
    }

    public static NotificationTrackingState fromString(Integer value) {
        return values.getOrDefault(value, UNDEFINED);
    }

}
