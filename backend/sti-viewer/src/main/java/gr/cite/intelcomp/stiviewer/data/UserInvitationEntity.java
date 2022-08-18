package gr.cite.intelcomp.stiviewer.data;

import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_invitation")
public class UserInvitationEntity extends TenantScopedBaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    public static final String _id = "id";

    public static final String _tenantId = "tenantId";

    @Column(name = "token", length = 1000, nullable = false)
    private String token;
    public static final String _token = "token";

    @Column(name = "email", length = 200, nullable = false)
    private String email;
    public static final String _email = "email";

    @Column(name = "is_consumed", nullable = false)
    private Boolean isConsumed;
    public static final String _isConsumed = "isConsumed";

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    public static final String _expiresAt = "expiresAt";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    public static final String _createdAt = "createdAt";

    @Column(name = "updated_at", nullable = false)
    private BigInteger updatedAt;
    public static final String _updatedAt = "updatedAt";

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public BigInteger getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(BigInteger updatedAt) {
        this.updatedAt = updatedAt;
    }
}
