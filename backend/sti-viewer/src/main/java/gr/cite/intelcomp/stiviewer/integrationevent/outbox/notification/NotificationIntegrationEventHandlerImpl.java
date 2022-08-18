package gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationContactType;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationNotifyState;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingProcess;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingState;
import gr.cite.intelcomp.stiviewer.data.UserEntity;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxService;
import gr.cite.intelcomp.stiviewer.model.persist.notification.NotificationPersist;
import gr.cite.intelcomp.stiviewer.query.UserQuery;
import gr.cite.intelcomp.stiviewer.service.notification.NotificationService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.validation.ValidationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequestScope
public class NotificationIntegrationEventHandlerImpl implements NotificationIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(NotificationIntegrationEventHandlerImpl.class));
//	private final OutboxService outboxService;
	private final NotificationService notificationService;
	private final ValidationService validationService;
	private final UserQuery userQuery;

	private final AuditService auditService;

	@Autowired
	public NotificationIntegrationEventHandlerImpl(
//			OutboxService outboxService,
			NotificationService notificationService,
			ValidationService validationService, UserQuery userQuery, AuditService auditService) {
//		this.outboxService = outboxService;
		this.notificationService = notificationService;
		this.validationService = validationService;
		this.userQuery = userQuery;
		this.auditService = auditService;
	}

	@Override
	public void handle(NotificationIntegrationEvent event) throws InvalidApplicationException {
//		OutboxIntegrationEvent message = new OutboxIntegrationEvent();
//		message.setMessageId(UUID.randomUUID());
//		message.setType(OutboxIntegrationEvent.NOTIFY);
//		message.setEvent(event);
//		this.outboxService.publish(message);
		NotificationPersist persist = new NotificationPersist();
		persist.setType(event.getNotificationType());
		persist.setUserId(event.getUserId());
		persist.setContactHint(event.getContactHint());
		persist.setContactTypeHint(event.getContactTypeHint());
		persist.setData(event.getData());
		persist.setNotifyState(NotificationNotifyState.PENDING);
		persist.setNotifiedWith(null);
		persist.setRetryCount(0);
		persist.setTrackingState(NotificationTrackingState.UNDEFINED);
		persist.setTrackingProcess(NotificationTrackingProcess.PENDING);
		persist.setTrackingData(null);
		persist.setProvenanceRef(event.getProvenanceRef());
		persist.setNotifiedAt(Instant.now());
		validationService.validateForce(persist);
		if (isNotificationConsistent(persist)) {
			notificationService.persist(persist, null);
			auditService.track(AuditableAction.Notification_Persist, "notification_event", event);
		}
	}

	private boolean isNotificationConsistent(NotificationPersist notification) {
		if (notification.getUserId() != null) {
			List<UserEntity> users = userQuery.ids(notification.getUserId()).collect();
			if (users.size() == 0) return false;
//			UserEntity user = users.get(0);
			return !(notification.getContactTypeHint() != NotificationContactType.IN_APP && (notification.getContactHint() == null || notification.getContactHint().isBlank()));
		}
		return true;
	}
}
