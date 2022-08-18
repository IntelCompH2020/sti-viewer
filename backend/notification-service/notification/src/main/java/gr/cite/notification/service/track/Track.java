package gr.cite.notification.service.track;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.service.track.model.TrackerResponse;

public interface Track {

    TrackerResponse track(NotificationEntity notification);

    NotificationContactType supports();
}
