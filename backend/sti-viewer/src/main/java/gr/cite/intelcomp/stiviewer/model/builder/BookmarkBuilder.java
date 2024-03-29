package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.BookmarkEntity;
import gr.cite.intelcomp.stiviewer.model.Bookmark;
import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.query.UserQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BookmarkBuilder extends BaseBuilder<Bookmark, BookmarkEntity> {

	private final BuilderFactory builderFactory;
	private final QueryFactory queryFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public BookmarkBuilder(
			ConventionService conventionService,
			BuilderFactory builderFactory,
			QueryFactory queryFactory
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(BookmarkBuilder.class)));
		this.builderFactory = builderFactory;
		this.queryFactory = queryFactory;
	}

	public BookmarkBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<Bookmark> build(FieldSet fields, List<BookmarkEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet userFields = fields.extractPrefixed(this.asPrefix(Bookmark._user));
		Map<UUID, User> userMap = this.collectUsers(userFields, datas);

		List<Bookmark> models = new ArrayList<>();

		for (BookmarkEntity d : datas) {
			Bookmark m = new Bookmark();
			if (fields.hasField(this.asIndexer(Bookmark._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(Bookmark._hash))) m.setHash(this.hashValue(d.getUpdatedAt()));
			if (fields.hasField(this.asIndexer(Bookmark._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(Bookmark._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
			if (fields.hasField(this.asIndexer(Bookmark._isActive))) m.setIsActive(d.getIsActive());
			if (fields.hasField(this.asIndexer(Bookmark._value))) m.setValue(d.getValue());
			if (fields.hasField(this.asIndexer(Bookmark._type))) m.setType(d.getType());
			if (fields.hasField(this.asIndexer(Bookmark._name))) m.setName(d.getName());
			if (fields.hasField(this.asIndexer(Bookmark._hashCode))) m.setHashCode(d.getHashCode());
			if (!userFields.isEmpty() && userMap != null && userMap.containsKey(d.getUserId())) m.setUser(userMap.get(d.getUserId()));
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.ofNullable(models).map(e -> e.size()).orElse(0));
		return models;
	}

	private Map<UUID, User> collectUsers(FieldSet fields, List<BookmarkEntity> datas) throws MyApplicationException {
		if (fields.isEmpty() || datas.isEmpty()) return null;
		this.logger.debug("checking related - {}", User.class.getSimpleName());

		Map<UUID, User> itemMap = null;
		if (!fields.hasOtherField(this.asIndexer(User._id))) {
			itemMap = this.asEmpty(
					datas.stream().map(x -> x.getUserId()).distinct().collect(Collectors.toList()),
					x -> {
						User item = new User();
						item.setId(x);
						return item;
					},
					x -> x.getId());
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(User._id);
			UserQuery q = this.queryFactory.query(UserQuery.class).authorize(this.authorize).ids(datas.stream().map(x -> x.getUserId()).distinct().collect(Collectors.toList()));
			itemMap = this.builderFactory.builder(UserBuilder.class).authorize(this.authorize).asForeignKey(q, clone, x -> x.getId());
		}
		if (!fields.hasField(User._id)) {
			itemMap.values().stream().filter(x -> x != null).map(x -> {
				x.setId(null);
				return x;
			}).collect(Collectors.toList());
		}

		return itemMap;
	}
}
