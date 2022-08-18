package gr.cite.user.model.persist;

import gr.cite.user.common.enums.IsActive;
import gr.cite.user.common.enums.UserContactType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


public class UserContactInfoPersist implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID userId;

    private UUID tenantId;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "validation.empty}")
    private String value;

    @NotNull(message = "{validation.empty}")
    private UserContactType type;

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

    public UserContactType getType() {
        return type;
    }

    public void setType(UserContactType type) {
        this.type = type;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}
