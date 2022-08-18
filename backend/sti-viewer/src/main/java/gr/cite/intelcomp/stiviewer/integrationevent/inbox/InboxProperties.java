package gr.cite.intelcomp.stiviewer.integrationevent.inbox;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "queue.task.listener.options")
@ConstructorBinding
public class InboxProperties {
	@NotNull
	private final String exchange;
	@NotNull
	private final List<String> userRemovalTopic;
	@NotNull
	private final List<String> userTouchedTopic;

	public InboxProperties(
			String exchange,
			List<String> userRemovalTopic,
			List<String> userTouchedTopic) {
		this.exchange = exchange;
		this.userRemovalTopic = userRemovalTopic;
		this.userTouchedTopic = userTouchedTopic;
	}

	public List<String> getUserRemovalTopic() {
		return userRemovalTopic;
	}

	public List<String> getUserTouchedTopic() {
		return userTouchedTopic;
	}

	public String getExchange() {
		return exchange;
	}
}
