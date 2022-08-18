package gr.cite.notification.web.controllers;

import gr.cite.notification.audit.AuditableAction;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.data.TenantConfigurationEntity;
import gr.cite.notification.model.TenantConfiguration;
import gr.cite.notification.model.builder.TenantConfigurationBuilder;
import gr.cite.notification.model.censorship.TenantConfigurationCensor;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationEmailClientPersist;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationNotifierListPersist;
import gr.cite.notification.query.TenantConfigurationQuery;
import gr.cite.notification.query.lookup.NotifierListLookup;
import gr.cite.notification.query.lookup.TenantConfigurationLookup;
import gr.cite.notification.service.tenant.TenantService;
import gr.cite.notification.service.tenantconfiguration.TenantConfigurationService;
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
@RequestMapping(path = "api/notification/tenant-configuration")
public class TenantConfigurationController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantConfigurationController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final TenantConfigurationService tenantConfigurationService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public TenantConfigurationController(BuilderFactory builderFactory,
										 AuditService auditService,
										 TenantConfigurationService tenantConfigurationService, CensorFactory censorFactory,
										 QueryFactory queryFactory,
										 MessageSource messageSource) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.tenantConfigurationService = tenantConfigurationService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<TenantConfiguration> query(@RequestBody TenantConfigurationLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", TenantConfiguration.class.getSimpleName());

		this.censorFactory.censor(TenantConfigurationCensor.class).censor(lookup.getProject());

		TenantConfigurationQuery query = lookup.enrich(this.queryFactory);
		List<TenantConfigurationEntity> data = query.collectAs(lookup.getProject());
		List<TenantConfiguration> models = this.builderFactory.builder(TenantConfigurationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Tenant_Configuration_Query, "lookup", lookup);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public TenantConfiguration get(@PathVariable UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + TenantConfiguration.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(TenantConfigurationCensor.class).censor(fieldSet);

		TenantConfigurationQuery query = this.queryFactory.query(TenantConfigurationQuery.class).ids(id);
		TenantConfiguration model = this.builderFactory.builder(TenantConfigurationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null)
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, TenantConfiguration.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Tenant_Configuration_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("persist/email-client")
	@Transactional
	public  TenantConfiguration persist(@RequestBody @Valid TenantConfigurationEmailClientPersist model, FieldSet fieldSet)
	{
		logger.debug(new MapLogEntry("persisting").And("type", TenantConfigurationType.EMAIL_CLIENT_CONFIGURATION).And("model", model).And("fields", fieldSet));

		TenantConfiguration persisted = this.tenantConfigurationService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Tenant_Configuration_Persist, Map.of(
				"type", TenantConfigurationType.EMAIL_CLIENT_CONFIGURATION,
				"model", model,
				"fields", fieldSet
	));
		//this._auditService.TrackIdentity(AuditableAction.IdentityTracking_Action);

		return persisted;
	}

	@PostMapping("notifier-list/available")
	public NotifierListConfigurationDataContainer getAvailableNotifiers(@RequestBody NotifierListLookup tenantNotifierListLookup)
	{
		logger.debug("querying available notifiers");

		NotifierListConfigurationDataContainer notifierListData = this.tenantConfigurationService.collectTenantAvailableNotifierList(tenantNotifierListLookup.getNotificationTypes());

		this.auditService.track(AuditableAction.Tenant_Available_Notifiers_Query, Map.of(
		"lookup", tenantNotifierListLookup
		));
		//this._auditService.TrackIdentity(AuditableAction.IdentityTracking_Action);

		return notifierListData;
	}

	@PostMapping("persist/notifier-list")
	@Transactional
	public TenantConfiguration persist(@RequestBody @Valid TenantConfigurationNotifierListPersist model, FieldSet fieldSet)
	{
		logger.debug(new MapLogEntry("persisting").And("type", TenantConfigurationType.NOTIFIER_LIST).And("model", model).And("fields", fieldSet));

		TenantConfiguration persisted = this.tenantConfigurationService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Tenant_Configuration_Persist, Map.of(
			"type", TenantConfigurationType.NOTIFIER_LIST,
			"model", model,
			"fields", fieldSet
				));
		//this._auditService.TrackIdentity(AuditableAction.IdentityTracking_Action);

		return persisted;
	}

	@DeleteMapping("{id}")
	@Transactional
	public void delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("deleting" + TenantConfiguration.class.getSimpleName()).And("id", id));

		this.tenantConfigurationService.deleteAndSave(id);

		this.auditService.track(AuditableAction.Tenant_Configuration_Delete, "id", id);

		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}
}
