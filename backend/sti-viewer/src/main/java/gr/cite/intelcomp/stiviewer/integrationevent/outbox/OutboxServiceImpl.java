package gr.cite.intelcomp.stiviewer.integrationevent.outbox;

import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class OutboxServiceImpl implements OutboxService {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(OutboxServiceImpl.class));

    private final ApplicationEventPublisher eventPublisher;

    public OutboxServiceImpl(
            ApplicationEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(OutboxIntegrationEvent event) {
        try {
            eventPublisher.publishEvent(event);
        } catch (Exception ex) {
            logger.error(new MapLogEntry(String.format("Could not save message %s", event.getMessage())).And("message", event.getMessage()).And("ex", ex));
            //Still want to skip it from processing
        }

    }
}
