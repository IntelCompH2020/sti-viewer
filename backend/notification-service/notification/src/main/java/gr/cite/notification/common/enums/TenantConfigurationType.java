package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum TenantConfigurationType {
    EMAIL_CLIENT_CONFIGURATION(1),
    DEFAULT_USER_LOCALE(3),
    NOTIFIER_LIST(4);

    private static final Map<Integer, TenantConfigurationType> values = new HashMap<>();

    static {
        for (TenantConfigurationType tenantConfigurationType: TenantConfigurationType.values()) {
            values.put(tenantConfigurationType.getValue(), tenantConfigurationType);
        }
    }

    private final int value;

    TenantConfigurationType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static TenantConfigurationType fromValue(int value) {
        return values.getOrDefault(value, EMAIL_CLIENT_CONFIGURATION);
    }
}
