package gr.cite.notification.service.notify;

import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.enums.NotificationInAppTracking;
import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.common.types.notification.InAppTrackingData;
import gr.cite.notification.data.InAppNotificationEntity;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.service.contact.model.Contact;
import gr.cite.notification.service.contact.model.InAppContact;
import gr.cite.notification.service.message.model.InAppMessage;
import gr.cite.notification.service.message.model.Message;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.Instant;
import java.util.UUID;

@Component
public class InAppNotifier implements Notify{
    private final static LoggerService logger = new LoggerService(LoggerFactory.getLogger(InAppNotifier.class));

    private final JsonHandlingService jsonHandlingService;
    private final ApplicationContext applicationContext;

    @Autowired
    public InAppNotifier(JsonHandlingService jsonHandlingService, ApplicationContext applicationContext) {
        this.jsonHandlingService = jsonHandlingService;
        this.applicationContext = applicationContext;
    }

    @Override
    public String notify(Contact contact, Message message) {
        String data = null;
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            InAppContact inAppContact = (InAppContact) contact;
            if (inAppContact == null) throw new IllegalArgumentException("contact not provided");

            InAppMessage inAppMessage = (InAppMessage) message;
            if (inAppMessage == null) throw new IllegalArgumentException("message not provided");

            TenantScope tenantScope = applicationContext.getBean(TenantScope.class);
            InAppNotificationEntity inApp = new InAppNotificationEntity();

            inApp.setId(UUID.randomUUID());
            inApp.setUserId(inAppContact.getUserId());
            inApp.setIsActive(IsActive.ACTIVE);
            inApp.setType(inAppMessage.getType());
            inApp.setTrackingState(NotificationInAppTracking.STORED);
            inApp.setPriority(inAppMessage.getPriority());
            inApp.setSubject(inAppMessage.getSubject());
            inApp.setBody(inAppMessage.getBody());
            inApp.setExtraData(inAppMessage.getExtraData() != null ? this.jsonHandlingService.toJsonSafe(inAppMessage.getExtraData()) : null);
            inApp.setCreatedAt(Instant.now());
            inApp.setTenantId(tenantScope.getTenant());

            inApp = entityManager.merge(inApp);
            entityManager.persist(inApp);

            InAppTrackingData trackingData = new InAppTrackingData(inApp.getId());
            data = this.jsonHandlingService.toJsonSafe(trackingData);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return data;
    }

    @Override
    public NotificationContactType supports() {
        return NotificationContactType.IN_APP;
    }
}
