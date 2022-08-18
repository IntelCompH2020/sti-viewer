package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenanttouched;

public interface TenantTouchedIntegrationEventHandler {
	void handle(TenantTouchedIntegrationEvent event);
}
