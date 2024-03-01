package gr.cite.intelcomp.stiviewer.eventscheduler.task;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.ScheduledEventStatus;
import gr.cite.intelcomp.stiviewer.common.scope.fake.FakeRequestScope;
import gr.cite.intelcomp.stiviewer.data.ScheduledEventEntity;
import gr.cite.intelcomp.stiviewer.eventscheduler.EventSchedulerProperties;
import gr.cite.intelcomp.stiviewer.eventscheduler.processing.EventProcessingStatus;
import gr.cite.intelcomp.stiviewer.eventscheduler.processing.ScheduledEventHandler;
import gr.cite.intelcomp.stiviewer.eventscheduler.processing.datagroupbuild.CreateDataGroupScheduledEventHandler;
import gr.cite.intelcomp.stiviewer.query.ScheduledEventQuery;
import gr.cite.tools.data.query.Ordering;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class EventSchedulerTask {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(EventSchedulerTask.class));

	private final ApplicationContext applicationContext;
	private final EventSchedulerProperties properties;

	public EventSchedulerTask(ApplicationContext applicationContext, EventSchedulerProperties properties) {
		this.applicationContext = applicationContext;
		this.properties = properties;
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
			Instant lastCandidateCreationTimestamp = null;
			while (true) {
				CandidateInfo candidateInfo = this.candidateEventToRun(lastCandidateCreationTimestamp);
				if (candidateInfo == null) break;

				lastCandidateCreationTimestamp = candidateInfo.getCreatedAt();
				Boolean shouldOmit = this.shouldOmit(candidateInfo);
				if (shouldOmit) {
					continue;
				}
				Boolean shouldAwait = this.shouldWait(candidateInfo);
				if (shouldAwait) {
					continue;
				}
				this.handle(candidateInfo.getId());
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private CandidateInfo candidateEventToRun(Instant lastCandidateNotificationCreationTimestamp) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		CandidateInfo candidateInfo = null;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();
			ScheduledEventQuery scheduledEventQuery = applicationContext.getBean(ScheduledEventQuery.class);
			ScheduledEventEntity candidates;

			scheduledEventQuery = scheduledEventQuery
					.isActives(IsActive.ACTIVE)
					.status(ScheduledEventStatus.PENDING, ScheduledEventStatus.ERROR)
					.retryThreshold(Math.toIntExact(this.properties.getTask().getProcessor().getOptions().getRetryThreshold()))
					.shouldRunBefore(Instant.now())
					.createdAfter(lastCandidateNotificationCreationTimestamp)
					.ordering(new Ordering().addAscending(ScheduledEventEntity._createdAt));
			candidates = scheduledEventQuery.first();
			if (candidates != null) {
				ScheduledEventStatus previousState = candidates.getStatus();
				candidates.setStatus(ScheduledEventStatus.PROCESSING);
				candidates = entityManager.merge(candidates);
				entityManager.persist(candidates);
				entityManager.flush();

				candidateInfo = new CandidateInfo(candidates.getId(), previousState, candidates.getCreatedAt());
			}
			transaction.commit();

		} catch (OptimisticLockException e) {
			logger.error("Optimistic Lock Error occurred on Notification persist");
			if (transaction != null) transaction.rollback();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			if (transaction != null) transaction.rollback();
		} finally {
			if (entityManager != null) entityManager.close();
		}
		return candidateInfo;
	}

	private Boolean shouldWait(CandidateInfo candidateInfo) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean shouldWait = false;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();
			ScheduledEventEntity scheduledEventEntity = entityManager.find(ScheduledEventEntity.class, candidateInfo.getId());
			if (scheduledEventEntity.getRetryCount() != null && scheduledEventEntity.getRetryCount() >= 1) {
				int accumulatedRetry = 0;
				int pastAccumulateRetry = 0;
				EventSchedulerProperties.Task.Processor.Options options = properties.getTask().getProcessor().getOptions();
				for (int i = 1; i <= scheduledEventEntity.getRetryCount() + 1; i += 1)
					accumulatedRetry += (i * options.getRetryThreshold());
				for (int i = 1; i <= scheduledEventEntity.getRetryCount(); i += 1)
					pastAccumulateRetry += (i * options.getRetryThreshold());
				int randAccumulatedRetry = ThreadLocalRandom.current().nextInt((int) (accumulatedRetry / 2), accumulatedRetry + 1);
				long additionalTime = randAccumulatedRetry > options.getMaxRetryDelaySeconds() ? options.getMaxRetryDelaySeconds() : randAccumulatedRetry;
				long retry = pastAccumulateRetry + additionalTime;

				Instant retryOn = scheduledEventEntity.getCreatedAt().plusSeconds(retry);
				boolean itIsTime = retryOn.isBefore(Instant.now());

				if (!itIsTime) {
					scheduledEventEntity.setStatus(candidateInfo.getPreviousState());
					//notification.setUpdatedAt(Instant.now());
					scheduledEventEntity = entityManager.merge(scheduledEventEntity);
					entityManager.persist(scheduledEventEntity);

				}
				shouldWait = !itIsTime;
			}
			transaction.commit();
		} catch (OptimisticLockException e) {
			logger.error("Optimistic Lock Error occurred on Notification persist");
			if (transaction != null) transaction.rollback();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			if (transaction != null) transaction.rollback();
		} finally {
			if (entityManager != null) entityManager.close();
		}
		return shouldWait;
	}

	private Boolean shouldOmit(CandidateInfo candidateInfo) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean shouldOmit = false;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();

			ScheduledEventEntity scheduledEventEntity = entityManager.find(ScheduledEventEntity.class, candidateInfo.getId());
			long age = Instant.now().getEpochSecond() - scheduledEventEntity.getCreatedAt().getEpochSecond();
			long omitSeconds = properties.getTask().getProcessor().getOptions().getTooOldToHandleSeconds();
			if (age >= omitSeconds) {
				scheduledEventEntity.setStatus(ScheduledEventStatus.OMITTED);
				scheduledEventEntity = entityManager.merge(scheduledEventEntity);
				entityManager.persist(scheduledEventEntity);
				shouldOmit = true;
			}
			transaction.commit();
		} catch (OptimisticLockException e) {
			logger.error("Optimistic Lock Error occurred on Notification persist");
			if (transaction != null) transaction.rollback();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			if (transaction != null) transaction.rollback();
		} finally {
			if (entityManager != null) entityManager.close();
		}
		return shouldOmit;
	}

	private Boolean handle(UUID eventId) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean success = false;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();

			ScheduledEventQuery scheduledEventQuery = applicationContext.getBean(ScheduledEventQuery.class);
			ScheduledEventEntity scheduledEvent = scheduledEventQuery.ids(eventId).first();
			if (scheduledEvent == null) throw new IllegalArgumentException("scheduledEvent is null");

			EventProcessingStatus status = this.process(scheduledEvent);
			switch (status) {
				case Success: {
					scheduledEvent.setStatus(ScheduledEventStatus.SUCCESSFUL);
					break;
				}
				case Postponed: {
					scheduledEvent.setStatus(ScheduledEventStatus.PARKED);
					break;
				}
				case Error: {
					scheduledEvent.setStatus(ScheduledEventStatus.ERROR);
					scheduledEvent.setRetryCount(scheduledEvent.getRetryCount() + 1);
					break;
				}
				case Discard:
				default: {
					scheduledEvent.setStatus(ScheduledEventStatus.DISCARD);
					break;
				}
			}
			success = status == EventProcessingStatus.Success;

			ScheduledEventEntity notification1 = entityManager.merge(scheduledEvent);
			entityManager.persist(notification1);

			entityManager.flush();

			transaction.commit();
		} catch (OptimisticLockException e) {
			logger.error("Optimistic Lock Error occurred on Notification persist");
			if (transaction != null) transaction.rollback();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			if (transaction != null) transaction.rollback();
		} finally {
			if (entityManager != null) entityManager.close();
		}
		return success;
	}

	protected EventProcessingStatus process(ScheduledEventEntity scheduledEventMessage) {
		try {
			ScheduledEventHandler handler = null;
			switch (scheduledEventMessage.getEventType()) {
				case BUILD_DATA_GROUP:
					handler = applicationContext.getBean(CreateDataGroupScheduledEventHandler.class);
					break;
				default:
					return EventProcessingStatus.Discard;
			}

			return handler.handle(scheduledEventMessage);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return EventProcessingStatus.Error;
		}
	}
}
