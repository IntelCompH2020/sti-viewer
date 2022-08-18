package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum NotificationInAppTracking {
    STORED(0),
    DELIVERED(1);

    private final int value;

    NotificationInAppTracking(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    private static final Map<Integer, NotificationInAppTracking> values = new HashMap<>();

    static {
        for (NotificationInAppTracking notificationInAppTracking: NotificationInAppTracking.values()) {
            values.put(notificationInAppTracking.getValue(), notificationInAppTracking);
        }
    }

    public static NotificationInAppTracking fromValue(int value) {
        return values.getOrDefault(value, STORED);
    }
}
