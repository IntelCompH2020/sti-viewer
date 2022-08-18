package gr.cite.notification.schedule;

import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.config.notification.NotificationProperties;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.query.NotificationQuery;
import gr.cite.notification.query.TenantQuery;
import gr.cite.notification.schedule.model.CandidateInfo;
import gr.cite.notification.schedule.model.MiniTenant;
import gr.cite.notification.service.notification.NotificationService;
import gr.cite.notification.service.notificationscheduling.NotificationSchedulingService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationScheduleTask {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(NotificationScheduleTask.class));

    private final ApplicationContext applicationContext;
    public NotificationScheduleTask(ApplicationContext applicationContext, NotificationProperties properties) {
        this.applicationContext = applicationContext;
        long intervalSeconds = properties.getTask().getProcessor().getIntervalSeconds();
        if (properties.getTask().getProcessor().getEnable() && intervalSeconds > 0) {
            logger.info("Notification task will be scheduled to run every {} seconds", intervalSeconds);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            //GK: Fixed rate is heavily unpredictable and it will not scale well on a very heavy workload
            scheduler.scheduleWithFixedDelay(this::process, 10, intervalSeconds, TimeUnit.SECONDS);
        }
    }

    public void process() {
        try {
            Instant lastCandidateNotificationCreationTimestamp = null;
            while (true) {
                CandidateInfo candidateInfo = this.candidateToNotify(lastCandidateNotificationCreationTimestamp);
                if (candidateInfo == null) break;

                lastCandidateNotificationCreationTimestamp = candidateInfo.getNotificationCreatedAt();
                Boolean shouldOmit = this.shouldOmitNotify(candidateInfo);
                if (shouldOmit) {
                    continue;
                }
                Boolean shouldAwait = this.shouldWait(candidateInfo);
                if (shouldAwait) {
                    continue;
                }
                this.notify(candidateInfo.getNotificationId());
            }

            lastCandidateNotificationCreationTimestamp = null;
            while(true) {
                CandidateInfo candidateInfo = this.candidateToTrack(lastCandidateNotificationCreationTimestamp);
                if (candidateInfo == null) break;

                lastCandidateNotificationCreationTimestamp = candidateInfo.getNotificationCreatedAt();
                Boolean shouldOmit = this.shouldOmitTracking(candidateInfo);
                if (shouldOmit) {
                    continue;
                }
                this.track(candidateInfo.getNotificationId());
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private CandidateInfo candidateToNotify(Instant lastCandidateNotificationCreationTimestamp) {
        CandidateInfo candidateInfo = null;
        try {
            NotificationSchedulingService schedulingService = this.applicationContext.getBean(NotificationSchedulingService.class);
            candidateInfo = schedulingService.candidateToNotify(lastCandidateNotificationCreationTimestamp);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return candidateInfo;
    }

    private Boolean shouldWait(CandidateInfo candidateInfo) {
        Boolean shouldWait = false;
        try {
            NotificationSchedulingService schedulingService = applicationContext.getBean(NotificationSchedulingService.class);
            shouldWait = schedulingService.shouldWait(candidateInfo);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return shouldWait;
    }

    private Boolean shouldOmitNotify(CandidateInfo candidateInfo) {
        Boolean shouldOmit = false;
        try {
            NotificationSchedulingService schedulingService = applicationContext.getBean(NotificationSchedulingService.class);
            shouldOmit = schedulingService.shouldOmitNotify(candidateInfo.getNotificationId());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return shouldOmit;
    }

    private Boolean notify(UUID notificationId) {
        Boolean success = null;
        try {
            NotificationSchedulingService schedulingService = applicationContext.getBean(NotificationSchedulingService.class);
            success = schedulingService.notify(notificationId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return success;
    }

    private CandidateInfo candidateToTrack(Instant lastCandidateNotificationCreationTimestamp) {
        CandidateInfo candidateInfo = null;
        try {
            NotificationSchedulingService schedulingService = this.applicationContext.getBean(NotificationSchedulingService.class);
            candidateInfo = schedulingService.candidateToTrack(lastCandidateNotificationCreationTimestamp);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return candidateInfo;
    }

    private Boolean shouldOmitTracking(CandidateInfo candidateInfo) {
        Boolean shouldOmit = false;
        try {
            NotificationSchedulingService schedulingService = applicationContext.getBean(NotificationSchedulingService.class);
            shouldOmit = schedulingService.shouldOmitTracking(candidateInfo.getNotificationId());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return shouldOmit;
    }

    private Boolean track(UUID notificationId) {
        Boolean success = null;
        try {
            NotificationSchedulingService schedulingService = applicationContext.getBean(NotificationSchedulingService.class);
            success = schedulingService.track(notificationId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return success;
    }
}
