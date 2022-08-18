package gr.cite.notification.service.track;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.enums.NotificationTrackingProgress;
import gr.cite.notification.common.enums.NotificationTrackingState;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.service.track.model.TrackerResponse;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailTracker implements Track {
    private final static LoggerService logger = new LoggerService(LoggerFactory.getLogger(EmailTracker.class));

    @Override
    public TrackerResponse track(NotificationEntity notification) {
        if (notification.getTrackingProgress() != NotificationTrackingProgress.PROCESSING)
        {
            this.logger.warn("notification " + notification.getId() + " was send for tracking but it is not locked for processing");
            return null;
        }

        return new TrackerResponse(NotificationTrackingState.NA, NotificationTrackingProgress.COMPLETED, null);
    }

    @Override
    public NotificationContactType supports() {
        return NotificationContactType.EMAIL;
    }
}
