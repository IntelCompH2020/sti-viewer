package gr.cite.notification.data;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_configuration")
public class TenantConfigurationEntity extends TenantScopedBaseEntity {

    public static class Field {
        public static final String ID = "id";
        public static final String TENANT_ID = "tenantId";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String IS_ACTIVE = "isActive";
        public static final String CREATED_AT = "createdAt";
        public static final String UPDATED_AT = "updatedAt";
    }

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantConfigurationType type;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "is_active", nullable = false)
    @Enumerated(EnumType.STRING)
    private IsActive isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TenantConfigurationType getType() {
        return type;
    }

    public void setType(TenantConfigurationType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IsActive getIsActive() {
        return isActive;
    }

    public void setIsActive(IsActive isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
