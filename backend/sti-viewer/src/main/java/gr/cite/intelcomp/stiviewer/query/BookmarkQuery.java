package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.BookmarkType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.BookmarkEntity;
import gr.cite.intelcomp.stiviewer.data.UserEntity;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.model.Bookmark;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.fieldset.BaseFieldSet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;
import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BookmarkQuery extends QueryBase<BookmarkEntity> {

	private String like;
	private String hashCode;
	private Collection<UUID> ids;
	private Collection<UUID> excludedIds;
	private Collection<UUID> userIds;
	private Collection<IsActive> isActives;
	private Collection<BookmarkType> types;
	private UserQuery userQuery;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
	private final UserScope userScope;
	private final AuthorizationService authService;
	private final QueryFactory queryFactory;

	public BookmarkQuery(
			UserScope userScope,
			AuthorizationService authService,
			QueryFactory queryFactory
	) {
		this.userScope = userScope;
		this.authService = authService;
		this.queryFactory = queryFactory;
	}

	public BookmarkQuery like(String value) {
		this.like = value;
		return this;
	}

	public BookmarkQuery hashCode(String value) {
		this.hashCode = value;
		return this;
	}

	public BookmarkQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public BookmarkQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public BookmarkQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public BookmarkQuery excludedIds(UUID value) {
		this.excludedIds = List.of(value);
		return this;
	}

	public BookmarkQuery excludedIds(UUID... value) {
		this.excludedIds = Arrays.asList(value);
		return this;
	}

	public BookmarkQuery excludedIds(Collection<UUID> values) {
		this.excludedIds = values;
		return this;
	}

	public BookmarkQuery userIds(UUID value) {
		this.userIds = List.of(value);
		return this;
	}

	public BookmarkQuery userIds(UUID... value) {
		this.userIds = Arrays.asList(value);
		return this;
	}

	public BookmarkQuery userIds(Collection<UUID> values) {
		this.userIds = values;
		return this;
	}

	public BookmarkQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public BookmarkQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public BookmarkQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public BookmarkQuery types(BookmarkType value) {
		this.types = List.of(value);
		return this;
	}

	public BookmarkQuery types(BookmarkType... value) {
		this.types = Arrays.asList(value);
		return this;
	}

	public BookmarkQuery types(Collection<BookmarkType> values) {
		this.types = values;
		return this;
	}

	public BookmarkQuery userSubQuery(UserQuery subQuery) {
		this.userQuery = subQuery;
		return this;
	}

	public BookmarkQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Class<BookmarkEntity> entityClass() {
		return BookmarkEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.excludedIds) || this.isEmpty(this.userIds) || this.isEmpty(this.types) || this.isEmpty(this.isActives) || this.isFalseQuery(this.userQuery);
	}


	@Override
	protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
		if (this.authorize.contains(AuthorizationFlags.None)) return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseBookmark)) return null;
		UUID ownerId = null;
		if (this.authorize.contains(AuthorizationFlags.Owner)) ownerId = this.userScope.getUserIdSafe();

		List<Predicate> predicates = new ArrayList<>();
		if (ownerId != null) {
			predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(BookmarkEntity._userId), ownerId));
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
			//TODO find solution for this query 
			List<UserSettingsEntity> userSettings = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Dashboard).authorize(this.authorize).like(this.like).collectAs(new BaseFieldSet(UserSettings._key));
			ArrayList<Predicate> likes = new ArrayList<>();
			likes.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(BookmarkEntity._name), this.like));
			likes.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(BookmarkEntity._value), this.like));
			if (userSettings != null && !userSettings.isEmpty()){
				for (UserSettingsEntity userSettingsEntity : userSettings){
					if (userSettingsEntity.getKey() != null && !userSettingsEntity.getKey().isBlank()) likes.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(BookmarkEntity._value), "%" + userSettingsEntity.getKey() + "%"));
				}
			}

			predicates.add(queryContext.CriteriaBuilder.or(likes.toArray(new Predicate[likes.size()])));
		}

		if (this.hashCode != null && !this.hashCode.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(BookmarkEntity._hashCode), this.hashCode));
		}
		if (this.ids != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(BookmarkEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.excludedIds != null) {
			CriteriaBuilder.In<UUID> notInClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(BookmarkEntity._id));
			for (UUID item : this.excludedIds) notInClause.value(item);
			predicates.add(notInClause.not());
		}
		if (this.userIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(BookmarkEntity._userId));
			for (UUID item : this.userIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(BookmarkEntity._isActive));
			for (IsActive item : this.isActives) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.types != null) {
			CriteriaBuilder.In<BookmarkType> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(BookmarkEntity._type));
			for (BookmarkType item : this.types) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.userQuery != null) {
			Subquery<UserEntity> subQuery = queryContext.Query.subquery(this.userQuery.entityClass());
			this.applySubQuery(this.userQuery, queryContext.CriteriaBuilder, subQuery);
			predicates.add(queryContext.CriteriaBuilder.in(queryContext.Root.get(BookmarkEntity._userId)).value(subQuery));
		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected BookmarkEntity convert(Tuple tuple, Set<String> columns) {
		BookmarkEntity item = new BookmarkEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, BookmarkEntity._id, UUID.class));
		item.setUserId(QueryBase.convertSafe(tuple, columns, BookmarkEntity._userId, UUID.class));
		item.setType(QueryBase.convertSafe(tuple, columns, BookmarkEntity._type, BookmarkType.class));
		item.setName(QueryBase.convertSafe(tuple, columns, BookmarkEntity._name, String.class));
		item.setHashCode(QueryBase.convertSafe(tuple, columns, BookmarkEntity._hashCode, String.class));
		item.setValue(QueryBase.convertSafe(tuple, columns, BookmarkEntity._value, String.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, BookmarkEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, BookmarkEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, BookmarkEntity._isActive, IsActive.class));
		return item;
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(Bookmark._id)) return BookmarkEntity._id;
		else if (item.match(Bookmark._isActive)) return BookmarkEntity._isActive;
		else if (item.match(Bookmark._type)) return BookmarkEntity._type;
		else if (item.match(Bookmark._name)) return BookmarkEntity._name;
		else if (item.match(Bookmark._hashCode)) return BookmarkEntity._hashCode;
		else if (item.match(Bookmark._value)) return BookmarkEntity._value;
		else if (item.match(Bookmark._createdAt)) return BookmarkEntity._createdAt;
		else if (item.match(Bookmark._updatedAt)) return BookmarkEntity._updatedAt;
		else if (item.match(Bookmark._hash)) return BookmarkEntity._updatedAt;
		else if (item.prefix(Bookmark._user)) return BookmarkEntity._userId;
		else return null;
	}
}
