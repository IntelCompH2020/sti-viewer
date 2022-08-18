package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenantuserinvited;

import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class TenantUserInvitedIntegrationEventHandlerImpl implements TenantUserInvitedIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantUserInvitedIntegrationEventHandlerImpl.class));
	private final OutboxService outboxService;

	public TenantUserInvitedIntegrationEventHandlerImpl(
			OutboxService outboxService
	) {
		this.outboxService = outboxService;
	}

	@Override
	public void handle(TenantUserInvitedIntegrationEvent event) {
		OutboxIntegrationEvent message = new OutboxIntegrationEvent();
		message.setMessageId(UUID.randomUUID());
		message.setType(OutboxIntegrationEvent.TENANT_USER_INVITE);
		message.setEvent(event);
		this.outboxService.publish(message);
	}
}
