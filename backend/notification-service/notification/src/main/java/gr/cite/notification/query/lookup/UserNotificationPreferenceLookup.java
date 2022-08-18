package gr.cite.notification.query.lookup;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.query.TenantConfigurationQuery;
import gr.cite.notification.query.UserNotificationPreferenceQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class UserNotificationPreferenceLookup extends Lookup {
    private List<UUID> userId;
    private List<UUID> type;
    private List<NotificationContactType> channel;

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

    public List<NotificationContactType> getChannel() {
        return channel;
    }

    public void setChannel(List<NotificationContactType> channel) {
        this.channel = channel;
    }

    public UserNotificationPreferenceQuery enrich(QueryFactory queryFactory) {
        UserNotificationPreferenceQuery query = queryFactory.query(UserNotificationPreferenceQuery.class);
        if (this.userId != null) query.userId(this.userId);
        if (this.channel != null) query.channel(this.channel);
        if (this.type != null) query.type(this.type);

        this.enrichCommon(query);

        return query;
    }

}
