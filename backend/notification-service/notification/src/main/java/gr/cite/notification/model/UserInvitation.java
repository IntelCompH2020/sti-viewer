package gr.cite.notification.model;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

public class UserInvitation {

    private UUID id;
    public static final String _id = "id";

    private Tenant tenant;
    public static final String _tenant = "tenant";

    private String token;
    public static final String _token = "token";

    private String email;
    public static final String _email = "email";

    private Boolean isConsumed;
    public static final String _isConsumed = "isConsumed";

    private Instant expiresAt;
    public static final String _expiresAt = "expiresAt";

    private Instant createdAt;
    public static final String _createdAt = "createdAt";

    private BigInteger updatedAt;
    public static final String _updatedAt = "updatedAt";

    private String hash;
    public static final String _hash = "hash";

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
