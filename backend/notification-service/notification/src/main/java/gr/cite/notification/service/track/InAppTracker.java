package gr.cite.notification.service.track;

import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.common.enums.*;
import gr.cite.notification.common.types.notification.InAppTrackingData;
import gr.cite.notification.common.types.notification.TrackingTrace;
import gr.cite.notification.data.InAppNotificationEntity;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.query.InAppNotificationQuery;
import gr.cite.notification.service.track.model.TrackerResponse;
import gr.cite.notification.service.track.model.TrackingTraceData;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class InAppTracker implements Track{
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(InAppTracker.class));

    private final JsonHandlingService jsonHandlingService;
    private final QueryFactory queryFactory;

    @Autowired
    public InAppTracker(JsonHandlingService jsonHandlingService, QueryFactory queryFactory) {
        this.jsonHandlingService = jsonHandlingService;
        this.queryFactory = queryFactory;
    }

    @Override
    public TrackerResponse track(NotificationEntity notification) {
        if (notification.getTrackingProgress() != NotificationTrackingProgress.PROCESSING)
        {
            logger.warn("notification " + notification.getId() + " was send for tracking but it is not locked for processing");
            return null;
        }

        InAppTrackingData data = this.jsonHandlingService.fromJsonSafe(InAppTrackingData.class, notification.getTrackingData());
        if (data == null)
        {
            logger.warn("could not find tracking info in notification " + notification.getId());
            return null;
        }

        InAppNotificationEntity inApp = this.queryFactory.query(InAppNotificationQuery.class)
                .ids(data.getInAppNotificationId()).first();

        NotificationTrackingProgress completed = NotificationTrackingProgress.PENDING;
        NotificationTrackingState state = this.evaluateState(inApp);
        switch (state)
        {
            case DELIVERED:
            case UNDELIVERED:
            case FAILED:
            case UNSENT: { completed = NotificationTrackingProgress.COMPLETED; break; }
            case QUEUED:
            case SENT: { completed = NotificationTrackingProgress.PENDING; break; }
            //Something went wrong
            case NA:
            case UNDEFINED:
            default: { completed = NotificationTrackingProgress.ERROR; break; }
        }

        TrackingTraceData traceData = new TrackingTraceData (inApp.getTrackingState().toString(), inApp.getReadTime(), inApp.getIsActive() == IsActive.ACTIVE );
        List<TrackingTrace> traces = data.getTraces() != null ? data.getTraces() : new ArrayList<>();
        TrackingTrace lastTrace = traces.stream().max(Comparator.comparing(TrackingTrace::getAt)).orElse(null);

        Boolean hasTrackingChanges = false;
        if (lastTrace != null)
        {
            TrackingTraceData lastTrackingTraceData = this.jsonHandlingService.fromJsonSafe(TrackingTraceData.class, lastTrace.getData());
            hasTrackingChanges = lastTrackingTraceData == null || !lastTrackingTraceData.equals(traceData);
        }
        else
        {
            hasTrackingChanges = true;
        }

        if (hasTrackingChanges)
        {
            traces.add(new TrackingTrace(Instant.now(), this.jsonHandlingService.toJsonSafe(traceData)));
        }
        InAppTrackingData newData = new InAppTrackingData(data.getInAppNotificationId(), traces);

        TrackerResponse trackerResponse = new TrackerResponse(state, completed, this.jsonHandlingService.toJsonSafe(newData));

        return trackerResponse;
    }

    private NotificationTrackingState evaluateState(InAppNotificationEntity notification)
    {
        if (notification == null) return NotificationTrackingState.FAILED;
        if (notification.getIsActive() == IsActive.INACTIVE && notification.getTrackingState() == NotificationInAppTracking.STORED) return NotificationTrackingState.UNDELIVERED;

        switch (notification.getTrackingState())
        {
            case DELIVERED: return NotificationTrackingState.DELIVERED;
            case STORED: return NotificationTrackingState.SENT;
            default: return NotificationTrackingState.UNDELIVERED;
        }
    }

    @Override
    public NotificationContactType supports() {
        return NotificationContactType.IN_APP;
    }
}
