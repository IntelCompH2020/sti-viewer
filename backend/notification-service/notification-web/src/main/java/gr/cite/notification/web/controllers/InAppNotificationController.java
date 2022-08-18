package gr.cite.notification.web.controllers;

import gr.cite.notification.audit.AuditableAction;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationInAppTracking;
import gr.cite.notification.common.scope.user.UserScope;
import gr.cite.notification.data.InAppNotificationEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.model.InAppNotification;
import gr.cite.notification.model.Notification;
import gr.cite.notification.model.builder.InAppNotificationBuilder;
import gr.cite.notification.model.censorship.InAppNotificationCensor;
import gr.cite.notification.query.InAppNotificationQuery;
import gr.cite.notification.query.lookup.InAppNotificationLookup;
import gr.cite.notification.service.inappnotification.InAppNotificationService;
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
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.*;

@RestController
@RequestMapping(path = "api/notification/inapp-notification")
public class InAppNotificationController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(InAppNotificationController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final TenantService tenantService;
	private final NotificationService notificationService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final InAppNotificationService inAppNotificationService;
	private final UserScope userScope;
	private final ErrorThesaurusProperties errors;

	@Autowired
	public InAppNotificationController(BuilderFactory builderFactory,
									   AuditService auditService,
									   TenantService tenantService,
									   NotificationService notificationService, CensorFactory censorFactory,
									   QueryFactory queryFactory,
									   MessageSource messageSource,
									   InAppNotificationService inAppNotificationService, UserScope userScope, ErrorThesaurusProperties errors) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.tenantService = tenantService;
		this.notificationService = notificationService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
		this.inAppNotificationService = inAppNotificationService;
		this.userScope = userScope;
		this.errors = errors;
	}

	@PostMapping("query")
	public QueryResult<InAppNotification> Query(@RequestBody InAppNotificationLookup lookup) throws MyApplicationException, MyForbiddenException, InvalidApplicationException {
		logger.debug("querying {}", InAppNotification.class.getSimpleName());

		this.censorFactory.censor(InAppNotificationCensor.class).censor(lookup.getProject());

		UUID userId = this.userScope.getUserId();
		if (userId == null) throw new MyForbiddenException(this.errors.getNonPersonPrincipal().getCode(), this.errors.getNonPersonPrincipal().getMessage());
		InAppNotificationQuery query = lookup.enrich(this.queryFactory).userId(userId);
		List<InAppNotificationEntity> data = query.collectAs(lookup.getProject());
		List<InAppNotification> models = this.builderFactory.builder(InAppNotificationBuilder.class).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Notification_Query, "lookup", lookup);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public InAppNotification Get(@PathVariable UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + InAppNotification.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(InAppNotificationCensor.class).censor(fieldSet);

		InAppNotificationQuery query = this.queryFactory.query(InAppNotificationQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id);
		InAppNotification model = this.builderFactory.builder(InAppNotificationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null)
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, Notification.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Notification_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("{id}/read")
	@Transactional
	public ResponseEntity<String> persist(@PathVariable UUID id)
	{
		logger.debug(new MapLogEntry("marking as read").And("id", id));

		this.inAppNotificationService.markAsRead(id);

		this.auditService.track(AuditableAction.InApp_Notification_Read, Map.of("id", id));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return ResponseEntity.ok("ok");
	}

	@PostMapping("read-all")
	@Transactional
	public ResponseEntity<String> MarkAsReadAllUserInAppNotification() throws InvalidApplicationException {
		logger.debug(new MapLogEntry("marking as read all"));

		UUID userId = this.userScope.getUserId();
		if (userId == null) throw new MyForbiddenException(this.errors.getNonPersonPrincipal().getCode(), this.errors.getNonPersonPrincipal().getMessage());

		this.inAppNotificationService.markAsReadAllUserNotification(userId);

		this.auditService.track(AuditableAction.InApp_Notification_Read_All, Map.of("userId", userId));
		return ResponseEntity.ok("ok");
	}

	@GetMapping("count-unread")
	@Transactional
	public ResponseEntity<Integer> CountUnread() throws InvalidApplicationException {
		logger.debug("count-unread");

		UUID userId = this.userScope.getUserId();
		if (userId == null) throw new MyForbiddenException(this.errors.getNonPersonPrincipal().getCode(), this.errors.getNonPersonPrincipal().getMessage());

		this.censorFactory.censor(InAppNotificationCensor.class).censor(new BaseFieldSet(InAppNotification.Field.ID));

		InAppNotificationQuery query = this.queryFactory.query(InAppNotificationQuery.class).isActive(IsActive.ACTIVE).trackingState(NotificationInAppTracking.STORED).userId(userId);
		int count = Math.toIntExact(query.count());

		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return ResponseEntity.ok(count);
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
