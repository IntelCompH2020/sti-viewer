package gr.cite.intelcomp.stiviewer.integrationevent.outbox.generatefile;

import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class GenerateFileIntegrationEventHandlerImpl implements GenerateFileIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(GenerateFileIntegrationEventHandlerImpl.class));
	private final OutboxService outboxService;

	public GenerateFileIntegrationEventHandlerImpl(
			OutboxService outboxService
	) {
		this.outboxService = outboxService;
	}

	@Override
	public void handle(GenerateFileIntegrationEvent event) {
		OutboxIntegrationEvent message = new OutboxIntegrationEvent();
		message.setMessageId(UUID.randomUUID());
		message.setType(OutboxIntegrationEvent.GENERATE_FILE);
		message.setEvent(event);
		this.outboxService.publish(message);
	}
}
