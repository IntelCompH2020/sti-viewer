package gr.cite.intelcomp.stiviewer.common.enums.notification;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum NotificationTrackingProcess {

    PENDING(0),
    PROCESSING(1),
    COMPLETED(2),
    ERROR(3),
    OMITTED(4);
    private static final Map<Integer, NotificationTrackingProcess> values = new HashMap<>();

    private final Integer mappedName;

    //For jackson parsing (used by MVC)
    @JsonValue
    public Integer getMappedName() {
        return mappedName;
    }

    static {
        for (NotificationTrackingProcess e : values()) {
            values.put(e.asInt(), e);
        }
    }

    private NotificationTrackingProcess(int mappedName) {
        this.mappedName = mappedName;
    }

    public Integer asInt() {
        return this.mappedName;
    }

    public static NotificationTrackingProcess fromString(Integer value) {
        return values.getOrDefault(value, PENDING);
    }

}
