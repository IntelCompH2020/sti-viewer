package gr.cite.notification.service.tenantconfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.common.types.tenantconfiguration.DefaultUserLocaleConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.EmailClientConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantConfigurationEntity;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.model.TenantConfiguration;
import gr.cite.notification.model.builder.TenantConfigurationBuilder;
import gr.cite.notification.model.deleter.InAppNotificationDeleter;
import gr.cite.notification.model.deleter.TenantConfigurationDeleter;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationEmailClientPersist;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationNotifierListPersist;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationUserLocaleIntegrationPersist;
import gr.cite.notification.query.TenantConfigurationQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequestScope
public class TenantConfigurationServiceImpl implements TenantConfigurationService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantConfigurationServiceImpl.class));

	private final ApplicationContext applicationContext;
	private final JsonHandlingService jsonHandlingService;
	private final AuthorizationService authorizationService;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final BuilderFactory builderFactory;

	private final TenantScopedEntityManager dbContext;
	private final DeleterFactory deleterFactory;

	@Autowired
	public TenantConfigurationServiceImpl(ApplicationContext applicationContext, JsonHandlingService jsonHandlingService, AuthorizationService authorizationService, ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, BuilderFactory builderFactory, TenantScopedEntityManager dbContext, DeleterFactory deleterFactory) {
		this.applicationContext = applicationContext;
		this.jsonHandlingService = jsonHandlingService;
		this.authorizationService = authorizationService;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.builderFactory = builderFactory;
		this.dbContext = dbContext;
		this.deleterFactory = deleterFactory;
	}

	@Override
	public EmailClientConfigurationDataContainer collectTenantEmailClient() {
		TenantConfigurationQuery query = applicationContext.getBean(TenantConfigurationQuery.class);
		String data = query.isActive(IsActive.ACTIVE).type(TenantConfigurationType.EMAIL_CLIENT_CONFIGURATION).first().getValue();
		if (data == null) return null;

		try {
			EmailClientConfigurationDataContainer emailClientData = this.jsonHandlingService.fromJson(EmailClientConfigurationDataContainer.class, data);
			return emailClientData;
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public DefaultUserLocaleConfigurationDataContainer collectTenantUserLocale() {
		TenantConfigurationQuery query = applicationContext.getBean(TenantConfigurationQuery.class);
		String data = query.isActive(IsActive.ACTIVE).type(TenantConfigurationType.DEFAULT_USER_LOCALE).first().getValue();
		if (data == null) return null;

		try {
			DefaultUserLocaleConfigurationDataContainer userLocaleData = this.jsonHandlingService.fromJson(DefaultUserLocaleConfigurationDataContainer.class, data);
			return userLocaleData;
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public NotifierListConfigurationDataContainer collectTenantNotifierList() {
		TenantConfigurationQuery query = applicationContext.getBean(TenantConfigurationQuery.class);
		TenantConfigurationEntity configurationEntity = query.isActive(IsActive.ACTIVE).type(TenantConfigurationType.NOTIFIER_LIST).first();
		String data = configurationEntity != null ? configurationEntity.getValue() : null;
		if (data == null) return null;

		try {
			NotifierListConfigurationDataContainer notifierListData = this.jsonHandlingService.fromJson(NotifierListConfigurationDataContainer.class, data);
			return notifierListData;
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public NotifierListConfigurationDataContainer collectTenantAvailableNotifierList(Set<UUID> notificationTypes) {
		return null;
	}

	@Override
	public TenantConfiguration persist(TenantConfigurationEmailClientPersist emailClientPersist, FieldSet fieldSet) {
		EmailClientConfigurationDataContainer container = new EmailClientConfigurationDataContainer();
		container.setEnableSSL(emailClientPersist.getEnableSSL());
		container.setRequireCredentials(emailClientPersist.getRequireCredentials());
		container.setHostServer(emailClientPersist.getHostServer());
		container.setHostPortNo(emailClientPersist.getHostPortNo());
		container.setCertificatePath(emailClientPersist.getCertificatePath());
		container.setEmailAddress(emailClientPersist.getEmailAddress());
		container.setEmailUserName(emailClientPersist.getEmailUserName());
		container.setEmailPassword(emailClientPersist.getEmailPassword());
		try {
			String value = jsonHandlingService.toJson(container);
			return this.persist(emailClientPersist.getId(), emailClientPersist.getHash(), TenantConfigurationType.EMAIL_CLIENT_CONFIGURATION, value, fieldSet);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public TenantConfiguration persist(TenantConfigurationUserLocaleIntegrationPersist userLocaleIntegrationPersist, FieldSet fieldSet) {
		this.authorizationService.authorizeForce(Permission.EditTenantConfiguration);

		TenantConfigurationQuery tenantConfigurationQuery = applicationContext.getBean(TenantConfigurationQuery.class);
		TenantConfigurationEntity data = tenantConfigurationQuery.isActive(IsActive.ACTIVE).type(TenantConfigurationType.DEFAULT_USER_LOCALE).first();
		Boolean isUpdate = data != null;
		if (!isUpdate) {

			data = new TenantConfigurationEntity();
			data.setCreatedAt(Instant.now());
			data.setIsActive(IsActive.ACTIVE);
			data.setType(TenantConfigurationType.DEFAULT_USER_LOCALE);
		}
		try {
			DefaultUserLocaleConfigurationDataContainer container = new DefaultUserLocaleConfigurationDataContainer();
			container.setCulture(userLocaleIntegrationPersist.getCulture());
			container.setTimeZone(userLocaleIntegrationPersist.getTimeZone());
			container.setLanguage(userLocaleIntegrationPersist.getLanguage());
			String value = jsonHandlingService.toJson(container);

			data.setValue(value);
			data.setUpdatedAt(Instant.now());
			this.dbContext.merge(data);
		} catch (InvalidApplicationException | JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}

		//this._eventBroker.EmitTenantConfigurationTouched(this._scope.Tenant, type);

		TenantConfiguration persisted = this.builderFactory.builder(TenantConfigurationBuilder.class).build(fieldSet.merge(new BaseFieldSet(TenantConfiguration.Field.ID, TenantConfiguration.Field.HASH)), data);
		return persisted;
	}

	@Override
	public TenantConfiguration persist(TenantConfigurationNotifierListPersist notifierListPersist, FieldSet fieldSet) {
		NotifierListConfigurationDataContainer container = new NotifierListConfigurationDataContainer();
		container.setNotifiers(notifierListPersist.getNotifiers());
		try {
			String value = jsonHandlingService.toJson(container);
			return this.persist(notifierListPersist.getId(), notifierListPersist.getHash(), TenantConfigurationType.NOTIFIER_LIST, value, fieldSet);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void deleteAndSave(UUID id) throws InvalidApplicationException {
		logger.debug("deleting tenant Configuration: {}", id);
		this.authorizationService.authorizeForce(Permission.DeleteNotification);
		this.deleterFactory.deleter(TenantConfigurationDeleter.class).deleteAndSaveByIds(List.of(id));
	}

	private TenantConfiguration persist(UUID modelId, String modelHash, TenantConfigurationType type, String value, FieldSet fieldSet) {
		this.authorizationService.authorizeForce(Permission.EditTenantConfiguration);

		Boolean isUpdate = this.conventionService.isValidGuid(modelId);

		TenantConfigurationQuery tenantConfigurationQuery = applicationContext.getBean(TenantConfigurationQuery.class);
		List<UUID> existingConfigIds = tenantConfigurationQuery.isActive(IsActive.ACTIVE).type(type).collectAs(new BaseFieldSet(TenantConfigurationEntity.Field.ID)).stream().map(TenantConfigurationEntity::getId).collect(Collectors.toList());
		TenantConfigurationEntity data = null;
		if (isUpdate) {
			if (!existingConfigIds.contains(modelId)) throw new MyValidationException(this.errors.getSingleTenantConfigurationPerTypeSupported().getCode(), this.errors.getSingleTenantConfigurationPerTypeSupported().getMessage());
			if (existingConfigIds.size() > 1) throw new MyValidationException(this.errors.getSingleTenantConfigurationPerTypeSupported().getCode(), this.errors.getSingleTenantConfigurationPerTypeSupported().getMessage());


			data = tenantConfigurationQuery.ids(modelId).first();
			if (data == null) throw new MyNotFoundException(this.messageSource.getMessage("General_ItemNotFound", new Object[]{modelId, TenantConfigurationEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!modelHash.equals(this.conventionService.hashValue(data.getUpdatedAt()))) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
			if (!data.getType().equals(type)) throw new MyValidationException(this.errors.getIncompatibleTenantConfigurationTypes().getCode(), this.errors.getIncompatibleTenantConfigurationTypes().getMessage());
		} else {
			if (!existingConfigIds.isEmpty()) throw new MyValidationException(this.errors.getSingleTenantConfigurationPerTypeSupported().getCode(), this.errors.getSingleTenantConfigurationPerTypeSupported().getMessage());

			data = new TenantConfigurationEntity();
			data.setCreatedAt(Instant.now());
			data.setIsActive(IsActive.ACTIVE);
			data.setType(type);
		}

		data.setValue(value);
		data.setUpdatedAt(Instant.now());

		try {
			this.dbContext.merge(data);
		} catch (InvalidApplicationException e) {
			logger.error(e.getMessage(), e);
		}

		//this._eventBroker.EmitTenantConfigurationTouched(this._scope.Tenant, type);

		TenantConfiguration persisted = this.builderFactory.builder(TenantConfigurationBuilder.class).build(fieldSet.merge(new BaseFieldSet(TenantConfiguration.Field.ID, TenantConfiguration.Field.HASH)), data);
		return persisted;
		//return null;

	}
}
