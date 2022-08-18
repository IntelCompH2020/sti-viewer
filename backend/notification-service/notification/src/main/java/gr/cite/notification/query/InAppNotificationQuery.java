package gr.cite.notification.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.*;
import gr.cite.notification.common.scope.user.UserScope;
import gr.cite.notification.data.InAppNotificationEntity;
import gr.cite.notification.data.InAppNotificationEntity;
import gr.cite.notification.model.InAppNotification;
import gr.cite.notification.model.InAppNotification;
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
public class InAppNotificationQuery extends QueryBase<InAppNotificationEntity> {
	private Collection<UUID> ids;
	private List<UUID> excludeIds;
	private List<UUID> userId;
	private List<UUID> type;
	private Collection<IsActive> isActives;
	private List<UUID> tenantIds;
	private List<NotificationInAppTracking> trackingState;
	private Boolean isRead;
	private Instant from;
	private Instant to;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public InAppNotificationQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public InAppNotificationQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public InAppNotificationQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public InAppNotificationQuery excludeIds(UUID... value) {
		this.excludeIds = Arrays.asList(value);
		return this;
	}

	public InAppNotificationQuery excludeIds(List<UUID> values) {
		this.excludeIds = values;
		return this;
	}

	public InAppNotificationQuery userId(UUID... value) {
		this.userId = Arrays.asList(value);
		return this;
	}

	public InAppNotificationQuery userId(List<UUID> values) {
		this.userId = values;
		return this;
	}

	public InAppNotificationQuery type(UUID... value) {
		this.type = Arrays.asList(value);
		return this;
	}

	public InAppNotificationQuery type(List<UUID> values) {
		this.type = values;
		return this;
	}

	public InAppNotificationQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public InAppNotificationQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public InAppNotificationQuery Tenants(List<UUID> value) {
		this.tenantIds = value;
		return this;
	}

	public InAppNotificationQuery Tenants(UUID... value) {
		this.tenantIds = Arrays.asList(value);
		return this;
	}

	public InAppNotificationQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public InAppNotificationQuery trackingState(List<NotificationInAppTracking> values) {
		this.trackingState = values;
		return this;
	}

	public InAppNotificationQuery trackingState(NotificationInAppTracking... value) {
		this.trackingState = List.of(value);
		return this;
	}

	public InAppNotificationQuery isRead(Boolean isRead) {
		this.isRead = isRead;
		return this;
	}

	public InAppNotificationQuery isRead() {
		this.isRead = true;
		return this;
	}

	public InAppNotificationQuery from(Instant from) {
		this.from = from;
		return this;
	}

	public InAppNotificationQuery to(Instant to) {
		this.to = to;
		return this;
	}

	public InAppNotificationQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isNullOrEmpty(this.ids)
				&& this.isNullOrEmpty(this.isActives)
				&& this.isNullOrEmpty(this.type)
				&& this.isNullOrEmpty(this.excludeIds)
				&& this.isNullOrEmpty(this.userId)
				&& this.isNullOrEmpty(this.trackingState);
	}

	@Override
	protected Class<InAppNotificationEntity> entityClass() {
		return InAppNotificationEntity.class;
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.ids != null) {
			predicates.add(queryContext.Root.get(InAppNotificationEntity.Field.ID).in(ids));
		}

		if (this.excludeIds != null) {
			predicates.add(queryContext.CriteriaBuilder.not(queryContext.Root.get(InAppNotificationEntity.Field.ID).in(ids)));
		}

		if (this.userId != null) {
			predicates.add(queryContext.Root.get(InAppNotificationEntity.Field.USER_ID).in(userId));
		}

		if (this.isActives != null) {
			predicates.add(queryContext.Root.get(InAppNotificationEntity.Field.IS_ACTIVE).in(isActives));
		}

		if (this.tenantIds != null) {
			predicates.add(queryContext.Root.get(InAppNotificationEntity._tenantId).in(tenantIds));
		}

		if (this.type != null) {
			predicates.add(queryContext.Root.get(InAppNotificationEntity.Field.TYPE).in(this.type));
		}

		if (this.trackingState != null) {
			predicates.add(queryContext.Root.get(InAppNotificationEntity.Field.TRACKING_STATE).in(trackingState));
		}

		if (isRead != null) {
			predicates.add(isRead ? queryContext.CriteriaBuilder.isNotNull(queryContext.Root.get(InAppNotificationEntity.Field.READ_TIME)) : queryContext.CriteriaBuilder.isNull(queryContext.Root.get(InAppNotificationEntity.Field.READ_TIME)));
		}

		if (from != null) {
			predicates.add(queryContext.CriteriaBuilder.greaterThanOrEqualTo(queryContext.Root.get(InAppNotificationEntity.Field.CREATED_AT), from));
		}

		if (to != null) {
			predicates.add(queryContext.CriteriaBuilder.lessThanOrEqualTo(queryContext.Root.get(InAppNotificationEntity.Field.CREATED_AT), from));
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
		if (item.match(InAppNotification.Field.ID)) return InAppNotificationEntity.Field.ID;
		else if (item.match(InAppNotification.Field.CREATED_AT)) return InAppNotificationEntity.Field.CREATED_AT;
		else if (item.match(InAppNotification.Field.USER)) return InAppNotificationEntity.Field.USER_ID;
		else if (item.match(InAppNotification.Field.IS_ACTIVE)) return InAppNotificationEntity.Field.IS_ACTIVE;
		else if (item.match(InAppNotification.Field.READ_TIME)) return InAppNotificationEntity.Field.READ_TIME;
		else if (item.match(InAppNotification.Field.TRACKING_STATE)) return InAppNotificationEntity.Field.TRACKING_STATE;
		else if (item.match(InAppNotification.Field.BODY)) return InAppNotificationEntity.Field.BODY;
		else if (item.match(InAppNotification.Field.EXTRA_DATA)) return InAppNotificationEntity.Field.EXTRA_DATA;
		else if (item.match(InAppNotification.Field.PRIORITY)) return InAppNotificationEntity.Field.PRIORITY;
		else if (item.match(InAppNotification.Field.SUBJECT)) return InAppNotificationEntity.Field.SUBJECT;
		else if (item.match(InAppNotification.Field.TENANT)) return InAppNotificationEntity._tenantId;
		else if (item.match(InAppNotification.Field.TYPE)) return InAppNotificationEntity.Field.TYPE;
		else if (item.match(InAppNotification.Field.UPDATED_AT)) return InAppNotificationEntity.Field.UPDATED_AT;
		else return null;
	}

	@Override
	protected InAppNotificationEntity convert(Tuple tuple, Set<String> columns) {
		InAppNotificationEntity item = new InAppNotificationEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.ID, UUID.class));
		item.setType(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.TYPE, UUID.class));
		item.setUserId(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.USER_ID, UUID.class));
		item.setTenantId(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity._tenantId, UUID.class));
		item.setTrackingState(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.TRACKING_STATE, NotificationInAppTracking.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.CREATED_AT, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.UPDATED_AT, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.IS_ACTIVE, IsActive.class));
		item.setExtraData(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.EXTRA_DATA, String.class));
		item.setBody(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.BODY, String.class));
		item.setSubject(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.SUBJECT, String.class));
		item.setPriority(QueryBase.convertSafe(tuple, columns, InAppNotificationEntity.Field.PRIORITY, InAppNotificationPriority.class));
		return item;
	}
}
