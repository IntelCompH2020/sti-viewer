package gr.cite.notification.common.scope.tenant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tenant.multitenancy")
public class MultitenancyProperties {
    private boolean isMultitenant;

    public boolean isMultitenant() {
        return isMultitenant;
    }

    public void setIsMultitenant(boolean multitenant) {
        isMultitenant = multitenant;
    }
}
