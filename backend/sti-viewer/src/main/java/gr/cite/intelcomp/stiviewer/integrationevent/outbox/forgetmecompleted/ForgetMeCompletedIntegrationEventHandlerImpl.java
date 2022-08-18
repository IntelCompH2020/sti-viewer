package gr.cite.intelcomp.stiviewer.integrationevent.outbox.forgetmecompleted;

import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class ForgetMeCompletedIntegrationEventHandlerImpl implements ForgetMeCompletedIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(ForgetMeCompletedIntegrationEventHandlerImpl.class));
	private final OutboxService outboxService;

	public ForgetMeCompletedIntegrationEventHandlerImpl(
			OutboxService outboxService
	) {
		this.outboxService = outboxService;
	}

	@Override
	public void handle(ForgetMeCompletedIntegrationEvent event) {
		OutboxIntegrationEvent message = new OutboxIntegrationEvent();
		message.setMessageId(UUID.randomUUID());
		message.setType(OutboxIntegrationEvent.FORGET_ME_COMPLETED);
		message.setEvent(event);
		this.outboxService.publish(message);
	}
}
