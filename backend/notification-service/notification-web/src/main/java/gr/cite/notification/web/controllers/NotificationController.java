package gr.cite.notification.web.controllers;

import gr.cite.notification.audit.AuditableAction;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.model.Notification;
import gr.cite.notification.model.Tenant;
import gr.cite.notification.model.builder.NotificationBuilder;
import gr.cite.notification.model.censorship.NotificationCensor;
import gr.cite.notification.model.persist.NotificationPersist;
import gr.cite.notification.query.NotificationQuery;
import gr.cite.notification.query.lookup.NotificationLookup;
import gr.cite.notification.service.notification.NotificationService;
import gr.cite.notification.service.tenant.TenantService;
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
@RequestMapping(path = "api/notification/notification")
public class NotificationController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(NotificationController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final TenantService tenantService;
	private final NotificationService notificationService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public NotificationController(BuilderFactory builderFactory,
								  AuditService auditService,
								  TenantService tenantService,
								  NotificationService notificationService, CensorFactory censorFactory,
								  QueryFactory queryFactory,
								  MessageSource messageSource) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.tenantService = tenantService;
		this.notificationService = notificationService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<Notification> Query(@RequestBody NotificationLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", Notification.class.getSimpleName());

		this.censorFactory.censor(NotificationCensor.class).censor(lookup.getProject());

		NotificationQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
		List<NotificationEntity> data = query.collectAs(lookup.getProject());
		List<Notification> models = this.builderFactory.builder(NotificationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Notification_Query, "lookup", lookup);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public Notification Get(@PathVariable UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + Notification.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(NotificationCensor.class).censor(fieldSet);

		NotificationQuery query = this.queryFactory.query(NotificationQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id);
		Notification model = this.builderFactory.builder(NotificationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null)
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, Notification.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Notification_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("persist")
	@Transactional
	public Notification Persist(@MyValidate @RequestBody NotificationPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + Notification.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		Notification persisted = this.notificationService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Notification_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return persisted;
	}

	@DeleteMapping("{id}")
	@Transactional
	public void Delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("deleting" + Notification.class.getSimpleName()).And("id", id));

		this.notificationService.deleteAndSave(id);

		this.auditService.track(AuditableAction.Tenant_Delete, "id", id);

		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}
}
