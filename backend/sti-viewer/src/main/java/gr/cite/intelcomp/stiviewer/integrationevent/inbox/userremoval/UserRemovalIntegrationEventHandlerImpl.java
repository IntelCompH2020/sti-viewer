package gr.cite.intelcomp.stiviewer.integrationevent.inbox.userremoval;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.scope.fake.FakeRequestScope;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.EventProcessingStatus;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.InboxPrincipal;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.query.TenantQuery;
import gr.cite.intelcomp.stiviewer.service.user.UserService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import java.util.AbstractMap;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserRemovalIntegrationEventHandlerImpl implements UserRemovalIntegrationEventHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserRemovalIntegrationEventHandlerImpl.class));
	private final JsonHandlingService jsonHandlingService;
	protected final ApplicationContext applicationContext;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;

	public UserRemovalIntegrationEventHandlerImpl(
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
	public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
		UserRemovalIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(UserRemovalIntegrationEvent.class, message);
		if (event == null) return EventProcessingStatus.Error;
		if (event.getUserId() == null) throw new MyValidationException(this.errors.getModelValidation().getCode(), "userId", messageSource.getMessage("Validation_Required", new Object[]{"userId"}, LocaleContextHolder.getLocale()));

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

				CurrentPrincipalResolver currentPrincipalResolver = this.applicationContext.getBean(CurrentPrincipalResolver.class);
				currentPrincipalResolver.push(InboxPrincipal.build(properties));

				UserRemovalConsistencyHandler userRemovalConsistencyHandler = this.applicationContext.getBean(UserRemovalConsistencyHandler.class);
				if (!(userRemovalConsistencyHandler.isConsistent(new UserRemovalConsistencyPredicates(event.getUserId())))) return EventProcessingStatus.Postponed;

				EntityManagerFactory entityManagerFactory = this.applicationContext.getBean(EntityManagerFactory.class);
				entityManager = entityManagerFactory.createEntityManager();

				transaction = entityManager.getTransaction();
				transaction.begin();

				try {
					UserService userService = this.applicationContext.getBean(UserService.class);
					userService.deleteAndSave(event.getUserId());

					AuditService auditService = this.applicationContext.getBean(AuditService.class);

					auditService.track(AuditableAction.User_Delete, Map.ofEntries(
							new AbstractMap.SimpleEntry<String, Object>("id", event.getUserId())
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
