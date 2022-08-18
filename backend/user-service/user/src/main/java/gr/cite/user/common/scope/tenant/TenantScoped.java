package gr.cite.user.common.scope.tenant;

import java.util.UUID;

public interface TenantScoped {
    void setTenantId(UUID tenantId);
    UUID getTenantId();
}
