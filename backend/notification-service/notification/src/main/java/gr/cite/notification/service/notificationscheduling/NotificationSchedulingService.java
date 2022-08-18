package gr.cite.notification.service.notificationscheduling;

import gr.cite.notification.schedule.model.CandidateInfo;
import gr.cite.notification.schedule.model.MiniTenant;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationSchedulingService {

    List<MiniTenant> getTenants();
    CandidateInfo candidateToNotify(Instant lastCandidateNotificationCreationTimestamp);
    Boolean shouldWait(CandidateInfo candidateInfo);
    Boolean shouldOmitNotify(UUID notificationId);
    Boolean notify(UUID notificationId);

    CandidateInfo candidateToTrack(Instant lastCandidateNotificationCreationTimestamp);
    Boolean shouldOmitTracking(UUID notificationId);
    Boolean track(UUID notificationId);
}
