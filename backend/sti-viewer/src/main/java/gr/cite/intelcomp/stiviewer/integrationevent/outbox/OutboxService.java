package gr.cite.intelcomp.stiviewer.integrationevent.outbox;

public interface OutboxService {
	void publish(OutboxIntegrationEvent event);
}
