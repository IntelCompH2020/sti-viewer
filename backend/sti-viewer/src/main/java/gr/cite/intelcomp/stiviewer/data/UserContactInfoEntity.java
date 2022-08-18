package gr.cite.intelcomp.stiviewer.data;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserContactType;
import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_contact_info")
@IdClass(UserContactInfoCompositeKey.class)
public class UserContactInfoEntity extends TenantScopedBaseEntity {

    public static final String _tenantId = "tenantId";

    @Id
    private UUID userId;
    public final static String _userId = "userId";

    @Id
    @Column(name = "type", length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserContactType type;
    public final static String _type = "type";

    @Column(name = "value", nullable = false)
    private String value;
    public final static String _value = "value";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    public final static String _createdAt = "createdAt";

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    public final static String _updatedAt = "updatedAt";

    @Column(name = "is_active", length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    private IsActive isActive;
    public final static String _isActive = "isActive";

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UserContactType getType() {
        return type;
    }

    public void setType(UserContactType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public IsActive getIsActive() {
        return isActive;
    }

    public void setIsActive(IsActive isActive) {
        this.isActive = isActive;
    }
}

class UserContactInfoCompositeKey implements Serializable {

    private UUID userId;
    private String type;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
