package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public class UserInvitationPersist {

    @ValidId(message = "{validation.invalidid}")
    private UUID id;

    @ValidId(message = "{validation.invalidid}")
    private UUID tenantId;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 1000, message = "{validation.largerthanmax}")
    private String token;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 200, message = "{validation.largerthanmax}")
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NotNull(message = "{validation.empty}")
    private Boolean isConsumed = Boolean.FALSE;

    @NotNull(message = "{validation.empty}")
    private Instant expiresAt;

    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getConsumed() {
        return isConsumed;
    }

    public void setConsumed(Boolean consumed) {
        isConsumed = consumed;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
