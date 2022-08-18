package gr.cite.user.model.builder;

import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import gr.cite.user.authorization.AuthorizationFlags;
import gr.cite.user.common.JsonHandlingService;
import gr.cite.user.common.enums.UserSettingsType;
import gr.cite.user.convention.ConventionService;
import gr.cite.user.data.UserSettingsEntity;
import gr.cite.user.model.UserSettings;
import gr.cite.user.query.UserSettingsQuery;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSettingsBuilder extends BaseBuilder<UserSettings, UserSettingsEntity> {

	private final BuilderFactory builderFactory;
	private final QueryFactory queryFactory;
	private final JsonHandlingService jsonHandlingService;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public UserSettingsBuilder(ConventionService conventionService, BuilderFactory builderFactory, QueryFactory queryFactory, JsonHandlingService jsonHandlingService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(UserSettingsBuilder.class)));
		this.builderFactory = builderFactory;
		this.queryFactory = queryFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public UserSettingsBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<UserSettings> build(FieldSet fields, List<UserSettingsEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();
		if (data.stream().map(UserSettingsEntity::getKey).distinct().count() > 1)
			throw new MyValidationException("Key must be the same.");

		FieldSet defaultSettingsFields = fields.extractPrefixed(this.asPrefix(UserSettings._defaultSetting));
		Optional<UserSettings.UserSetting> defaultSetting = this.collectDefaultSettings(defaultSettingsFields, data.stream().filter(x -> UserSettingsType.Config.equals(x.getType())).collect(Collectors.toList()));
		UUID defaultSettingId = defaultSetting.orElse(new UserSettings.UserSetting()).getId();

		FieldSet settingsFields = fields.extractPrefixed(this.asPrefix(UserSettings._settings));

		List<UserSettings.UserSetting> settings = this.collectSettings(settingsFields, data, defaultSettingId).stream().filter(x -> UserSettingsType.Settings.name().equals(x.getType().name())).collect(Collectors.toList());
		List<UserSettings> models = new ArrayList<>();
		if (data.size() != 0) {
			UserSettings model = new UserSettings();
			Optional<UserSettingsEntity> firstData = data.stream().findFirst();
			if (fields.hasField(this.asIndexer(UserSettings._key)))
				model.setKey(firstData.orElse(new UserSettingsEntity()).getKey());
			if (!defaultSettingsFields.isEmpty() && defaultSetting.isPresent())
				model.setDefaultSetting(defaultSetting.get());
			if (!settingsFields.isEmpty() && !settings.isEmpty()) model.setSettings(settings);
			models.add(model);
		}
		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

	private List<UserSettings.UserSetting> collectSettings(FieldSet fields, List<UserSettingsEntity> data, UUID defaultSettingId) {
		if (fields == null || fields.isEmpty() || data.isEmpty()) return new ArrayList<>();
		this.logger.trace(new DataLogEntry("checking related {}", UserSettings._settings));

		FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(UserSettings._settings, UserSettings.UserSetting._id));
		List<UserSettings.UserSetting> userSettings = new ArrayList<>();
		for (UserSettingsEntity entity : data) {
			UserSettings.UserSetting setting = this.builderFactory.builder(UserSettingBuilder.class).build(clone, entity);
			if (setting != null) {
				setting.setDefault(entity.getId().equals(defaultSettingId));
				userSettings.add(setting);
			}

		}
		return userSettings;
	}

	private Optional<UserSettings.UserSetting> collectDefaultSettings(FieldSet fields, List<UserSettingsEntity> defaultSetting) {
		if (fields == null || defaultSetting == null || fields.isEmpty()) return Optional.empty();
		this.logger.trace("Checking related - {}", UserSettings._settings);

		UserSettings.UserSetting userSetting = null;
		if (!fields.hasOtherField(this.asIndexer(UserSettings.UserSetting._id))) {
			return Optional.empty();
		} else {
			FieldSet clone = fields.ensure(UserSettings.UserSetting._id);
			UserSettingsEntity config = defaultSetting.stream().findFirst().orElse(null);
			if (config == null) return Optional.empty();
			UserSettings.UserSettingsConfig UserSettingsConfig = this.jsonHandlingService.fromJsonSafe(UserSettings.UserSettingsConfig.class, config.getValue());

			List<UserSettingsEntity> userSettingsEntities = UserSettingsConfig.getDefaultSetting() == null ? new ArrayList<>() : this.queryFactory.query(UserSettingsQuery.class).ids(UserSettingsConfig.getDefaultSetting()).collect();
			if (userSettingsEntities != null && userSettingsEntities.size() != 0) {
				userSetting = this.builderFactory.builder(UserSettingBuilder.class).build(clone, userSettingsEntities).stream().findFirst().orElse(new UserSettings.UserSetting());
				userSetting.setDefault(true);
			}
		}

		return Optional.ofNullable(userSetting);
	}
}
