package gr.cite.notification.query;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.data.UserNotificationPreferenceEntity;
import gr.cite.notification.model.UserNotificationPreference;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.*;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UserNotificationPreferenceQuery extends QueryBase<UserNotificationPreferenceEntity> {
    private List<UUID> userId;
    private List<UUID> type;
    private List<NotificationContactType> channel;

    public UserNotificationPreferenceQuery userId(UUID... userId) {
        this.userId = List.of(userId);
        return this;
    }

    public UserNotificationPreferenceQuery userId(List<UUID> userId) {
        this.userId = userId;
        return this;
    }

    public UserNotificationPreferenceQuery type(UUID... type) {
        this.type = List.of(type);
        return this;
    }

    public UserNotificationPreferenceQuery type(List<UUID> type) {
        this.type = type;
        return this;
    }

    public UserNotificationPreferenceQuery channel(NotificationContactType... channel) {
        this.channel = List.of(channel);
        return this;
    }

    public UserNotificationPreferenceQuery channel(List<NotificationContactType> channel) {
        this.channel = channel;
        return this;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isNullOrEmpty(this.userId) && this.isNullOrEmpty(this.type) && this.isNullOrEmpty(this.channel);
    }

    @Override
    protected Class<UserNotificationPreferenceEntity> entityClass() {
        return UserNotificationPreferenceEntity.class;
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.userId != null) {
            predicates.add(queryContext.Root.get(UserNotificationPreferenceEntity.Field.USER_ID).in(this.userId));
        }

        if (this.type != null) {
            predicates.add(queryContext.Root.get(UserNotificationPreferenceEntity.Field.TYPE).in(this.type));
        }

        if (this.channel != null) {
            predicates.add(queryContext.Root.get(UserNotificationPreferenceEntity.Field.CHANNEL).in(this.channel));
        }

        if (predicates.size() > 0) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }

    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(UserNotificationPreference.Field.USER_ID)) return UserNotificationPreferenceEntity.Field.USER_ID;
        else if (item.match(UserNotificationPreference.Field.TENANT_ID)) return UserNotificationPreferenceEntity._tenantId;
        else if (item.match(UserNotificationPreference.Field.TYPE)) return UserNotificationPreferenceEntity.Field.TYPE;
        else if (item.match(UserNotificationPreference.Field.CHANNEL)) return UserNotificationPreferenceEntity.Field.CHANNEL;
        else if (item.match(UserNotificationPreference.Field.ORDINAL)) return UserNotificationPreferenceEntity.Field.ORDINAL;
        else if (item.match(UserNotificationPreference.Field.CREATED_AT)) return UserNotificationPreferenceEntity.Field.CREATED_AT;
        else return null;
    }

    @Override
    protected UserNotificationPreferenceEntity convert(Tuple tuple, Set<String> columns) {
        UserNotificationPreferenceEntity item = new UserNotificationPreferenceEntity();
        item.setUserId(QueryBase.convertSafe(tuple, columns, UserNotificationPreferenceEntity.Field.USER_ID, UUID.class));
        item.setChannel(QueryBase.convertSafe(tuple, columns, UserNotificationPreferenceEntity.Field.CHANNEL, NotificationContactType.class));
        item.setType(QueryBase.convertSafe(tuple, columns, UserNotificationPreferenceEntity.Field.TYPE, UUID.class));
        item.setTenantId(QueryBase.convertSafe(tuple, columns, UserNotificationPreferenceEntity._tenantId, UUID.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, UserNotificationPreferenceEntity.Field.CREATED_AT, Instant.class));
        item.setOrdinal(QueryBase.convertSafe(tuple, columns, UserNotificationPreferenceEntity.Field.ORDINAL, Integer.class));
        return item;
    }
}
