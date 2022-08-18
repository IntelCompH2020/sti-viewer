package gr.cite.intelcomp.stiviewer.integrationevent;

import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxProperties;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.OutboxRepositoryImpl;
import gr.cite.queueoutbox.IntegrationEventContextCreator;
import gr.cite.queueoutbox.OutboxConfigurer;
import gr.cite.queueoutbox.repository.OutboxRepository;
import gr.cite.rabbitmq.IntegrationEventMessageConstants;
import gr.cite.rabbitmq.RabbitProperties;
import gr.cite.rabbitmq.broker.MessageHydrator;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties({OutboxProperties.class})
@ConditionalOnProperty(prefix = "queue.task.publisher", name = "enable", matchIfMissing = false)
public class OutboxIntegrationEventConfigurer extends OutboxConfigurer {
	private ApplicationContext applicationContext;
	private OutboxProperties outboxProperties;

	public OutboxIntegrationEventConfigurer(ApplicationContext applicationContext, OutboxProperties outboxProperties) {
		this.applicationContext = applicationContext;
		this.outboxProperties = outboxProperties;
	}

	@Bean
	public MessageHydrator messageHydrator(RabbitProperties rabbitProperties) {
		return (message, event, eventContext) -> {
			MessageProperties messageProperties = message.getMessageProperties();
			messageProperties.setAppId(rabbitProperties.getAppId());
			messageProperties.setContentEncoding(StandardCharsets.UTF_8.displayName());
			messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
			//messageProperties.setUserId(userContext.getCurrentUser().toString());
			messageProperties.setTimestamp(Date.from(Instant.now()));
			messageProperties.setMessageId(event.getMessageId().toString());

			if (eventContext != null) {
				UUID tenant = ((IntegrationEventContextImpl) eventContext).getTenant();
				if (tenant != null) {
					messageProperties.setHeader(IntegrationEventMessageConstants.TENANT, tenant);
				}
			}

			return message;
		};
	}

	@Bean
	public IntegrationEventContextCreator integrationEventContextCreator() {
		return (message) -> new IntegrationEventContextImpl();
	}

	@Bean
	public OutboxRepository outboxRepositoryCreator() {
		return new OutboxRepositoryImpl(this.applicationContext, this.outboxProperties);
	}
}


