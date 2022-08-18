package gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification;

import javax.management.InvalidApplicationException;

public interface NotificationIntegrationEventHandler {
	void handle(NotificationIntegrationEvent event) throws InvalidApplicationException;
}
