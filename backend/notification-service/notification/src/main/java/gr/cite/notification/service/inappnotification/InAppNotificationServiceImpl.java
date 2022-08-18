package gr.cite.notification.service.inappnotification;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.NotificationInAppTracking;
import gr.cite.notification.data.InAppNotificationEntity;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.model.deleter.InAppNotificationDeleter;
import gr.cite.notification.query.InAppNotificationQuery;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
@RequestScope
public class InAppNotificationServiceImpl implements InAppNotificationService{
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(InAppNotificationServiceImpl.class));

    private final QueryFactory queryFactory;
    private final TenantScopedEntityManager entityManager;
    private final AuthorizationService authService;
    private final DeleterFactory deleterFactory;

    @Autowired
    public InAppNotificationServiceImpl(QueryFactory queryFactory, TenantScopedEntityManager entityManager, AuthorizationService authService, DeleterFactory deleterFactory) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
        this.authService = authService;
        this.deleterFactory = deleterFactory;
    }

    public void markAsRead(UUID id)
    {
        this.markAsRead(List.of(id));
    }

    public void markAsRead(List<UUID> ids)
    {
        try {
            logger.debug(new DataLogEntry("marking as read in-app notifications", ids));

            List<InAppNotificationEntity> items = this.queryFactory.query(InAppNotificationQuery.class)
                    .ids(ids)
                    .collect();

            Instant now = Instant.now();
            for (InAppNotificationEntity item : items) {
                item.setTrackingState(NotificationInAppTracking.DELIVERED);
                item.setUpdatedAt(now);
                item.setReadTime(now);
                this.entityManager.merge(item);
                this.entityManager.persist(item);
            }
        } catch (InvalidApplicationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void markAsReadAllUserNotification(UUID userId)
    {
        try {
            logger.debug(new DataLogEntry("marking as read all user in-app notifications", userId));

            if (userId == null || userId.equals(new UUID(0L, 0L))) return;

            List<InAppNotificationEntity> inAppNotificationEntities = this.queryFactory.query(InAppNotificationQuery.class)
                    .userId(userId)
                    .trackingState(NotificationInAppTracking.STORED)
                    .collect();
            Instant now = Instant.now();
            int bulkSize = this.entityManager.getBulkSize();
            this.entityManager.setBulkSize(inAppNotificationEntities.size());
            for (InAppNotificationEntity entity : inAppNotificationEntities) {
                entity.setReadTime(now);
                entity.setUpdatedAt(now);
                entity.setTrackingState(NotificationInAppTracking.DELIVERED);
                this.entityManager.merge(entity);
                this.entityManager.persist(entity);
            }
            this.entityManager.setBulkSize(bulkSize);
        } catch (InvalidApplicationException e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting tenant: {}", id);
        this.authService.authorizeForce(Permission.DeleteNotification);
        this.deleterFactory.deleter(InAppNotificationDeleter.class).deleteAndSaveByIds(List.of(id));
    }
}
