package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class Dataset {
    public final static String _id = "id";
    private UUID id;

    public final static String _title = "title";
    private String title;

    public final static String _notes = "notes";
    private String notes;

    public final static String _createdAt = "createdAt";
    private Instant createdAt;

    public final static String _isActive = "isActive";
    private IsActive isActive;

    public final static String _updatedAt = "updatedAt";
    private Instant updatedAt;

    public final static String _hash = "hash";
    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
