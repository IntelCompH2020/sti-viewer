package gr.cite.notification.query.lookup;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.enums.NotificationNotifyState;
import gr.cite.notification.query.NotificationQuery;
import gr.cite.notification.query.TenantQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationLookup extends Lookup {
    private List<IsActive> isActive;
    private List<UUID> ids;
    private List<NotificationNotifyState> notifyState = new ArrayList<>();
    private List<NotificationContactType> notifiedWith = new ArrayList<>();
    private Boolean notifiedWithHasValue;
    private Boolean notifiedAtHasValue;
    private List<UUID> type = new ArrayList<>();
    private List<NotificationContactType> contactType = new ArrayList<>();
    private Integer retryThreshold;
    private Instant createdAfter;


    public List<IsActive> getIsActive() {
        return isActive;
    }

    public void setIsActive(List<IsActive> isActive) {
        this.isActive = isActive;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public List<NotificationNotifyState> getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(List<NotificationNotifyState> notifyState) {
        this.notifyState = notifyState;
    }

    public List<NotificationContactType> getNotifiedWith() {
        return notifiedWith;
    }

    public void setNotifiedWith(List<NotificationContactType> notifiedWith) {
        this.notifiedWith = notifiedWith;
    }

    public Boolean getNotifiedWithHasValue() {
        return notifiedWithHasValue;
    }

    public void setNotifiedWithHasValue(Boolean notifiedWithHasValue) {
        this.notifiedWithHasValue = notifiedWithHasValue;
    }

    public Boolean getNotifiedAtHasValue() {
        return notifiedAtHasValue;
    }

    public void setNotifiedAtHasValue(Boolean notifiedAtHasValue) {
        this.notifiedAtHasValue = notifiedAtHasValue;
    }

    public List<UUID> getType() {
        return type;
    }

    public void setType(List<UUID> type) {
        this.type = type;
    }

    public List<NotificationContactType> getContactType() {
        return contactType;
    }

    public void setContactType(List<NotificationContactType> contactType) {
        this.contactType = contactType;
    }

    public Integer getRetryThreshold() {
        return retryThreshold;
    }

    public void setRetryThreshold(Integer retryThreshold) {
        this.retryThreshold = retryThreshold;
    }

    public Instant getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Instant createdAfter) {
        this.createdAfter = createdAfter;
    }

    public NotificationQuery enrich(QueryFactory queryFactory) {
        NotificationQuery query = queryFactory.query(NotificationQuery.class);
        if (this.isActive != null) query.isActive(this.isActive);
        if (this.ids != null) query.ids(this.ids);
        if (this.notifiedAtHasValue != null) query.notifiedAtHasValue(this.notifiedAtHasValue);
        if (this.notifyState != null) query.notifyState(this.notifyState);
        if (this.contactType != null) query.contactType(this.contactType);
        if (this.createdAfter != null) query.createdAfter(this.createdAfter);
        if (this.notifiedWithHasValue != null) query.notifiedWithHasValue(this.notifiedWithHasValue);
        if (this.notifiedWith != null) query.notifiedWith(this.notifiedWith);
        if (this.retryThreshold != null) query.retryThreshold(this.retryThreshold);
        if (this.type != null) query.type(this.type);

        this.enrichCommon(query);

        return query;
    }

}
