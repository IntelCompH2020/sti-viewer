package gr.cite.notification.common.scope.tenant;

import java.util.UUID;

public interface TenantScoped {
    void setTenantId(UUID tenantId);
    UUID getTenantId();
}
