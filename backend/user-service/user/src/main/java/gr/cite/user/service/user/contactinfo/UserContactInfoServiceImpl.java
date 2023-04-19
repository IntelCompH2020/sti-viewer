package gr.cite.user.service.user.contactinfo;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.user.authorization.AuthorizationFlags;
import gr.cite.user.authorization.OwnedResource;
import gr.cite.user.authorization.Permission;
import gr.cite.user.common.enums.IsActive;
import gr.cite.user.common.enums.UserContactType;
import gr.cite.user.common.scope.tenant.TenantScope;
import gr.cite.user.common.scope.user.UserScope;
import gr.cite.user.convention.ConventionService;
import gr.cite.user.data.TenantEntity;
import gr.cite.user.data.TenantEntityManager;
import gr.cite.user.data.UserContactInfoCompositeKey;
import gr.cite.user.data.UserContactInfoEntity;
import gr.cite.user.errorcode.ErrorThesaurusProperties;
import gr.cite.user.model.UserContactInfo;
import gr.cite.user.model.builder.UserContactInfoBuilder;
import gr.cite.user.model.deleter.UserContactInfoDeleter;
import gr.cite.user.model.persist.UserContactInfoPersist;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequestScope
public class UserContactInfoServiceImpl implements UserContactInfoService {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserContactInfoServiceImpl.class));
	private final TenantEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final UserScope userScope;
	private final TenantScope tenantScope;
	
	@PersistenceContext
	private EntityManager globalEntityManager;
	@Value("${spring.jpa.hibernate.jdbc.batch-size}")
	private int batchSize;

	@Autowired
	public UserContactInfoServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			UserScope userScope, TenantScope tenantScope) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.userScope = userScope;
		this.tenantScope = tenantScope;
	}



	@Override
	public UserContactInfo persist(UserContactInfoPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting Contact info").And("model", model).And("fields", fields));

		boolean isValidType = Arrays.stream(UserContactType.values()).anyMatch(x -> x.name().equals(model.getType().name()));
		if (!isValidType)
			throw new MyValidationException(this.errors.getModelValidation().getCode(), this.errors.getModelValidation().getMessage());

		boolean isValidUserId = this.conventionService.isValidGuid(model.getUserId()) ;
		if (!isValidUserId)
			throw new MyValidationException(this.errors.getModelValidation().getCode(), this.errors.getModelValidation().getMessage());


		UserContactInfoCompositeKey compositeKey = new UserContactInfoCompositeKey();
		compositeKey.setType(model.getType());
		compositeKey.setUserId(model.getUserId());
		UserContactInfoEntity data = this.entityManager.find(UserContactInfoEntity.class, compositeKey);
		boolean isUpdate = data != null;

		if (data == null){
			data = new UserContactInfoEntity();
			data.setIsActive(IsActive.ACTIVE);
			data.setCreatedAt(Instant.now());
		}
		modelToEntity(model, data);
		data.setUpdatedAt(Instant.now());
		boolean shouldChange = this.shouldChangeTenant();
		try {
			if (shouldChange) {
				TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
				this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId(), tenant.getCode());
			}

			if (isUpdate) data = this.entityManager.merge(data);
			else this.entityManager.persist(data);

		} finally {
			if (shouldChange) this.tenantScope.removeTempTenant(this.globalEntityManager);
		}

		return this.builderFactory.builder(UserContactInfoBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, UserContactInfo._user,UserContactInfo._type,UserContactInfo._value), data);
	}

	@Override
	public void batchPersist(List<UserContactInfoPersist> models, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug("will persist {} items", Optional.ofNullable(models).map(List::size).orElse(0));
		if (models == null || models.isEmpty()) return;

		for (int i = 0; i < models.size(); i++) {
			if (i > 0 && i % this.batchSize == 0) {
				logger.trace("batch size reached");
				logger.trace("Flushing");
				entityManager.flush();
				logger.trace("Clearing");
				entityManager.clear();
			}
			logger.trace("persisting item {}", models.get(i));
			logger.trace("adding item");
			this.persist(models.get(i), fields);
			logger.trace("added item");
		}

		logger.trace("batch size reached");
		logger.trace("Flushing");
		entityManager.flush();
		logger.trace("Clearing");
		entityManager.clear();
	}

	@Override
	public void deleteAndSave(List<UserContactInfoPersist> model) throws MyForbiddenException, InvalidApplicationException {

		for (UserContactInfoPersist m: model){
			boolean shouldChange = this.shouldChangeTenant();
			try {
				if (shouldChange) {
					TenantEntity tenant = this.entityManager.find(TenantEntity.class, m.getTenantId());
					this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId(), tenant.getCode());
				}
				UserContactInfoCompositeKey compositeKey = new UserContactInfoCompositeKey();
				compositeKey.setType(m.getType());
				compositeKey.setUserId(m.getUserId());
				this.deleterFactory.deleter(UserContactInfoDeleter.class).deleteAndSaveByIds(List.of(compositeKey));

			} finally {
				if (shouldChange) this.tenantScope.removeTempTenant(this.globalEntityManager);
			}
		}

	}

	private boolean shouldChangeTenant() {
		try {
			return this.authorizationService.authorizeForce(Permission.AllowNoTenant);
		}catch (MyForbiddenException exception){
			logger.warn(exception.getMessage());
			return false;
		}
	}

	private void modelToEntity(UserContactInfoPersist model,UserContactInfoEntity data) {
		if (model.getUserId() != null) data.setUserId(model.getUserId());
		if (model.getType() != null) data.setType(model.getType());
		if (model.getValue() != null) data.setValue(model.getValue());
		if (model.getTenantId() != null) data.setTenantId(model.getTenantId());
	}

}
