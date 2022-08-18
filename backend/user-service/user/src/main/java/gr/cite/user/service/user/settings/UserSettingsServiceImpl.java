package gr.cite.user.service.user.settings;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.user.authorization.OwnedResource;
import gr.cite.user.common.JsonHandlingService;
import gr.cite.user.common.enums.UserSettingsEntityType;
import gr.cite.user.common.enums.UserSettingsType;
import gr.cite.user.common.scope.user.UserScope;
import gr.cite.user.convention.ConventionService;
import gr.cite.user.data.TenantEntityManager;
import gr.cite.user.data.UserSettingsEntity;
import gr.cite.user.errorcode.ErrorThesaurusProperties;
import gr.cite.user.model.UserSettings;
import gr.cite.user.model.builder.UserSettingsBuilder;
import gr.cite.user.model.deleter.UserSettingsDeleter;
import gr.cite.user.model.persist.UserSettingsPersist;
import gr.cite.user.query.UserSettingsQuery;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequestScope
public class UserSettingsServiceImpl implements UserSettingsService {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserSettingsServiceImpl.class));
	private final TenantEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final QueryFactory queryFactory;
	private final UserScope userScope;
	private final JsonHandlingService jsonHandlingService;
	@Value("${spring.jpa.hibernate.jdbc.batch-size}")
	private int batchSize;

	@Autowired
	public UserSettingsServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			QueryFactory queryFactory, UserScope userScope, JsonHandlingService jsonHandlingService) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.queryFactory = queryFactory;
		this.userScope = userScope;
		this.jsonHandlingService = jsonHandlingService;
	}

	@Override
	public UserSettings getUserSettings(String key, UUID userId, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug("get user setting: {}", key);
		this.authorizationService.authorizeAtLeastOneForce(List.of(new OwnedResource(userId)));
		List<UserSettingsEntity> datas = this.queryFactory.query(UserSettingsQuery.class).entityTypes(UserSettingsEntityType.User).keys(key).entityIds(userId).collectAs(fields);
		return this.builderFactory.builder(UserSettingsBuilder.class).build(fields, datas).stream().findFirst().orElse(null);
	}

	@Override
	public UserSettings persist(UserSettingsPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting user settings").And("model", model).And("fields", fields));

		UUID userId = userScope.getUserIdSafe();
		this.authorizationService.authorizeAtLeastOneForce(List.of(new OwnedResource(userId)));

		boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		UserSettingsEntity data;
		if (isUpdate) {
			data = this.queryFactory.query(UserSettingsQuery.class).entityTypes(UserSettingsEntityType.User).ids(model.getId()).entityIds(userId).first();
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));

			//if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			data = new UserSettingsEntity();
			data.setId(UUID.randomUUID());
			data.setKey(model.getKey());
			data.setEntityId(userId);
			data.setEntityType(UserSettingsEntityType.User);
			data.setCreatedAt(Instant.now());
		}
		data.setValue(model.getValue());
		if (model.getIsDefault()) this.defaultSettingPersist(data, userId);
		data.setType(UserSettingsType.Settings);
		data.setName(model.getName());
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		return this.getUserSettings(model.getKey(), userId, BaseFieldSet.build(fields, UserSettings.UserSetting._id, UserSettings.UserSetting._key));
	}

	@Override
	public List<UserSettings> batchPersist(List<UserSettingsPersist> models, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug("will persist {} items", Optional.ofNullable(models).map(List::size).orElse(0));
		if (models == null || models.isEmpty()) return new ArrayList<>();

		for (int i = 0; i < models.size(); i++) {
			if (i > 0 && i % this.batchSize == 0) {
				logger.trace("batch size reached");
				logger.trace("Flushing");
				entityManager.flush();
				logger.trace("Clearing");
				entityManager.clear();
			}
			logger.trace("persisting item {}", models.get(i));
			logger.trace("adding item");
			this.persist(models.get(i), fields);
			logger.trace("added item");
		}

		logger.trace("batch size reached");
		logger.trace("Flushing");
		entityManager.flush();
		logger.trace("Clearing");
		entityManager.clear();

		List<UserSettings> persisted = new ArrayList<>();
		for (String key : models.stream().map(UserSettingsPersist::getKey).distinct().collect(Collectors.toList())) {
			persisted.add(this.getUserSettings(key, userScope.getUserIdSafe(), this.getModelFields()));
		}
		return persisted;
	}


	public UserSettings deleteAndSave(UUID userId, UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug("deleting user setting: {}", id);
		this.authorizationService.authorizeAtLeastOneForce(List.of(new OwnedResource(userId)));
		List<UserSettingsEntity> deletedDatas = this.deleterFactory.deleter(UserSettingsDeleter.class).deleteAndSaveById(userId, id);
		if (deletedDatas != null && !deletedDatas.isEmpty()) {
			return this.getUserSettings(deletedDatas.stream().findFirst().orElse(null).getKey(), userId, this.getModelFields());
		}
		return null;
	}

	public FieldSet getModelFields() {

		return new BaseFieldSet(
				UserSettings.UserSetting._id,
				UserSettings.UserSetting._name,
				UserSettings.UserSetting._key,
				UserSettings.UserSetting._createdAt,
				UserSettings.UserSetting._entityId,
				UserSettings.UserSetting._entityType,
				UserSettings.UserSetting._type,
				UserSettings.UserSetting._updatedAt,
				UserSettings.UserSetting._value,
				UserSettings.UserSetting._isDefault,
				UserSettings.UserSetting._hash,
				UserSettings._settings + "." + UserSettings.UserSetting._id,
				UserSettings._settings + "." + UserSettings.UserSetting._key,
				UserSettings._settings + "." + UserSettings.UserSetting._name,
				UserSettings._settings + "." + UserSettings.UserSetting._createdAt,
				UserSettings._settings + "." + UserSettings.UserSetting._entityId,
				UserSettings._settings + "." + UserSettings.UserSetting._entityType,
				UserSettings._settings + "." + UserSettings.UserSetting._type,
				UserSettings._settings + "." + UserSettings.UserSetting._updatedAt,
				UserSettings._settings + "." + UserSettings.UserSetting._value,
				UserSettings._settings + "." + UserSettings.UserSetting._isDefault,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._id,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._key,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._name,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._createdAt,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._entityId,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._entityType,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._type,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._updatedAt,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._value,
				UserSettings._defaultSetting + "." + UserSettings.UserSetting._isDefault
		);

	}

	private void defaultSettingPersist(UserSettingsEntity data, UUID userId) throws InvalidApplicationException {

		UserSettingsEntity defaultSetting = this.queryFactory.query(UserSettingsQuery.class)
				.keys(data.getKey())
				.entityIds(userId)
				.entityTypes(UserSettingsEntityType.User)
				.types(UserSettingsType.Config)
				.first();

		boolean isUpdate = defaultSetting != null;

		if (!isUpdate) {
			defaultSetting = new UserSettingsEntity();
			defaultSetting.setId(UUID.randomUUID());
			defaultSetting.setName(data.getKey());
			defaultSetting.setKey(data.getKey());
			defaultSetting.setType(UserSettingsType.Config);
			defaultSetting.setCreatedAt(Instant.now());
			defaultSetting.setEntityType(UserSettingsEntityType.User);
		}

		defaultSetting.setEntityId(userId);

		UserSettingsPersist.UserSettingsConfigPersist userSettingsConfigPersist = new UserSettingsPersist.UserSettingsConfigPersist();
		userSettingsConfigPersist.setDefaultSetting(data.getId());
		defaultSetting.setValue(this.jsonHandlingService.toJsonSafe(userSettingsConfigPersist));
		defaultSetting.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(defaultSetting);
		else this.entityManager.persist(defaultSetting);
		this.entityManager.flush();
	}
}
