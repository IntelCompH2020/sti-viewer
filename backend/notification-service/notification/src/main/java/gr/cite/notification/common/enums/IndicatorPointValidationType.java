package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum IndicatorPointValidationType {

    REQUIRED("required"),
    ALLOWEDVALUES("allowed_values"),
    VALUERANGE("value_range");

    private static final Map<String, IndicatorPointValidationType> values = new HashMap<>();

    private final String mappedName;

    //For jackson parsing (used by MVC)
    @JsonValue
    public java.lang.String getMappedName() {
        return mappedName;
    }

    static {
        for (IndicatorPointValidationType e : values()) {
            values.put(e.asString(), e);
        }
    }

    private IndicatorPointValidationType(String mappedName) {
        this.mappedName = mappedName;
    }

    public String asString() {
        return this.mappedName;
    }

    public static IndicatorPointValidationType fromString(String value) {
        return values.get(value);
    }
}
