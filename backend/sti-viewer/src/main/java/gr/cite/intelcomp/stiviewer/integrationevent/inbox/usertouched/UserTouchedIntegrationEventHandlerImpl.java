package gr.cite.intelcomp.stiviewer.integrationevent.inbox.usertouched;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.scope.fake.FakeRequestScope;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.EventProcessingStatus;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.InboxPrincipal;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.persist.UserTouchedIntegrationEventPersist;
import gr.cite.intelcomp.stiviewer.query.TenantQuery;
import gr.cite.intelcomp.stiviewer.service.user.UserService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.validation.ValidationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import java.util.AbstractMap;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserTouchedIntegrationEventHandlerImpl implements UserTouchedIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserTouchedIntegrationEventHandlerImpl.class));
	private final JsonHandlingService jsonHandlingService;
	protected final ApplicationContext applicationContext;

	public UserTouchedIntegrationEventHandlerImpl(
			JsonHandlingService jsonHandlingService,
			ApplicationContext applicationContext
	) {
		this.jsonHandlingService = jsonHandlingService;
		this.applicationContext = applicationContext;
	}

	@Override
	public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
		UserTouchedIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(UserTouchedIntegrationEvent.class, message);
		if (event == null) return EventProcessingStatus.Error;

		UserTouchedIntegrationEventPersist model = new UserTouchedIntegrationEventPersist();
		model.setId(event.getId());
		model.setFirstName(event.getFirstName());
		model.setLastName(event.getLastName());

		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		try (FakeRequestScope fakeRequestScope = new FakeRequestScope()) {
			try {
				QueryFactory queryFactory = this.applicationContext.getBean(QueryFactory.class);
				TenantScope scope = this.applicationContext.getBean(TenantScope.class);
				if (scope.isMultitenant() && event.getTenant() != null) {
					TenantEntity tenant = queryFactory.query(TenantQuery.class).ids(event.getTenant()).firstAs(new BaseFieldSet().ensure(Tenant._id).ensure(Tenant._code));
					if (tenant == null) {
						logger.error("missing tenant from event message");
						return EventProcessingStatus.Error;
					}
					scope.setTenant(event.getTenant(), tenant.getCode());
				} else if (scope.isMultitenant()) {
					logger.error("missing tenant from event message");
					return EventProcessingStatus.Error;
				}

				ValidationService validator = this.applicationContext.getBean(ValidationService.class);
				validator.validateForce(model);


				CurrentPrincipalResolver currentPrincipalResolver = this.applicationContext.getBean(CurrentPrincipalResolver.class);
				currentPrincipalResolver.push(InboxPrincipal.build(properties));

				EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);
				entityManager = entityManagerFactory.createEntityManager();

				transaction = entityManager.getTransaction();
				transaction.begin();

				try {
					UserService userService = this.applicationContext.getBean(UserService.class);
					userService.persist(model, null);

					AuditService auditService = this.applicationContext.getBean(AuditService.class);

					auditService.track(AuditableAction.User_Persist, Map.ofEntries(
							new AbstractMap.SimpleEntry<String, Object>("model", model)
					));
					//auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

					transaction.commit();
				} catch (Exception e) {
					transaction.rollback();
					throw e;
				} finally {
					currentPrincipalResolver.pop();
				}

				transaction.commit();
			} catch (OptimisticLockException ex) {
				// we get this if/when someone else already modified the notifications. We want to essentially ignore this, and keep working
				this.logger.debug("Concurrency exception getting queue outbox. Skipping: {} ", ex.getMessage());
				if (transaction != null) transaction.rollback();
			} catch (Exception ex) {
				this.logger.error("Problem getting list of queue outbox. Skipping: {}", ex.getMessage(), ex);
				if (transaction != null) transaction.rollback();
			} finally {
				if (entityManager != null) entityManager.close();
			}
		} catch (Exception ex) {
			this.logger.error("Problem getting list of queue outbox. Skipping: {}", ex.getMessage(), ex);
		}
		return null;
	}
}
