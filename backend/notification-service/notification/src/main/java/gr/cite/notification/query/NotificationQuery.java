package gr.cite.notification.query;

import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.enums.*;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.model.Notification;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.Ordering;
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
public class NotificationQuery extends QueryBase<NotificationEntity> {
	private Collection<UUID> ids;
	private Collection<IsActive> isActives;
	private List<UUID> tenantIds;
	private List<NotificationNotifyState> notifyState;
	private List<NotificationContactType> notifiedWith;
	private Boolean notifiedWithHasValue;
	private Boolean notifiedAtHasValue;
	private List<UUID> type;
	private List<NotificationContactType> contactType;
	private Integer retryThreshold;
	private Instant createdAfter;
	private List<NotificationTrackingState> trackingState;

	private List<NotificationTrackingProgress> trackingProgress;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public NotificationQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public NotificationQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public NotificationQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public NotificationQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public NotificationQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public NotificationQuery Tenants(List<UUID> value) {
		this.tenantIds = value;
		return this;
	}

	public NotificationQuery Tenants(UUID... value) {
		this.tenantIds = Arrays.asList(value);
		return this;
	}

	public NotificationQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public NotificationQuery notifyState(NotificationNotifyState... notifyState) {
		this.notifyState = List.of(notifyState);
		return this;
	}

	public NotificationQuery notifyState(List<NotificationNotifyState> notifyState) {
		this.notifyState = notifyState;
		return this;
	}

	public NotificationQuery notifiedWith(NotificationContactType... notifiedWith) {
		this.notifiedWith = List.of(notifiedWith);
		return this;
	}

	public NotificationQuery notifiedWith(List<NotificationContactType> notifiedWith) {
		this.notifiedWith = notifiedWith;
		return this;
	}

	public NotificationQuery notifiedWithHasValue(Boolean notifiedWithHasValue) {
		this.notifiedWithHasValue = notifiedWithHasValue;
		return this;
	}

	public NotificationQuery notifiedWithHasValue() {
		this.notifiedWithHasValue = true;
		return this;
	}

	public NotificationQuery notifiedAtHasValue(Boolean notifiedAtHasValue) {
		this.notifiedAtHasValue = notifiedAtHasValue;
		return this;
	}

	public NotificationQuery notifiedAtHasValue() {
		this.notifiedAtHasValue = true;
		return this;
	}

	public NotificationQuery type(UUID... type) {
		this.type = List.of(type);
		return this;
	}

	public NotificationQuery type(List<UUID> type) {
		this.type = type;
		return this;
	}

	public NotificationQuery contactType(NotificationContactType... contactType) {
		this.contactType = List.of(contactType);
		return this;
	}

	public NotificationQuery contactType(List<NotificationContactType> contactType) {
		this.contactType = contactType;
		return this;
	}

	public NotificationQuery retryThreshold(Integer retryThreshold) {
		this.retryThreshold = retryThreshold;
		return this;
	}

	public NotificationQuery createdAfter(Instant createdAfter) {
		this.createdAfter = createdAfter;
		return this;
	}

	public NotificationQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	public NotificationQuery trackingState(NotificationTrackingState... trackingState) {
		this.trackingState = List.of(trackingState);
		return this;
	}

	public NotificationQuery trackingState(List<NotificationTrackingState> trackingState) {
		this.trackingState = trackingState;
		return this;
	}

	public NotificationQuery trackingProgress(NotificationTrackingProgress... trackingProgress) {
		this.trackingProgress = List.of(trackingProgress);
		return this;
	}

	public NotificationQuery trackingProgress(List<NotificationTrackingProgress> trackingProgress) {
		this.trackingProgress = trackingProgress;
		return this;
	}

	public NotificationQuery ordering(Ordering ordering) {
		this.setOrder(ordering);
		return this;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isNullOrEmpty(this.ids)
				&& this.isNullOrEmpty(this.isActives)
				&& this.isNullOrEmpty(this.notifyState);
	}

	@Override
	protected Class<NotificationEntity> entityClass() {
		return NotificationEntity.class;
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.ids != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._id).in(ids));
		}

		if (this.isActives != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._isActive).in(isActives));
		}

		if (this.tenantIds != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._tenantId).in(tenantIds));
		}

		if (this.notifyState != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._notifyState).in(notifyState));
		}

		if (this.notifiedWith != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._notifiedWith).in(notifiedWith));
		}

		if (notifiedWithHasValue != null) {
			Predicate hasValuePredicate = notifiedWithHasValue ? queryContext.CriteriaBuilder.isNotNull(queryContext.Root.get(NotificationEntity.Field._notifiedWith)) : queryContext.CriteriaBuilder.isNull(queryContext.Root.get(NotificationEntity.Field._notifiedWith));
			predicates.add(hasValuePredicate);
		}

		if (notifiedAtHasValue != null) {
			Predicate hasValuePredicate = notifiedAtHasValue ? queryContext.CriteriaBuilder.isNotNull(queryContext.Root.get(NotificationEntity.Field._notifiedAt)) : queryContext.CriteriaBuilder.isNull(queryContext.Root.get(NotificationEntity.Field._notifiedAt));
			predicates.add(hasValuePredicate);
		}

		if (this.type != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._type).in(this.type));
		}

		if (this.contactType != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._contactTypeHint).in(this.contactType));
		}

		if (this.retryThreshold != null) {
			predicates.add(queryContext.CriteriaBuilder.le(queryContext.Root.get(NotificationEntity.Field._retryCount), this.retryThreshold));
		}

		if (this.createdAfter != null) {
			predicates.add(queryContext.CriteriaBuilder.greaterThan(queryContext.Root.get(NotificationEntity.Field._createdAt), this.createdAfter));
		}

		if (this.trackingState != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._trackingState).in(trackingState));
		}

		if (this.trackingProgress != null) {
			predicates.add(queryContext.Root.get(NotificationEntity.Field._trackingProgress).in(trackingProgress));
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
		if (item.match(Notification._id)) return NotificationEntity.Field._id;
		else if (item.match(Notification._contactHint)) return NotificationEntity.Field._contactHint;
		else if (item.match(Notification._createdAt)) return NotificationEntity.Field._createdAt;
		else if (item.match(Notification._isActive)) return NotificationEntity.Field._isActive;
		else if (item.match(Notification._contactTypeHint)) return NotificationEntity.Field._contactTypeHint;
		else if (item.match(Notification._updatedAt)) return NotificationEntity.Field._updatedAt;
		else if (item.match(Notification._notifiedAt)) return NotificationEntity.Field._notifiedAt;
		else if (item.match(Notification._tenant)) return NotificationEntity.Field._tenantId;
		else if (item.match(Notification._user)) return NotificationEntity.Field._userId;
		else if (item.match(Notification._type)) return NotificationEntity.Field._type;
		else return null;
	}

	@Override
	protected NotificationEntity convert(Tuple tuple, Set<String> columns) {
		NotificationEntity item = new NotificationEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._id, UUID.class));
		item.setContactHint(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._contactHint, String.class));
		item.setContactTypeHint(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._contactTypeHint, NotificationContactType.class));
		item.setNotifiedAt(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._notifiedAt, Instant.class));
		item.setType(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._type, UUID.class));
		item.setUserId(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._userId, UUID.class));
		item.setTenantId(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._tenantId, UUID.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, NotificationEntity.Field._isActive, IsActive.class));
		return item;
	}
}
