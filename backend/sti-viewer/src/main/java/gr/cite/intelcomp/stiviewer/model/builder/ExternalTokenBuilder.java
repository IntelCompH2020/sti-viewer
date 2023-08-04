package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.ExternalTokenEntity;
import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;
import gr.cite.intelcomp.stiviewer.model.ExternalToken;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.query.TenantQuery;
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
public class ExternalTokenBuilder extends BaseBuilder<ExternalToken, ExternalTokenEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public ExternalTokenBuilder(
			ConventionService conventionService,
			QueryFactory queryFactory, BuilderFactory builderFactory) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(ExternalTokenBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
	}

	public ExternalTokenBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<ExternalToken> build(FieldSet fields, List<ExternalTokenEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || datas == null || fields.isEmpty()) return new ArrayList<>();

		List<ExternalToken> models = new ArrayList<>();

		FieldSet tenantFields = fields.extractPrefixed(this.asPrefix(ExternalToken._tenant));
		Map<UUID, Tenant> tenantItemsMap = this.collectTenants(tenantFields, datas);

		FieldSet ownerFields = fields.extractPrefixed(this.asPrefix(ExternalToken._owner));
		Map<UUID, User> ownerItemsMap = this.collectOwners(ownerFields, datas);


		for (ExternalTokenEntity d : datas) {
			ExternalToken m = new ExternalToken();
			if (fields.hasField(this.asIndexer(ExternalToken._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(ExternalToken._expiresAt))) m.setExpiresAt(d.getExpiresAt());
			if (fields.hasField(this.asIndexer(ExternalToken._name))) m.setName(d.getName());
			if (fields.hasField(this.asIndexer(ExternalToken._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(ExternalToken._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
			if (fields.hasField(this.asIndexer(ExternalToken._isActive))) m.setIsActive(d.getIsActive());
			if (fields.hasField(this.asIndexer(ExternalToken._type))) m.setType(d.getType());
			if (fields.hasField(this.asIndexer(ExternalToken._hash))) m.setHash(this.hashValue(d.getUpdatedAt()));
			if (!tenantFields.isEmpty() && tenantItemsMap != null && tenantItemsMap.containsKey(d.getTenantId())) m.setTenant(tenantItemsMap.get(d.getTenantId()));
			if (!ownerFields.isEmpty() && ownerItemsMap != null && ownerItemsMap.containsKey(d.getOwnerId())) m.setOwner(ownerItemsMap.get(d.getOwnerId()));
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

	private Map<UUID, Tenant> collectTenants(FieldSet fields, List<ExternalTokenEntity> data) throws MyApplicationException {
		if (fields.isEmpty() || data.isEmpty()) return null;
		this.logger.debug("checking related - {}", Tenant.class.getSimpleName());

		Map<UUID, Tenant> itemMap;
		if (!fields.hasOtherField(this.asIndexer(Tenant._id))) {
			itemMap = this.asEmpty(
					data.stream().map(TenantScopedBaseEntity::getTenantId).distinct().collect(Collectors.toList()),
					x -> {
						Tenant item = new Tenant();
						item.setId(x);
						return item;
					},
					Tenant::getId);
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(Tenant._id);
			TenantQuery q = this.queryFactory.query(TenantQuery.class).authorize(this.authorize).ids(data.stream().map(TenantScopedBaseEntity::getTenantId).distinct().collect(Collectors.toList()));
			itemMap = this.builderFactory.builder(TenantBuilder.class).authorize(this.authorize).asForeignKey(q, clone, Tenant::getId);
		}
		if (!fields.hasField(Tenant._id)) {
			itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toList());
		}

		return itemMap;
	}

	private Map<UUID, User> collectOwners(FieldSet fields, List<ExternalTokenEntity> data) throws MyApplicationException {
		if (fields.isEmpty() || data.isEmpty()) return null;
		this.logger.debug("checking related - {}", User.class.getSimpleName());

		Map<UUID, User> itemMap;
		if (!fields.hasOtherField(this.asIndexer(User._id))) {
			itemMap = this.asEmpty(
					data.stream().map(x -> x.getOwnerId()).distinct().collect(Collectors.toList()),
					x -> {
						User item = new User();
						item.setId(x);
						return item;
					},
					User::getId);
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(User._id);
			UserQuery q = this.queryFactory.query(UserQuery.class).authorize(this.authorize).ids(data.stream().map(x -> x.getOwnerId()).distinct().collect(Collectors.toList()));
			itemMap = this.builderFactory.builder(UserBuilder.class).authorize(this.authorize).asForeignKey(q, clone, User::getId);
		}
		if (!fields.hasField(User._id)) {
			itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toList());
		}

		return itemMap;
	}

}
