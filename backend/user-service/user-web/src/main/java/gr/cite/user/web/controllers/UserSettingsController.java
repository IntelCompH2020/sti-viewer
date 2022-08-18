package gr.cite.user.web.controllers;

import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.MyValidate;
import gr.cite.user.audit.AuditableAction;
import gr.cite.user.common.scope.user.UserScope;
import gr.cite.user.model.UserSettings;
import gr.cite.user.model.censorship.UserSettingsCensor;
import gr.cite.user.model.persist.UserSettingsPersist;
import gr.cite.user.service.user.settings.UserSettingsService;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/user/user-settings")
public class UserSettingsController {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserSettingsController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final UserSettingsService settingsService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final UserScope userScope;

	@Autowired
	public UserSettingsController(
			BuilderFactory builderFactory,
			AuditService auditService,
			UserSettingsService settingsService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource,
			UserScope userScope) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.settingsService = settingsService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
		this.userScope = userScope;
	}

	@GetMapping("{key}")
	public UserSettings getUserSettings(@PathVariable("key") String key) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("retrieving" + UserSettings.class.getSimpleName()).And("key", key));
		UUID userId = this.userScope.getUserIdSafe();
		if (userId == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{"user", UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		this.censorFactory.censor(UserSettingsCensor.class).censor(this.settingsService.getModelFields(), userId);


		UserSettings settings = this.settingsService.getUserSettings(key, userId, this.settingsService.getModelFields());
		this.auditService.track(AuditableAction.User_Settings_Lookup, "lookup",
				Map.ofEntries(new AbstractMapEntry<String, Object>("key", key) {
				}));
		return settings;
	}

	@PostMapping("persist")
	@Transactional
	public UserSettings Persist(@MyValidate @RequestBody UserSettingsPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + UserSettings.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		UserSettings persisted = this.settingsService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.User_Settings_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@PostMapping("persist-all-default")
	@Transactional
	public List<UserSettings> Persist(@MyValidate @RequestBody List<UserSettingsPersist> models) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + UserSettings.class.getSimpleName()).And("models", models));

		List<UserSettings> persisted = this.settingsService.batchPersist(models, this.settingsService.getModelFields());

		this.auditService.track(AuditableAction.User_Settings_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("models", models),
				new AbstractMap.SimpleEntry<String, Object>("fields", this.settingsService.getModelFields())
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@DeleteMapping("{id}")
	@Transactional
	public UserSettings delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("deleting" + UserSettings.class.getSimpleName()).And("id", id));
		UUID userId = userScope.getUserIdSafe();
		if (userId == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{"user", UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		UserSettings deleted = this.settingsService.deleteAndSave(userId, id);
		this.auditService.track(AuditableAction.User_Settings_Delete, "id", id);

		return deleted;
	}

}
