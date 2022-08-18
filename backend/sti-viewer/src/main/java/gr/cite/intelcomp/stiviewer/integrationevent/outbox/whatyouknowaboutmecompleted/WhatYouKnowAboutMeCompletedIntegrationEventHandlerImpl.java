package gr.cite.intelcomp.stiviewer.integrationevent.outbox.whatyouknowaboutmecompleted;

import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class WhatYouKnowAboutMeCompletedIntegrationEventHandlerImpl implements WhatYouKnowAboutMeCompletedIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(WhatYouKnowAboutMeCompletedIntegrationEventHandlerImpl.class));
	private final OutboxService outboxService;

	public WhatYouKnowAboutMeCompletedIntegrationEventHandlerImpl(
			OutboxService outboxService
	) {
		this.outboxService = outboxService;
	}

	@Override
	public void handle(WhatYouKnowAboutMeCompletedIntegrationEvent event) {
		OutboxIntegrationEvent message = new OutboxIntegrationEvent();
		message.setMessageId(UUID.randomUUID());
		message.setType(OutboxIntegrationEvent.WHAT_YOU_KNOW_ABOUT_ME_COMPLETED);
		message.setEvent(event);
		this.outboxService.publish(message);
	}
}
