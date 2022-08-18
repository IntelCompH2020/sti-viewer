package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserContactType;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@FieldNotNullIfOtherSet(message = "{validation.hashempty}")
public class UserContactInfoPersist {

    @ValidId(message = "{validation.invalidid}")
    private ID id;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    private String value;

    @NotNull(message = "{validation.empty}")
    @ValidId(message = "{validation.invalidid}")
    private UUID tenantId;

    private Instant createdAt;

    private Instant updatedAt;

    private IsActive isActive;

    private String hash;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public static class ID {

        @NotNull(message = "{validation.empty}")
        @ValidId(message = "{validation.invalidid}")
        private UUID userId;

        @NotNull(message = "{validation.empty}")
        private UserContactType type;

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
    }

}
