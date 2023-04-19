package gr.cite.intelcomp.stiviewer.eventscheduler.processing.datagroupbuild;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.DataGroupRequestStatus;
import gr.cite.intelcomp.stiviewer.common.scope.fake.FakeRequestScope;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.data.DataGroupRequestEntity;
import gr.cite.intelcomp.stiviewer.data.ScheduledEventEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.eventscheduler.processing.EventProcessingStatus;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.query.DataGroupRequestQuery;
import gr.cite.intelcomp.stiviewer.query.TenantQuery;
import gr.cite.intelcomp.stiviewer.service.datagrouprequest.DataGroupRequestService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import javax.persistence.*;
import java.io.IOException;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateDataGroupScheduledEventHandlerImpl implements CreateDataGroupScheduledEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(CreateDataGroupScheduledEventHandlerImpl.class));
	private final JsonHandlingService jsonHandlingService;
	protected final ApplicationContext applicationContext;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;

	@PersistenceContext
	public EntityManager entityManager;

	public CreateDataGroupScheduledEventHandlerImpl(
			JsonHandlingService jsonHandlingService,
			ApplicationContext applicationContext,
			ErrorThesaurusProperties errors,
			MessageSource messageSource
	) {
		this.jsonHandlingService = jsonHandlingService;
		this.applicationContext = applicationContext;
		this.errors = errors;
		this.messageSource = messageSource;
	}


	@Override
	public EventProcessingStatus handle(ScheduledEventEntity scheduledEvent) {
		CreateDataGroupScheduledEventData eventData = this.jsonHandlingService.fromJsonSafe(CreateDataGroupScheduledEventData.class, scheduledEvent.getData());
		if (eventData == null) return EventProcessingStatus.Postponed;
		EventProcessingStatus status = EventProcessingStatus.Error;

		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			try {
				EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);
				entityManager = entityManagerFactory.createEntityManager();

				transaction = entityManager.getTransaction();
				transaction.begin();

				CreateDataGroupConsistencyHandler createDataGroupConsistencyHandler = this.applicationContext.getBean(CreateDataGroupConsistencyHandler.class);
				Boolean isConsistent = (createDataGroupConsistencyHandler.isConsistent(new CreateDataGroupConsistencyPredicates(scheduledEvent.getKey(), eventData.getDataGroupRequestId())));
				if (isConsistent) {
					QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
					TenantScope scope = this.applicationContext.getBean(TenantScope.class);

					DataGroupRequestEntity dataGroupRequest = queryFactory.query(DataGroupRequestQuery.class).ids(eventData.getDataGroupRequestId()).first();

					if (scope.isMultitenant() && dataGroupRequest.getTenantId() != null) {
						TenantEntity tenant = queryFactory.query(TenantQuery.class).ids(dataGroupRequest.getTenantId()).firstAs(new BaseFieldSet().ensure(Tenant._id).ensure(Tenant._code));
						if (tenant == null) {
							logger.error("missing tenant from event message");
							status = EventProcessingStatus.Error;
							throw new MyApplicationException("missing tenant from event message");
						}
						scope.setTenant(dataGroupRequest.getTenantId(), tenant.getCode());
					} else if (scope.isMultitenant()) {
						logger.error("missing tenant from event message");
						status = EventProcessingStatus.Error;
						throw new MyApplicationException("missing tenant from event message");

					}

					try {
						this.run(dataGroupRequest, entityManager, queryFactory, scope);
						status = EventProcessingStatus.Success;

						AuditService auditService = this.applicationContext.getBean(AuditService.class);

						auditService.track(AuditableAction.Scheduled_Event_Run, Map.ofEntries(
								new AbstractMap.SimpleEntry<String, Object>("id", scheduledEvent.getId()),
								new AbstractMap.SimpleEntry<String, Object>("eventType", scheduledEvent.getEventType()),
								new AbstractMap.SimpleEntry<String, Object>("key", scheduledEvent.getKey()),
								new AbstractMap.SimpleEntry<String, Object>("keyType", scheduledEvent.getKeyType()),
								new AbstractMap.SimpleEntry<String, Object>("runAt", scheduledEvent.getRunAt())

						));
						//auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

					} catch (Exception e) {
						transaction.rollback();
						status = EventProcessingStatus.Error;
						throw e;
					}
				} else {
					status = EventProcessingStatus.Postponed;
				}


				transaction.commit();
			} catch (OptimisticLockException ex) {
				this.logger.debug("Concurrency exception getting scheduled event. Skipping: {} ", ex.getMessage());
				if (transaction != null) transaction.rollback();
				status = EventProcessingStatus.Error;
			} catch (Exception ex) {
				this.logger.error("Problem getting scheduled event. Skipping: {}", ex.getMessage(), ex);
				if (transaction != null) transaction.rollback();
				status = EventProcessingStatus.Error;
			} finally {
				if (entityManager != null) entityManager.close();
			}
		} catch (Exception ex) {
			this.logger.error("Problem getting scheduled event. Skipping: {}", ex.getMessage(), ex);
			status = EventProcessingStatus.Error;
		}
		return status;
	}

	private void run(DataGroupRequestEntity dataGroupRequest, EntityManager entityManager, QueryFactory queryFactory, TenantScope scope) throws InvalidApplicationException, IOException {
		DataGroupRequestService dataGroupRequestService = this.applicationContext.getBean(DataGroupRequestService.class);
		boolean success = dataGroupRequestService.buildGroup(dataGroupRequest);

		List<DataGroupRequestEntity> dataGroupRequests = queryFactory.query(DataGroupRequestQuery.class).groupHashes(dataGroupRequest.getGroupHash()).collect();
		DataGroupRequestStatus status = success ? DataGroupRequestStatus.COMPLETED : DataGroupRequestStatus.ERROR;
		try {
			for (DataGroupRequestEntity item : dataGroupRequests) {
				TenantEntity tenant = queryFactory.query(TenantQuery.class).ids(dataGroupRequest.getTenantId()).firstAs(new BaseFieldSet().ensure(Tenant._id).ensure(Tenant._code));
				scope.setTempTenant(this.entityManager, tenant.getId(), tenant.getCode());

				item.setStatus(status);
				item.setUpdatedAt(Instant.now());
				entityManager.merge(item);
				entityManager.flush();
			}
		} finally {
			scope.removeTempTenant(this.entityManager);
		}
	}
}
