package gr.cite.notification.model.builder;

import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.UserEntity;
import gr.cite.notification.model.User;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
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
public class UserBuilder extends BaseBuilder<User, UserEntity> {

	private final BuilderFactory builderFactory;
	private final QueryFactory queryFactory;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public UserBuilder(
			ConventionService conventionService,
			BuilderFactory builderFactory,
			QueryFactory queryFactory
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(UserBuilder.class)));
		this.builderFactory = builderFactory;
		this.queryFactory = queryFactory;
	}

	public UserBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<User> build(FieldSet fields, List<UserEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		List<User> models = new ArrayList<>();

		for (UserEntity d : datas) {
			User m = new User();
			if (fields.hasField(this.asIndexer(User._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(User._hash))) m.setHash(this.hashValue(d.getUpdatedAt()));
			if (fields.hasField(this.asIndexer(User._firstName))) m.setFirstName(d.getFirstName());
			if (fields.hasField(this.asIndexer(User._lastName))) m.setLastName(d.getLastName());
			if (fields.hasField(this.asIndexer(User._timezone))) m.setTimezone(d.getTimezone());
			if (fields.hasField(this.asIndexer(User._culture))) m.setCulture(d.getCulture());
			if (fields.hasField(this.asIndexer(User._language))) m.setLanguage(d.getLanguage());
			if (fields.hasField(this.asIndexer(User._subjectId))) m.setSubjectId(d.getSubjectId());
			if (fields.hasField(this.asIndexer(User._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(User._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
			if (fields.hasField(this.asIndexer(User._isActive))) m.setIsActive(d.getIsActive());
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.ofNullable(models).map(e -> e.size()).orElse(0));
		return models;
	}
}
