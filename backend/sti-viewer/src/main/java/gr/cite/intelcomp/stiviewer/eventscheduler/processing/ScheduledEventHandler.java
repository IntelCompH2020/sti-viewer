package gr.cite.intelcomp.stiviewer.eventscheduler.processing;

import gr.cite.intelcomp.stiviewer.data.ScheduledEventEntity;

public interface ScheduledEventHandler {
	EventProcessingStatus handle(ScheduledEventEntity scheduledEvent);
}
