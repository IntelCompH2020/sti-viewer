package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenantremoved;

public interface TenantRemovedIntegrationEventHandler {
	void handle(TenantRemovedIntegrationEvent event);
}
