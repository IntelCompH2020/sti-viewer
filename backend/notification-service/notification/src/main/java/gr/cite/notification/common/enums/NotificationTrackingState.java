package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum NotificationTrackingState {
    /* *
    * Initial state
    */
    UNDEFINED(0),
    /* *
    * Final for notifiers that do not provide any kind of tracking
    */
    NA(1),
    QUEUED(2),
    SENT(3),
    DELIVERED(4),
    UNDELIVERED(5),
    FAILED(6),
    UNSENT(7);

    private final int value;

    NotificationTrackingState(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    private static final Map<Integer, NotificationTrackingState> values = new HashMap<>();

    static {
        for (NotificationTrackingState trackingState: NotificationTrackingState.values()) {
            values.put(trackingState.getValue(), trackingState);
        }
    }

    public static NotificationTrackingState fromValue(int value) {
        return values.getOrDefault(value, UNDEFINED);
    }
}
