package gr.cite.notification.schedule.model;

import java.util.UUID;

public class MiniTenant {
    private UUID id;
    private String tenantCode;

    public MiniTenant() {
    }

    public MiniTenant(UUID id, String tenantCode) {
        this.id = id;
        this.tenantCode = tenantCode;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
}
