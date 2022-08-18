package gr.cite.notification.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.UserSettingsEntityType;
import gr.cite.notification.common.enums.UserSettingsType;
import gr.cite.notification.common.scope.user.UserScope;
import gr.cite.notification.data.UserSettingsEntity;
import gr.cite.notification.model.UserSettings;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.*;

@Component
@RequestScope
public class UserSettingsQuery extends QueryBase<UserSettingsEntity> {

	private String like;
	private Collection<UUID> ids;
	private Collection<String> keys;
	private Collection<UserSettingsType> types;
	private Collection<UserSettingsEntityType> entityTypes;
	private Collection<UUID> entityIds;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	private final UserScope userScope;
	private final AuthorizationService authService;

	public UserSettingsQuery(
			UserScope userScope,
			AuthorizationService authService
	) {
		this.userScope = userScope;
		this.authService = authService;
	}

	public UserSettingsQuery like(String like) {
		this.like = like;
		return this;
	}

	public UserSettingsQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public UserSettingsQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public UserSettingsQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public UserSettingsQuery keys(String value) {
		this.keys = List.of(value);
		return this;
	}

	public UserSettingsQuery keys(String... value) {
		this.keys = Arrays.asList(value);
		return this;
	}

	public UserSettingsQuery keys(Collection<String> values) {
		this.keys = values;
		return this;
	}


	public UserSettingsQuery types(UserSettingsType value) {
		this.types = List.of(value);
		return this;
	}

	public UserSettingsQuery types(UserSettingsType... value) {
		this.types = Arrays.asList(value);
		return this;
	}

	public UserSettingsQuery types(Collection<UserSettingsType> values) {
		this.types = values;
		return this;
	}

	public UserSettingsQuery entityTypes(UserSettingsEntityType value) {
		this.entityTypes = List.of(value);
		return this;
	}

	public UserSettingsQuery entityTypes(UserSettingsEntityType... value) {
		this.entityTypes = Arrays.asList(value);
		return this;
	}

	public UserSettingsQuery entityTypes(Collection<UserSettingsEntityType> values) {
		this.entityTypes = values;
		return this;
	}

	public UserSettingsQuery entityIds(UUID value) {
		this.entityIds = List.of(value);
		return this;
	}

	public UserSettingsQuery entityIds(UUID... value) {
		this.entityIds = Arrays.asList(value);
		return this;
	}

	public UserSettingsQuery entityIds(Collection<UUID> values) {
		this.entityIds = values;
		return this;
	}

	public UserSettingsQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.keys) || this.isEmpty(this.types) || this.isEmpty(this.entityTypes) || this.isEmpty(this.entityIds);
	}

	@Override
	protected Class<UserSettingsEntity> entityClass() {
		return UserSettingsEntity.class;
	}

	@Override
	protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
		if (this.authorize.contains(AuthorizationFlags.None)) return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseUserSettings)) return null;
		UUID ownerId = null;
		if (this.authorize.contains(AuthorizationFlags.Owner)) ownerId = this.userScope.getUserIdSafe();

		List<Predicate> predicates = new ArrayList<>();
		if (ownerId != null) {
			predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(UserSettingsEntity._entityId), ownerId));
		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return queryContext.CriteriaBuilder.or(); //Creates a false query
		}
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.like != null && !this.like.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(UserSettingsEntity._key), this.like));
		}
		if (this.ids != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserSettingsEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.keys != null) {
			CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserSettingsEntity._key));
			for (String item : this.keys) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.entityIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserSettingsEntity._entityId));
			for (UUID item : this.entityIds) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.entityTypes != null) {
			CriteriaBuilder.In<UserSettingsEntityType> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserSettingsEntity._entityType));
			for (UserSettingsEntityType item : this.entityTypes) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.types != null) {
			CriteriaBuilder.In<UserSettingsType> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserSettingsEntity._type));
			for (UserSettingsType item : this.types) inClause.value(item);
			predicates.add(inClause);
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
		if (item.match(UserSettings._id)) return UserSettingsEntity._id;
		else if (item.match(UserSettings._entityId)) return UserSettingsEntity._entityId;
		else if (item.match(UserSettings._value)) return UserSettingsEntity._value;
		else if (item.match(UserSettings._entityType)) return UserSettingsEntity._entityType;
		else if (item.match(UserSettings._key)) return UserSettingsEntity._key;
		else if (item.match(UserSettings._type)) return UserSettingsEntity._type;
		else if (item.match(UserSettings._createdAt)) return UserSettingsEntity._createdAt;
		else if (item.match(UserSettings._updatedAt)) return UserSettingsEntity._updatedAt;
		else if (item.match(UserSettings._hash)) return UserSettingsEntity._updatedAt;
		else return null;
	}

	@Override
	protected UserSettingsEntity convert(Tuple tuple, Set<String> columns) {
		UserSettingsEntity item = new UserSettingsEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._id, UUID.class));
		item.setEntityId(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._entityId, UUID.class));
		item.setEntityType(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._entityType, UserSettingsEntityType.class));
		item.setKey(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._key, String.class));
		item.setValue(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._value, String.class));
		item.setType(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._type, UserSettingsType.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, UserSettingsEntity._updatedAt, Instant.class));
		return item;
	}
}
