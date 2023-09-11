package gr.cite.intelcomp.stiviewer.integrationevent.inbox;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.fake.FakeRequestScope;
import gr.cite.intelcomp.stiviewer.data.QueueInboxEntity;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.userremoval.UserRemovalIntegrationEventHandler;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.usertouched.UserTouchedIntegrationEventHandler;
import gr.cite.intelcomp.stiviewer.query.QueueInboxQuery;
import gr.cite.queueinbox.entity.QueueInbox;
import gr.cite.queueinbox.entity.QueueInboxStatus;
import gr.cite.queueinbox.repository.CandidateInfo;
import gr.cite.queueinbox.repository.InboxRepository;
import gr.cite.queueinbox.task.MessageOptions;
import gr.cite.rabbitmq.consumer.InboxCreatorParams;
import gr.cite.tools.data.query.Ordering;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class InboxRepositoryImpl implements InboxRepository {

    protected final ApplicationContext applicationContext;

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(InboxRepositoryImpl.class));

    private final InboxProperties inboxProperties;

    public InboxRepositoryImpl(
            ApplicationContext applicationContext,
            InboxProperties inboxProperties
    ) {
        this.applicationContext = applicationContext;
        this.inboxProperties = inboxProperties;
    }

    @Override
    public CandidateInfo candidate(Instant lastCandidateCreationTimestamp, MessageOptions options) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        CandidateInfo candidate = null;
        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {
                QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
                EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);
                entityManager = entityManagerFactory.createEntityManager();

                transaction = entityManager.getTransaction();
                transaction.begin();

                QueueInboxEntity item = queryFactory.query(QueueInboxQuery.class)
                        .isActives(IsActive.ACTIVE)
                        .status(QueueInboxStatus.PENDING, QueueInboxStatus.ERROR)
                        .retryThreshold(options.getRetryThreashold())
                        .createdAfter(lastCandidateCreationTimestamp)
                        .ordering(new Ordering().addAscending(QueueInboxEntity._createdAt))
                        .first();

                if (item != null) {
                    QueueInboxStatus prevState = item.getStatus();
                    item.setStatus(QueueInboxStatus.PROCESSING);

                    entityManager.merge(item);
                    entityManager.flush();

                    candidate = new CandidateInfo();
                    candidate.setId(item.getId());
                    candidate.setCreatedAt(item.getCreatedAt());
                    candidate.setPreviousState(prevState);
                }

                transaction.commit();
            } catch (OptimisticLockException ex) {
                // we get this if/when someone else already modified the notifications. We want to essentially ignore this, and keep working
                logger.debug("Concurrency exception getting queue inbox. Skipping: {} ", ex.getMessage());
                if (transaction != null)
                    transaction.rollback();
                candidate = null;
            } catch (Exception ex) {
                logger.error("Problem getting list of queue inbox. Skipping: {}", ex.getMessage(), ex);
                if (transaction != null)
                    transaction.rollback();
                candidate = null;
            } finally {
                if (entityManager != null)
                    entityManager.close();
            }
        } catch (Exception ex) {
            logger.error("Problem getting list of queue inbox. Skipping: {}", ex.getMessage(), ex);
        }

        return candidate;
    }

    @Override
    public Boolean shouldOmit(CandidateInfo candidate, Function<QueueInbox, Boolean> shouldOmit) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        boolean success = false;

        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {
                EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

                entityManager = entityManagerFactory.createEntityManager();
                transaction = entityManager.getTransaction();

                transaction.begin();

                QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
                QueueInboxEntity item = queryFactory.query(QueueInboxQuery.class).ids(candidate.getId()).first();

                if (item == null) {
                    logger.warn("Could not lookup queue inbox {} to process. Continuing...", candidate.getId());
                } else {
                    if (shouldOmit.apply(item)) {
                        item.setStatus(QueueInboxStatus.OMITTED);

                        entityManager.merge(item);
                        entityManager.flush();
                        success = true;
                    }
                }

                transaction.commit();
            } catch (Exception ex) {
                logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
                if (transaction != null)
                    transaction.rollback();
                success = false;
            } finally {
                if (entityManager != null)
                    entityManager.close();
            }
        } catch (Exception ex) {
            logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
        }
        return success;
    }

    @Override
    public boolean shouldWait(CandidateInfo candidate, Function<QueueInbox, Boolean> itIsTimeFunc) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        boolean success = false;

        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {
                EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

                entityManager = entityManagerFactory.createEntityManager();
                transaction = entityManager.getTransaction();

                transaction.begin();

                QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
                QueueInboxEntity item = queryFactory.query(QueueInboxQuery.class).ids(candidate.getId()).first();

                if (item.getRetryCount() != null && item.getRetryCount() >= 1) {
                    Boolean itIsTime = itIsTimeFunc.apply(item);

                    if (!itIsTime) {
                        item.setStatus(candidate.getPreviousState());

                        entityManager.merge(item);
                        entityManager.flush();
                        success = true;
                    }
                }
                transaction.commit();
            } catch (Exception ex) {
                logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
                if (transaction != null)
                    transaction.rollback();
                success = false;
            } finally {
                if (entityManager != null)
                    entityManager.close();
            }
        } catch (Exception ex) {
            logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
        }
        return success;
    }

    @Override
    public QueueInbox create(InboxCreatorParams inboxCreatorParams) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        QueueInboxEntity queueMessage = null;
        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {
                queueMessage = this.createQueueInboxEntity(inboxCreatorParams);
                EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

                entityManager = entityManagerFactory.createEntityManager();
                transaction = entityManager.getTransaction();

                transaction.begin();

                entityManager.persist(queueMessage);
                entityManager.flush();

                transaction.commit();
            } catch (Exception ex) {
                logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
                if (transaction != null)
                    transaction.rollback();
            } finally {
                if (entityManager != null)
                    entityManager.close();
            }
        } catch (Exception ex) {
            logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
        }
        return queueMessage;
    }

    private QueueInboxEntity createQueueInboxEntity(InboxCreatorParams inboxCreatorParams) {

        QueueInboxEntity queueMessage = new QueueInboxEntity();
        queueMessage.setId(UUID.randomUUID());
        queueMessage.setTenantId(null);
        queueMessage.setExchange(this.inboxProperties.getExchange());
        queueMessage.setRoute(inboxCreatorParams.getRoutingKey());
        queueMessage.setQueue(inboxCreatorParams.getQueueName());
        queueMessage.setApplicationId(inboxCreatorParams.getAppId());
        queueMessage.setMessageId(UUID.fromString(inboxCreatorParams.getMessageId()));
        queueMessage.setMessage(inboxCreatorParams.getMessageBody());
        queueMessage.setIsActive(IsActive.ACTIVE);
        queueMessage.setStatus(QueueInboxStatus.PENDING);
        queueMessage.setRetryCount(0);
        queueMessage.setCreatedAt(Instant.now());

        return queueMessage;
    }

    @Override
    public Boolean emit(CandidateInfo candidateInfo) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        boolean success = false;

        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {

                EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

                entityManager = entityManagerFactory.createEntityManager();
                transaction = entityManager.getTransaction();
                transaction.begin();

                QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
                QueueInboxEntity queueInboxMessage = queryFactory.query(QueueInboxQuery.class).ids(candidateInfo.getId()).first();

                if (queueInboxMessage == null) {
                    logger.warn("Could not lookup queue inbox {} to process. Continuing...", candidateInfo.getId());
                } else {

                    EventProcessingStatus status = this.processMessage(queueInboxMessage.getRoute(), queueInboxMessage.getMessageId().toString(), queueInboxMessage.getApplicationId(), queueInboxMessage.getMessage());
                    switch (status) {
                        case Success: {
                            queueInboxMessage.setStatus(QueueInboxStatus.SUCCESSFUL);
                            break;
                        }
                        case Postponed: {
                            queueInboxMessage.setStatus(QueueInboxStatus.PARKED);
                            break;
                        }
                        case Error: {
                            queueInboxMessage.setStatus(QueueInboxStatus.ERROR);
                            queueInboxMessage.setRetryCount(queueInboxMessage.getRetryCount() != null ? queueInboxMessage.getRetryCount() + 1 : 0);
                            break;
                        }
                        case Discard:
                        default: {
                            queueInboxMessage.setStatus(QueueInboxStatus.DISCARD);
                            break;
                        }
                    }
                    success = status == EventProcessingStatus.Success;

                    entityManager.merge(queueInboxMessage);
                    entityManager.flush();
                }

                transaction.commit();
            } catch (Exception ex) {
                logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
                if (transaction != null)
                    transaction.rollback();
                success = false;
            } finally {
                if (entityManager != null)
                    entityManager.close();
            }
        } catch (Exception ex) {
            logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
        }
        return success;
    }

    private EventProcessingStatus processMessage(String routingKey, String messageId, String appId, String message) {
        IntegrationEventHandler handler;
        if (routingKeyMatched(routingKey, this.inboxProperties.getUserRemovalTopic()))
            handler = this.applicationContext.getBean(UserRemovalIntegrationEventHandler.class);
        else if (routingKeyMatched(routingKey, this.inboxProperties.getUserTouchedTopic()))
            handler = this.applicationContext.getBean(UserTouchedIntegrationEventHandler.class);
        else
            handler = null;

        if (handler == null)
            return EventProcessingStatus.Discard;

        IntegrationEventProperties properties = new IntegrationEventProperties();
        properties.setAppId(appId);
        properties.setMessageId(messageId);

        try {
            return handler.handle(properties, message);
        } catch (Exception ex) {
            logger.error("problem handling event from routing key " + routingKey + ". Setting nack and continuing...", ex);
            return EventProcessingStatus.Error;
        }
    }

    private Boolean routingKeyMatched(String routingKey, List<String> topics) {
        if (topics == null || topics.isEmpty())
            return Boolean.FALSE;
        return topics.stream().anyMatch(x -> x.equals(routingKey));
    }
}
