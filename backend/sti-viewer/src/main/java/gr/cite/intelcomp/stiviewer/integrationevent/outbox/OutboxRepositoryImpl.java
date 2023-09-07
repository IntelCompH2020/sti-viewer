package gr.cite.intelcomp.stiviewer.integrationevent.outbox;

import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.fake.FakeRequestScope;
import gr.cite.intelcomp.stiviewer.data.QueueOutboxEntity;
import gr.cite.intelcomp.stiviewer.query.QueueOutboxQuery;
import gr.cite.queueoutbox.entity.QueueOutbox;
import gr.cite.queueoutbox.entity.QueueOutboxNotifyStatus;
import gr.cite.queueoutbox.repository.CandidateInfo;
import gr.cite.queueoutbox.repository.OutboxRepository;
import gr.cite.queueoutbox.task.MessageOptions;
import gr.cite.rabbitmq.IntegrationEvent;
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
import java.util.stream.Collectors;

public class OutboxRepositoryImpl implements OutboxRepository {

    protected final ApplicationContext applicationContext;
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(OutboxRepositoryImpl.class));
    private final JsonHandlingService jsonHandlingService;
    private final OutboxProperties outboxProperties;

    public OutboxRepositoryImpl(
            ApplicationContext applicationContext,
            OutboxProperties outboxProperties
    ) {
        this.applicationContext = applicationContext;
        this.jsonHandlingService = this.applicationContext.getBean(JsonHandlingService.class);
        this.outboxProperties = outboxProperties;
    }

    @Override
    public CandidateInfo candidate(Instant lastCandidateCreationTimestamp, MessageOptions messageOptions, Function<QueueOutbox, Boolean> onConfirmTimeout) {
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

                QueueOutboxEntity item = queryFactory.query(QueueOutboxQuery.class)
                        .isActives(IsActive.ACTIVE)
                        .notifyStatus(QueueOutboxNotifyStatus.PENDING, QueueOutboxNotifyStatus.WAITING_CONFIRMATION, QueueOutboxNotifyStatus.ERROR)
                        .retryThreshold(messageOptions.getRetryThreashold())
                        .confirmTimeout(messageOptions.getConfirmTimeoutSeconds())
                        .createdAfter(lastCandidateCreationTimestamp)
                        .ordering(new Ordering().addAscending(QueueOutboxEntity._createdAt))
                        .first();

                if (item != null) {
                    onConfirmTimeout.apply(item);

                    QueueOutboxNotifyStatus prevState = item.getNotifyStatus();
                    item.setNotifyStatus(QueueOutboxNotifyStatus.PROCESSING);

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
                logger.debug("Concurrency exception getting queue outbox. Skipping: {} ", ex.getMessage());
                if (transaction != null)
                    transaction.rollback();
                candidate = null;
            } catch (Exception ex) {
                logger.error("Problem getting list of queue outbox. Skipping: {}", ex.getMessage(), ex);
                if (transaction != null)
                    transaction.rollback();
                candidate = null;
            } finally {
                if (entityManager != null)
                    entityManager.close();
            }
        } catch (Exception ex) {
            logger.error("Problem getting list of queue outbox. Skipping: {}", ex.getMessage(), ex);
        }

        return candidate;
    }

    @Override
    public Boolean shouldOmit(CandidateInfo candidate, Function<QueueOutbox, Boolean> shouldOmit) {
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
                QueueOutboxEntity item = queryFactory.query(QueueOutboxQuery.class).ids(candidate.getId()).first();

                if (item == null) {
                    logger.warn("Could not lookup queue outbox {} to process. Continuing...", candidate.getId());
                } else {
                    if (shouldOmit.apply(item)) {
                        item.setNotifyStatus(QueueOutboxNotifyStatus.OMITTED);

                        entityManager.merge(item);
                        entityManager.flush();
                        success = true;
                    }
                }

                transaction.commit();
            } catch (Exception ex) {
                logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
                if (transaction != null) transaction.rollback();
                success = false;
            } finally {
                if (entityManager != null) entityManager.close();
            }
        } catch (Exception ex) {
            logger.error("Problem executing purge. rolling back any db changes and marking error. Continuing...", ex);
        }
        return success;
    }

    @Override
    public Boolean shouldWait(CandidateInfo candidate, Function<QueueOutbox, Boolean> itIsTimeFunc) {
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
                QueueOutboxEntity item = queryFactory.query(QueueOutboxQuery.class).ids(candidate.getId()).first();

                if (item.getRetryCount() != null && item.getRetryCount() >= 1) {
                    Boolean itIsTime = itIsTimeFunc.apply(item);

                    if (!itIsTime) {
                        item.setNotifyStatus(candidate.getPreviousState());

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
    public Boolean process(CandidateInfo candidateInfo, Boolean isAutoconfirmOnPublish, Function<QueueOutbox, Boolean> publish) {
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
                QueueOutboxEntity item = queryFactory.query(QueueOutboxQuery.class).ids(candidateInfo.getId()).first();

                if (item == null) {
                    logger.warn("Could not lookup queue outbox {} to process. Continuing...", candidateInfo.getId());
                } else {
                    success = publish.apply(item);
                    if (success) {
                        if (isAutoconfirmOnPublish) {
                            item.setNotifyStatus(QueueOutboxNotifyStatus.CONFIRMED);
                            item.setConfirmedAt(Instant.now());
                        } else {
                            item.setNotifyStatus(QueueOutboxNotifyStatus.WAITING_CONFIRMATION);
                        }
                        item.setPublishedAt(Instant.now());
                    } else {
                        item.setNotifyStatus(QueueOutboxNotifyStatus.ERROR);
                        item.setRetryCount(item.getRetryCount() != null ? item.getRetryCount() + 1 : 0);
                        item.setPublishedAt(null);
                    }

                    entityManager.merge(item);
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

    @Override
    public void handleConfirm(List<UUID> confirmedMessages) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;

        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {
                EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

                entityManager = entityManagerFactory.createEntityManager();
                transaction = entityManager.getTransaction();
                transaction.begin();

                QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
                List<QueueOutboxEntity> queueOutboxMessages = queryFactory.query(QueueOutboxQuery.class).ids(confirmedMessages).collect();

                if (queueOutboxMessages == null) {
                    logger.warn("Could not lookup messages {} to process. Continuing...", String.join(",", confirmedMessages.stream().map(x -> x.toString()).collect(Collectors.toList())));
                } else {

                    for (QueueOutboxEntity queueOutboxMessage : queueOutboxMessages) {
                        queueOutboxMessage.setNotifyStatus(QueueOutboxNotifyStatus.CONFIRMED);
                        queueOutboxMessage.setConfirmedAt(Instant.now());
                        entityManager.merge(queueOutboxMessage);
                    }

                    entityManager.flush();
                }

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
    }

    @Override
    public void handleNack(List<UUID> nackedMessages) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;

        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {
                EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

                entityManager = entityManagerFactory.createEntityManager();
                transaction = entityManager.getTransaction();
                transaction.begin();

                QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
                List<QueueOutboxEntity> queueOutboxMessages = queryFactory.query(QueueOutboxQuery.class).ids(nackedMessages).collect();

                if (queueOutboxMessages == null) {
                    logger.warn("Could not lookup messages {} to process. Continuing...", nackedMessages.stream().map(UUID::toString).collect(Collectors.joining(",")));
                } else {

                    for (QueueOutboxEntity queueOutboxMessage : queueOutboxMessages) {
                        queueOutboxMessage.setNotifyStatus(QueueOutboxNotifyStatus.ERROR);
                        queueOutboxMessage.setRetryCount(queueOutboxMessage.getRetryCount() != null ? queueOutboxMessage.getRetryCount() + 1 : 0);
                        entityManager.merge(queueOutboxMessage);
                    }

                    entityManager.flush();
                }

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
    }

    @Override
    public QueueOutbox create(IntegrationEvent item) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        QueueOutboxEntity queueMessage = null;
        try (FakeRequestScope ignored = new FakeRequestScope()) {
            try {
                queueMessage = this.mapEvent((OutboxIntegrationEvent) item);
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

    private QueueOutboxEntity mapEvent(OutboxIntegrationEvent event) {
        String routingKey;
        switch (event.getType()) {
            case OutboxIntegrationEvent.TENANT_REACTIVATE: {
                routingKey = this.outboxProperties.getTenantReactivationTopic();
                break;
            }
            case OutboxIntegrationEvent.TENANT_REMOVE: {
                routingKey = this.outboxProperties.getTenantRemovalTopic();
                break;
            }
            case OutboxIntegrationEvent.TENANT_TOUCH: {
                routingKey = this.outboxProperties.getTenantTouchTopic();
                break;
            }
            case OutboxIntegrationEvent.TENANT_USER_INVITE: {
                routingKey = this.outboxProperties.getTenantUserInviteTopic();
                break;
            }
            case OutboxIntegrationEvent.FORGET_ME_COMPLETED: {
                routingKey = this.outboxProperties.getForgetMeCompletedTopic();
                break;
            }
            case OutboxIntegrationEvent.NOTIFY: {
                routingKey = this.outboxProperties.getNotifyTopic();
                break;
            }
            case OutboxIntegrationEvent.WHAT_YOU_KNOW_ABOUT_ME_COMPLETED: {
                routingKey = this.outboxProperties.getWhatYouKnowAboutMeCompletedTopic();
                break;
            }
            case OutboxIntegrationEvent.GENERATE_FILE: {
                routingKey = this.outboxProperties.getGenerateFileTopic();
                break;
            }
            default: {
                logger.error("unrecognized outgoing integration event {}. Skipping...", event.getType());
                return null;
            }
        }

        UUID correlationId = UUID.randomUUID();
        if (event.getEvent() != null)
            event.getEvent().setTrackingContextTag(correlationId.toString());
        event.setMessage(this.jsonHandlingService.toJsonSafe(event.getEvent()));
        //this._logTrackingService.Trace(correlationId.ToString(), $"Correlating current tracking context with new correlationId {correlationId}");

        QueueOutboxEntity queueMessage = new QueueOutboxEntity();
        queueMessage.setId(UUID.randomUUID());
        queueMessage.setTenantId(null);
        queueMessage.setExchange(this.outboxProperties.getExchange());
        queueMessage.setRoute(routingKey);
        queueMessage.setMessageId(event.getMessageId());
        queueMessage.setMessage(this.jsonHandlingService.toJsonSafe(event));
        queueMessage.setIsActive(IsActive.ACTIVE);
        queueMessage.setNotifyStatus(QueueOutboxNotifyStatus.PENDING);
        queueMessage.setRetryCount(0);
        queueMessage.setCreatedAt(Instant.now());

        return queueMessage;
    }
}
