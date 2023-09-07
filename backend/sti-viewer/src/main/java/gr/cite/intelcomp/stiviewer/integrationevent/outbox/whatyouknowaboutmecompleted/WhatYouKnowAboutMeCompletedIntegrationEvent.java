package gr.cite.intelcomp.stiviewer.integrationevent.outbox.whatyouknowaboutmecompleted;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.UUID;

public class WhatYouKnowAboutMeCompletedIntegrationEvent extends TrackedEvent {

    public static class InlinePayload {
        private String name;
        private String extension;
        private String mimeType;
        private String payload;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }

    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private Boolean success;
    private InlinePayload inline;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public InlinePayload getInline() {
        return inline;
    }

    public void setInline(InlinePayload inline) {
        this.inline = inline;
    }
}
