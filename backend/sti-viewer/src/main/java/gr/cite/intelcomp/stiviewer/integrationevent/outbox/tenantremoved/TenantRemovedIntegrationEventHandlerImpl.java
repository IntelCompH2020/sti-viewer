package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenantremoved;

import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class TenantRemovedIntegrationEventHandlerImpl implements TenantRemovedIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantRemovedIntegrationEventHandlerImpl.class));
	private final OutboxService outboxService;

	public TenantRemovedIntegrationEventHandlerImpl(
			OutboxService outboxService
	) {
		this.outboxService = outboxService;
	}

	@Override
	public void handle(TenantRemovedIntegrationEvent event) {
		OutboxIntegrationEvent message = new OutboxIntegrationEvent();
		message.setMessageId(UUID.randomUUID());
		message.setType(OutboxIntegrationEvent.TENANT_REMOVE);
		message.setEvent(event);

		//this._scope.Set(@event.Id);
		this.outboxService.publish(message);
	}
}
