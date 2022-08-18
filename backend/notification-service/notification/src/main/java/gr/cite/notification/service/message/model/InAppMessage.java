package gr.cite.notification.service.message.model;

import gr.cite.notification.common.enums.InAppNotificationPriority;
import gr.cite.notification.common.types.notification.FieldInfo;

import java.util.List;
import java.util.UUID;

public class InAppMessage implements Message{
    private String subject;
    private String body;
    private UUID type;
    private InAppNotificationPriority priority;
    private List<FieldInfo> ExtraData;

    public InAppMessage() {
    }

    public InAppMessage(String subject, String body, UUID type, InAppNotificationPriority priority, List<FieldInfo> extraData) {
        this.subject = subject;
        this.body = body;
        this.type = type;
        this.priority = priority;
        ExtraData = extraData;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public InAppNotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(InAppNotificationPriority priority) {
        this.priority = priority;
    }

    public List<FieldInfo> getExtraData() {
        return ExtraData;
    }

    public void setExtraData(List<FieldInfo> extraData) {
        ExtraData = extraData;
    }
}
