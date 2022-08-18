package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenanttouched;

import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class TenantTouchedIntegrationEventHandlerImpl implements TenantTouchedIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantTouchedIntegrationEventHandlerImpl.class));
	private final OutboxService outboxService;

	public TenantTouchedIntegrationEventHandlerImpl(
			OutboxService outboxService
	) {
		this.outboxService = outboxService;
	}

	@Override
	public void handle(TenantTouchedIntegrationEvent event) {
		OutboxIntegrationEvent message = new OutboxIntegrationEvent();
		message.setMessageId(UUID.randomUUID());
		message.setType(OutboxIntegrationEvent.TENANT_TOUCH);
		message.setEvent(event);

		//this._scope.Set(@event.Id);
		this.outboxService.publish(message);
	}
}
