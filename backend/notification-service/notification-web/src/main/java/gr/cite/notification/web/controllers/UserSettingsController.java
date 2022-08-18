package gr.cite.notification.web.controllers;

import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.notification.audit.AuditableAction;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.data.UserSettingsEntity;
import gr.cite.notification.model.UserSettings;
import gr.cite.notification.model.builder.UserSettingsBuilder;
import gr.cite.notification.model.censorship.UserSettingsCensor;
import gr.cite.notification.model.persist.UserSettingsPersist;
import gr.cite.notification.query.UserSettingsQuery;
import gr.cite.notification.query.lookup.UserSettingsLookup;
import gr.cite.notification.service.user.settings.UserSettingsService;
import gr.cite.notification.web.model.QueryResult;
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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.*;

@RestController
@RequestMapping(path = "api/user-settings")
public class UserSettingsController {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserSettingsController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final UserSettingsService settingsService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final ClaimExtractor claimExtractor;

	@Autowired
	public UserSettingsController(
			BuilderFactory builderFactory,
			AuditService auditService,
			UserSettingsService settingsService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource,
			ClaimExtractor claimExtractor) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.settingsService = settingsService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
		this.claimExtractor = claimExtractor;
	}

	@PostMapping("query")
	public QueryResult<UserSettings> Query(@RequestBody UserSettingsLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", UserSettings.class.getSimpleName());
		this.censorFactory.censor(UserSettingsCensor.class).censor(lookup.getProject(), null);
		UserSettingsQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
		List<UserSettingsEntity> data = query.collectAs(lookup.getProject());
		List<UserSettings> models = this.builderFactory.builder(UserSettingsBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.User_Settings_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public UserSettings Get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + UserSettings.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(UserSettingsCensor.class).censor(fieldSet, null);

		UserSettingsQuery query = this.queryFactory.query(UserSettingsQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id);
		UserSettings model = this.builderFactory.builder(UserSettingsBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.User_Settings_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
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

}
