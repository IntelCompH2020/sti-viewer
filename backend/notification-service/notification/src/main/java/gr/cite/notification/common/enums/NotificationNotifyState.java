package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum NotificationNotifyState {

    PENDING(0),
    PROCESSING(1),
    SUCCESSFUL(2),
    ERROR(3),
    OMITTED(4);
    private static final Map<Integer, NotificationNotifyState> values = new HashMap<>();

    private final Integer mappedName;

    //For jackson parsing (used by MVC)
    @JsonValue
    public Integer getMappedName() {
        return mappedName;
    }

    static {
        for (NotificationNotifyState e : values()) {
            values.put(e.asInt(), e);
        }
    }

    private NotificationNotifyState(int mappedName) {
        this.mappedName = mappedName;
    }

    public Integer asInt() {
        return this.mappedName;
    }

    public static NotificationNotifyState fromString(Integer value) {
        return values.getOrDefault(value, PENDING);
    }

}
