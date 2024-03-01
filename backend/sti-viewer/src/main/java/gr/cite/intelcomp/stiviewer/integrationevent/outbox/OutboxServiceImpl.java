package gr.cite.intelcomp.stiviewer.integrationevent.outbox;

import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class OutboxServiceImpl implements OutboxService {
	private final TenantEntityManager entityManager;
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(OutboxServiceImpl.class));

	private final OutboxProperties config;
	private final JsonHandlingService jsonHandlingService;
	private final ApplicationEventPublisher eventPublisher;

	public OutboxServiceImpl(
			TenantEntityManager entityManager,
			OutboxProperties config,
			JsonHandlingService jsonHandlingService,
			ApplicationEventPublisher eventPublisher
	) {
		this.entityManager = entityManager;
		this.config = config;
		this.jsonHandlingService = jsonHandlingService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void publish(OutboxIntegrationEvent event) {
		try {
			eventPublisher.publishEvent(event);
			return;
		} catch (Exception ex) {
			logger.error(new MapLogEntry(String.format("Could not save message ", event.getMessage())).And("message", event.getMessage()).And("ex", ex));
			//Still want to skip it from processing
			return;
		}

	}
}
