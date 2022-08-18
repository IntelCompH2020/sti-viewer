package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum NotificationTrackingProgress {
    PENDING(0),
    PROCESSING(1),
    COMPLETED(2),
    ERROR(3),
    OMITTED(4);

    private final int value;

    NotificationTrackingProgress(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    private static final Map<Integer, NotificationTrackingProgress> values = new HashMap<>();

    static {
        for (NotificationTrackingProgress trackingState: NotificationTrackingProgress.values()) {
            values.put(trackingState.getValue(), trackingState);
        }
    }

    public static NotificationTrackingProgress fromValue(int value) {
        return values.getOrDefault(value, PENDING);
    }
}
