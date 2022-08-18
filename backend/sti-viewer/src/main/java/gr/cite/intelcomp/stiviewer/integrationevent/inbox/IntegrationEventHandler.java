package gr.cite.intelcomp.stiviewer.integrationevent.inbox;

public interface IntegrationEventHandler {
	EventProcessingStatus handle(IntegrationEventProperties properties, String message);
}
