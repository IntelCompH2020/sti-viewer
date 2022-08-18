package gr.cite.notification.web.controllers;

import gr.cite.notification.audit.AuditableAction;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.data.UserNotificationPreferenceEntity;
import gr.cite.notification.model.TenantConfiguration;
import gr.cite.notification.model.UserNotificationPreference;
import gr.cite.notification.model.builder.UserNotificationPreferenceBuilder;
import gr.cite.notification.model.censorship.UserNotificationPreferenceCensor;
import gr.cite.notification.model.persist.UserNotificationPreferencePersist;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationNotifierListPersist;
import gr.cite.notification.query.UserNotificationPreferenceQuery;
import gr.cite.notification.query.lookup.NotifierListLookup;
import gr.cite.notification.query.lookup.UserNotificationPreferenceLookup;
import gr.cite.notification.service.tenantconfiguration.TenantConfigurationService;
import gr.cite.notification.service.userNotificationPreference.UserNotificationPreferenceService;
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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(path = "api/notification/notification-preference")
public class UserNotificationPreferenceController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserNotificationPreferenceController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final UserNotificationPreferenceService userNotificationPreferenceService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public UserNotificationPreferenceController(BuilderFactory builderFactory,
												AuditService auditService,
												UserNotificationPreferenceService userNotificationPreferenceService,
												CensorFactory censorFactory,
												QueryFactory queryFactory,
												MessageSource messageSource) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.userNotificationPreferenceService = userNotificationPreferenceService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<UserNotificationPreference> query(@RequestBody UserNotificationPreferenceLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", UserNotificationPreference.class.getSimpleName());

		this.censorFactory.censor(UserNotificationPreferenceCensor.class).censor(lookup.getProject());

		UserNotificationPreferenceQuery query = lookup.enrich(this.queryFactory);
		List<UserNotificationPreferenceEntity> data = query.collectAs(lookup.getProject());
		List<UserNotificationPreference> models = this.builderFactory.builder(UserNotificationPreferenceBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.User_Notification_Preference_Query, "lookup", lookup);

		return new QueryResult<>(models, count);
	}

	@GetMapping("user/{userId}/current")
	@Transactional
	public UserNotificationPreference current(@PathVariable UUID userId, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + UserNotificationPreference.class.getSimpleName()).And("userId", userId).And("fields", fieldSet));

		this.censorFactory.censor(UserNotificationPreferenceCensor.class).censor(fieldSet);

		UserNotificationPreferenceQuery query = this.queryFactory.query(UserNotificationPreferenceQuery.class).userId(userId);
		UserNotificationPreference model = this.builderFactory.builder(UserNotificationPreferenceBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null)
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{userId, TenantConfiguration.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.User_Notification_Preference_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("userId", userId),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("notifier-list/available")
	public NotifierListConfigurationDataContainer getAvailableNotifiers(@RequestBody NotifierListLookup tenantNotifierListLookup)
	{
		logger.debug("querying available notifiers");

		NotifierListConfigurationDataContainer notifierListData = this.userNotificationPreferenceService.collectUserAvailableNotifierList(tenantNotifierListLookup.getNotificationTypes());

		this.auditService.track(AuditableAction.User_Available_Notifiers_Query, Map.of(
		"lookup", tenantNotifierListLookup
		));
		//this._auditService.TrackIdentity(AuditableAction.IdentityTracking_Action);

		return notifierListData;
	}

	@PostMapping("persist")
	@Transactional
	public List<UserNotificationPreference> persist(@RequestBody @Valid UserNotificationPreferencePersist model, FieldSet fieldSet)
	{
		logger.debug(new MapLogEntry("persisting").And("type", TenantConfigurationType.NOTIFIER_LIST).And("model", model).And("fields", fieldSet));

		List<UserNotificationPreference> persisted = this.userNotificationPreferenceService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.User_Notification_Preference_Persist, Map.of(
			"type", TenantConfigurationType.NOTIFIER_LIST,
			"model", model,
			"fields", fieldSet
				));
		//this._auditService.TrackIdentity(AuditableAction.IdentityTracking_Action);

		return persisted;
	}

/*	@DeleteMapping("{id}")
	@Transactional
	public void delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("deleting" + TenantConfiguration.class.getSimpleName()).And("id", id));

		this.tenantConfigurationService.deleteAndSave(id);

		this.auditService.track(AuditableAction.Tenant_Configuration_Delete, "id", id);

		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}*/
}
