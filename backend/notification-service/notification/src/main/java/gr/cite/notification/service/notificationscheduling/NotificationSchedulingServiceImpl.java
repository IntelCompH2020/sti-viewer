package gr.cite.notification.service.notificationscheduling;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationNotifyState;
import gr.cite.notification.common.enums.NotificationTrackingProgress;
import gr.cite.notification.common.enums.NotificationTrackingState;
import gr.cite.notification.common.scope.fake.FakeRequestScope;
import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.config.notification.NotificationProperties;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.data.TenantEntity;
import gr.cite.notification.model.SendNotificationResult;
import gr.cite.notification.query.NotificationQuery;
import gr.cite.notification.query.TenantQuery;
import gr.cite.notification.schedule.model.CandidateInfo;
import gr.cite.notification.schedule.model.MiniTenant;
import gr.cite.notification.service.notification.NotificationService;
import gr.cite.notification.service.track.TrackingFactory;
import gr.cite.notification.service.track.model.TrackerResponse;
import gr.cite.tools.data.query.Ordering;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class NotificationSchedulingServiceImpl implements NotificationSchedulingService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(NotificationSchedulingServiceImpl.class));
	private final ApplicationContext applicationContext;
	private final NotificationProperties properties;

	public NotificationSchedulingServiceImpl(ApplicationContext applicationContext, NotificationProperties properties) {
		this.applicationContext = applicationContext;
		this.properties = properties;
	}


	@Override
	public List<MiniTenant> getTenants() {
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			List<MiniTenant> tenantIds = List.of(new MiniTenant(new UUID(0L, 0L), ""));
			TenantScope tenantScope = applicationContext.getBean(TenantScope.class);
			if (tenantScope.isMultitenant()) {
				FieldSet fieldSet = new BaseFieldSet(List.of(TenantEntity._id, TenantEntity._code));
				TenantQuery tenantQuery = this.applicationContext.getBean(TenantQuery.class);
				List<TenantEntity> tenants = tenantQuery.isActive(IsActive.ACTIVE).collectAs(fieldSet);
				tenantIds = tenants.stream().map(tenantEntity -> new MiniTenant(tenantEntity.getId(), tenantEntity.getCode())).collect(Collectors.toList());
			}
			return tenantIds;
		}
	}

	@Override
	public CandidateInfo candidateToNotify(Instant lastCandidateNotificationCreationTimestamp) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		CandidateInfo candidateInfo = null;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();
			NotificationQuery notificationQuery = applicationContext.getBean(NotificationQuery.class);
			NotificationEntity candidates;

			notificationQuery = notificationQuery
					.isActive(IsActive.ACTIVE)
					.notifyState(NotificationNotifyState.PENDING, NotificationNotifyState.ERROR)
					.retryThreshold(Math.toIntExact(this.properties.getTask().getProcessor().getOptions().getRetryThreshold()))
					.createdAfter(lastCandidateNotificationCreationTimestamp)
					.ordering(new Ordering().addAscending(NotificationEntity.Field._createdAt));
			candidates = notificationQuery.first();
			if (candidates != null) {
				NotificationNotifyState previousState = candidates.getNotifyState();
				candidates.setNotifyState(NotificationNotifyState.PROCESSING);
				//candidates.setUpdatedAt(Instant.now());
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

	@Override
	public Boolean shouldWait(CandidateInfo candidateInfo) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean shouldWait = false;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();
			NotificationEntity notification = entityManager.find(NotificationEntity.class, candidateInfo.getNotificationId());
			if (notification.getRetryCount() != null && notification.getRetryCount() >= 1) {
				int accumulatedRetry = 0;
				int pastAccumulateRetry = 0;
				NotificationProperties.Task.Processor.Options options = properties.getTask().getProcessor().getOptions();
				for (int i = 1; i <= notification.getRetryCount() + 1; i += 1)
					accumulatedRetry += (i * options.getRetryThreshold());
				for (int i = 1; i <= notification.getRetryCount(); i += 1)
					pastAccumulateRetry += (i * options.getRetryThreshold());
				int randAccumulatedRetry = ThreadLocalRandom.current().nextInt((int) (accumulatedRetry / 2), accumulatedRetry + 1);
				long additionalTime = randAccumulatedRetry > options.getMaxRetryDelaySeconds() ? options.getMaxRetryDelaySeconds() : randAccumulatedRetry;
				long retry = pastAccumulateRetry + additionalTime;

				Instant retryOn = notification.getCreatedAt().plusSeconds(retry);
				boolean itIsTime = retryOn.isBefore(Instant.now());

				if (!itIsTime) {
					notification.setNotifyState(candidateInfo.getPreviousState());
					//notification.setUpdatedAt(Instant.now());
					notification = entityManager.merge(notification);
					entityManager.persist(notification);

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

	@Override
	public Boolean shouldOmitNotify(UUID notificationId) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean shouldOmit = false;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();

			NotificationEntity notification = entityManager.find(NotificationEntity.class, notificationId);
			long age = Instant.now().getEpochSecond() - notification.getCreatedAt().getEpochSecond();
			long omitSeconds = properties.getTask().getProcessor().getOptions().getTooOldToSendSeconds();
			if (age >= omitSeconds) {
				notification.setNotifyState(NotificationNotifyState.OMITTED);
				//notification.setUpdatedAt(Instant.now());
				notification = entityManager.merge(notification);
				entityManager.persist(notification);
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

	@Override
	public Boolean notify(UUID notificationId) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean success = null;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();

			NotificationQuery notificationQuery = applicationContext.getBean(NotificationQuery.class);
			NotificationEntity notification = notificationQuery.ids(notificationId).first();
			if (notification == null) throw new IllegalArgumentException("notification is null");

			SendNotificationResult result = null;
			NotificationService notificationService = this.applicationContext.getBean(NotificationService.class);
			result = notificationService.doNotify(notification);

			if (result != null && result.getSuccess()) {
				notification.setNotifyState(NotificationNotifyState.SUCCESSFUL);
				notification.setTrackingData(result.getTrackingData());
				notification.setNotifiedWith(result.getContactType());
				notification.setNotifiedAt(Instant.now());
			} else {
				notification.setNotifyState(NotificationNotifyState.ERROR);
				notification.setRetryCount(notification.getRetryCount() != null ? notification.getRetryCount() + 1 : 0);
				notification.setNotifiedWith(null);
				notification.setNotifiedAt(null);
			}
			//notification.setUpdatedAt(Instant.now());

			NotificationEntity notification1 = entityManager.merge(notification);
			entityManager.persist(notification1);


			//we want to return false for notification we want to add in the skip bag
			success = result != null && result.getSuccess();
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

	@Override
	public CandidateInfo candidateToTrack(Instant lastCandidateNotificationCreationTimestamp) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		CandidateInfo candidateInfo = null;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();
			NotificationQuery notificationQuery = applicationContext.getBean(NotificationQuery.class);
			NotificationEntity candidates;

			notificationQuery = notificationQuery
					.isActive(IsActive.ACTIVE)
					.notifyState(NotificationNotifyState.SUCCESSFUL)
					.notifiedWithHasValue()
					.notifiedWithHasValue()
					.createdAfter(lastCandidateNotificationCreationTimestamp)
					.trackingState(NotificationTrackingState.QUEUED, NotificationTrackingState.SENT, NotificationTrackingState.UNDEFINED)
					.trackingProgress(NotificationTrackingProgress.PENDING);
			candidates = notificationQuery.first();
			if (candidates != null) {
				NotificationNotifyState previousState = candidates.getNotifyState();
				candidates.setTrackingProgress(NotificationTrackingProgress.PROCESSING);
				//candidates.setUpdatedAt(Instant.now());
				candidates = entityManager.merge(candidates);
				entityManager.persist(candidates);
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

	@Override
	public Boolean shouldOmitTracking(UUID notificationId) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean shouldOmit = false;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();

			NotificationEntity notification = entityManager.find(NotificationEntity.class, notificationId);
			long age = Instant.now().getEpochSecond() - notification.getNotifiedAt().getEpochSecond();
			long omitSeconds = properties.getTask().getProcessor().getOptions().getTooOldToTrackSeconds();
			if (age >= omitSeconds) {
				notification.setTrackingProgress(NotificationTrackingProgress.OMITTED);
				//notification.setUpdatedAt(Instant.now());
				notification = entityManager.merge(notification);
				entityManager.persist(notification);
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

	@Override
	public Boolean track(UUID notificationId) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		Boolean success = null;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);

			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();

			NotificationQuery notificationQuery = applicationContext.getBean(NotificationQuery.class);
			NotificationEntity notification = notificationQuery.ids(notificationId).first();
			if (notification == null) throw new IllegalArgumentException("notification is null");
			if (notification.getNotifiedWith() == null) throw new IllegalArgumentException("Notification's Notified With is null");
			if (notification.getNotifiedAt() == null) throw new IllegalArgumentException("Notification's Notified At is null");

			TrackerResponse result = null;
			try {
				TrackingFactory trackingFactory = applicationContext.getBean(TrackingFactory.class);
				result = trackingFactory.fromContactType(notification.getNotifiedWith()).track(notification);
			} catch (Exception e) {
				logger.error("Could not complete track for notification " + notification.getId(), e);
			}

			if (result != null && notification.getTrackingProgress().equals(result.getTrackingProgress()) && notification.getTrackingState().equals(result.getTrackingState())) {
				return false;
			}

			if (result != null) {
				notification.setTrackingState(result.getTrackingState());
				notification.setTrackingProgress(result.getTrackingProgress());
				notification.setTrackingData(result.getTrackingData());
			} else {
				notification.setTrackingProgress(NotificationTrackingProgress.ERROR);
			}
			//notification.setUpdatedAt(Instant.now());

			NotificationEntity notification1 = entityManager.merge(notification);
			entityManager.persist(notification1);


			//we want to return false for notification we want to add in the skip bag
			success = result != null && !notification.getTrackingProgress().equals(NotificationTrackingProgress.ERROR);
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
}
