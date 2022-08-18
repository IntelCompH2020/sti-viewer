package gr.cite.notification.model;

import gr.cite.notification.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class Tenant {

    private UUID id;
    public final static String _id = "id";

    private String code;
    public final static String _code = "code";

    private String name;
    public final static String _name = "name";

    private IsActive isActive;
    public final static String _isActive = "isActive";

    private String config;
    public final static String _config = "config";

    private Instant createdAt;
    public final static String _createdAt = "createdAt";

    private Instant updatedAt;
    public final static String _updatedAt = "updatedAt";

    public final static String _hash = "hash";
    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IsActive getIsActive() {
        return isActive;
    }

    public void setIsActive(IsActive isActive) {
        this.isActive = isActive;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
