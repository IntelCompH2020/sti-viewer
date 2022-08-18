package gr.cite.notification.query.lookup;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationInAppTracking;
import gr.cite.notification.query.InAppNotificationQuery;
import gr.cite.notification.query.NotificationQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class InAppNotificationLookup extends Lookup {
    private Collection<UUID> ids;
    private List<UUID> excludeIds;
    private List<UUID> userId;
    private List<UUID> type;
    private Collection<IsActive> isActive;
    private List<UUID> tenantIds;
    private List<NotificationInAppTracking> trackingState;
    private Boolean isRead;
    private Instant from;
    private Instant to;

    public Collection<UUID> getIds() {
        return ids;
    }

    public void setIds(Collection<UUID> ids) {
        this.ids = ids;
    }

    public List<UUID> getExcludeIds() {
        return excludeIds;
    }

    public void setExcludeIds(List<UUID> excludeIds) {
        this.excludeIds = excludeIds;
    }

    public List<UUID> getUserId() {
        return userId;
    }

    public void setUserId(List<UUID> userId) {
        this.userId = userId;
    }

    public List<UUID> getType() {
        return type;
    }

    public void setType(List<UUID> type) {
        this.type = type;
    }

    public Collection<IsActive> getIsActive() {
        return isActive;
    }

    public void setIsActive(Collection<IsActive> isActive) {
        this.isActive = isActive;
    }

    public List<UUID> getTenantIds() {
        return tenantIds;
    }

    public void setTenantIds(List<UUID> tenantIds) {
        this.tenantIds = tenantIds;
    }

    public List<NotificationInAppTracking> getTrackingState() {
        return trackingState;
    }

    public void setTrackingState(List<NotificationInAppTracking> trackingState) {
        this.trackingState = trackingState;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public InAppNotificationQuery enrich(QueryFactory queryFactory) {
        InAppNotificationQuery query = queryFactory.query(InAppNotificationQuery.class);
        if (this.isActive != null) query.isActive(this.isActive);
        if (this.ids != null) query.ids(this.ids);
        if (this.type != null) query.type(this.type);
        if (this.isRead != null) query.isRead(this.isRead);
        if (this.excludeIds != null) query.excludeIds(this.excludeIds);
        if (this.to != null) query.to(this.to);
        if (this.trackingState != null) query.trackingState(this.trackingState);
        if (this.userId != null) query.userId(this.userId);

        this.enrichCommon(query);

        return query;
    }

}
