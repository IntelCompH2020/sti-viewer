package gr.cite.intelcomp.stiviewer.integrationevent.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "queue.task.publisher.options")
@ConstructorBinding
public class OutboxProperties {
	@NotNull
	private final String exchange;
	@NotNull
	private final String tenantTouchTopic;
	@NotNull
	private final String tenantRemovalTopic;
	@NotNull
	private final String tenantReactivationTopic;
	@NotNull
	private final String tenantUserInviteTopic;
	@NotNull
	private final String notifyTopic;
	@NotNull
	private final String forgetMeCompletedTopic;
	@NotNull
	private final String whatYouKnowAboutMeCompletedTopic;
	@NotNull
	private final String generateFileTopic;

	public OutboxProperties(String exchange,
	                        String tenantTouchTopic,
	                        String tenantRemovalTopic,
	                        String tenantReactivationTopic,
	                        String tenantUserInviteTopic,
	                        String notifyTopic,
	                        String forgetMeCompletedTopic,
	                        String whatYouKnowAboutMeCompletedTopic,
	                        String generateFileTopic
	) {
		this.exchange = exchange;
		this.tenantTouchTopic = tenantTouchTopic;
		this.tenantRemovalTopic = tenantRemovalTopic;
		this.tenantReactivationTopic = tenantReactivationTopic;
		this.tenantUserInviteTopic = tenantUserInviteTopic;
		this.notifyTopic = notifyTopic;
		this.forgetMeCompletedTopic = forgetMeCompletedTopic;
		this.whatYouKnowAboutMeCompletedTopic = whatYouKnowAboutMeCompletedTopic;
		this.generateFileTopic = generateFileTopic;
	}

	public String getExchange() {
		return exchange;
	}

	public String getTenantTouchTopic() {
		return tenantTouchTopic;
	}

	public String getTenantRemovalTopic() {
		return tenantRemovalTopic;
	}

	public String getTenantReactivationTopic() {
		return tenantReactivationTopic;
	}

	public String getTenantUserInviteTopic() {
		return tenantUserInviteTopic;
	}

	public String getNotifyTopic() {
		return notifyTopic;
	}

	public String getForgetMeCompletedTopic() {
		return forgetMeCompletedTopic;
	}

	public String getWhatYouKnowAboutMeCompletedTopic() {
		return whatYouKnowAboutMeCompletedTopic;
	}

	public String getGenerateFileTopic() {
		return generateFileTopic;
	}
}
