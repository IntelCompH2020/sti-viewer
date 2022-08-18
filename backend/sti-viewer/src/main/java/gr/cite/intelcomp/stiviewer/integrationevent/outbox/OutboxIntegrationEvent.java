package gr.cite.intelcomp.stiviewer.integrationevent.outbox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;
import gr.cite.rabbitmq.IntegrationEvent;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboxIntegrationEvent extends IntegrationEvent {
	public static final String FORGET_ME_COMPLETED = "FORGET_ME_COMPLETED";
	public static final String NOTIFY = "NOTIFY";
	public static final String TENANT_REACTIVATE = "TENANT_REACTIVATE";
	public static final String TENANT_REMOVE = "TENANT_REMOVE";
	public static final String TENANT_TOUCH = "TENANT_TOUCH";
	public static final String TENANT_USER_INVITE = "TENANT_USER_INVITE";
	public static final String WHAT_YOU_KNOW_ABOUT_ME_COMPLETED = "WHAT_YOU_KNOW_ABOUT_ME_COMPLETED";
	public static final String GENERATE_FILE = "GENERATE_FILE";
	private TrackedEvent event;

	public TrackedEvent getEvent() {
		return event;
	}

	public void setEvent(TrackedEvent event) {
		this.event = event;
	}
}
