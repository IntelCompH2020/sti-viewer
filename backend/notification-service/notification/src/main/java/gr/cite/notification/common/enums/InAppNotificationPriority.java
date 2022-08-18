package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum InAppNotificationPriority {
    LOW(-1),
    NORMAL(0),
    HIGH(1),
    EMERGENCY(2);

    private final int value;

    InAppNotificationPriority(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    private static final Map<Integer, InAppNotificationPriority> values = new HashMap<>();

    static {
        for (InAppNotificationPriority inAppNotificationPriority: InAppNotificationPriority.values()) {
            values.put(inAppNotificationPriority.getValue(), inAppNotificationPriority);
        }
    }

    public static InAppNotificationPriority fromValue(int value) {
        return values.getOrDefault(value, LOW);
    }
}
