package gr.cite.intelcomp.stiviewer.service.externaltoken;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.OwnedResource;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.ExternalTokenType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.externaltoken.DefinitionEntity;
import gr.cite.intelcomp.stiviewer.common.types.externaltoken.DefinitionMapperEntity;
import gr.cite.intelcomp.stiviewer.common.types.externaltoken.IndicatorPointQueryDefinitionEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.ExternalTokenEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.ExternalToken;
import gr.cite.intelcomp.stiviewer.model.ExternalTokenCreateResponse;
import gr.cite.intelcomp.stiviewer.model.builder.ExternalTokenBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.ExternalTokenDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.ExternalTokenChangePersist;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.ExternalTokenExpirationPersist;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.IndicatorPointChartExternalTokenPersist;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.IndicatorPointReportExternalTokenPersist;
import gr.cite.intelcomp.stiviewer.query.ExternalTokenQuery;
import gr.cite.tools.cipher.CipherService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import javax.naming.OperationNotSupportedException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequestScope
public class ExternalTokenServiceImpl implements ExternalTokenService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(ExternalTokenServiceImpl.class));

	private final TenantEntityManager entityManager;

	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final UserScope userScope;
	private final QueryFactory queryFactory;
	private final JsonHandlingService jsonHandlingService;
	private final CipherService cipherService;
	private final ExternalTokenServiceProperties config;
	private final TenantScope tenantScope;
	@PersistenceContext
	private EntityManager globalEntityManager;
	
	@Autowired
	public ExternalTokenServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			UserScope userScope,
			QueryFactory queryFactory,
			JsonHandlingService jsonHandlingService,
			CipherService cipherService,
			ExternalTokenServiceProperties properties, 
			TenantScope tenantScope) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.userScope = userScope;
		this.queryFactory = queryFactory;
		this.jsonHandlingService = jsonHandlingService;
		this.cipherService = cipherService;
		this.config = properties;
		this.tenantScope = tenantScope;
	}

	
	@Override
	public ExternalToken persist(ExternalTokenExpirationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException {
		logger.debug(new MapLogEntry("persisting external token").And("model", model).And("fields", fields));

		if (!this.config.getEnabled()) throw new OperationNotSupportedException("ExternalToken not supported");
		
		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		ExternalTokenEntity data = null;
		if (isUpdate) {
			data = this.entityManager.find(ExternalTokenEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), ExternalToken.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			throw new OperationNotSupportedException("Create not supported");
		}

		this.authorizationService.authorizeAtLeastOneForce(data.getOwnerId() != null ? List.of(new OwnedResource(data.getOwnerId())) : null, Permission.EditExternalToken);
		data.setExpiresAt(model.getExpiresAt());
		data.setUpdatedAt(Instant.now());

		if (tenantScope.isSet() && !data.getTenantId().equals(tenantScope.getTenant())) throw  new MyForbiddenException("tenant is not allowed by user");
		boolean shouldChangeTenant = !tenantScope.isSet();
		try {

			if (shouldChangeTenant) {
				this.authorizationService.authorizeForce(Permission.AllowNoTenant);
				TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
				this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId(), tenant.getCode());
			}
			this.entityManager.merge(data);
			this.entityManager.flush();
		} finally {
			if (shouldChangeTenant) this.tenantScope.removeTempTenant(this.globalEntityManager);
		}
		return this.builderFactory.builder(ExternalTokenBuilder.class).build(BaseFieldSet.build(fields, ExternalToken._id, ExternalToken._hash), data);
	}

	@Override
	public String persist(ExternalTokenChangePersist model) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException, NoSuchAlgorithmException {
		logger.debug(new MapLogEntry("persisting external token").And("model", model));

		if (!this.config.getEnabled()) throw new OperationNotSupportedException("ExternalToken not supported");
		
		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		ExternalTokenEntity data = null;
		if (isUpdate) {
			data = this.entityManager.find(ExternalTokenEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), ExternalToken.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			throw new OperationNotSupportedException("Create not supported");
		}

		this.authorizationService.authorizeAtLeastOneForce(data.getOwnerId() != null ? List.of(new OwnedResource(data.getOwnerId())) : null, Permission.EditExternalToken);

		String token = this.createToken();
		
		data.setToken(this.cipherService.toSha512(token));
		data.setUpdatedAt(Instant.now());

		if (tenantScope.isSet() && !data.getTenantId().equals(tenantScope.getTenant())) throw  new MyForbiddenException("tenant is not allowed by user");
		boolean shouldChangeTenant = !tenantScope.isSet();
		try {

			if (shouldChangeTenant) {
				this.authorizationService.authorizeForce(Permission.AllowNoTenant);
				TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
				this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId(), tenant.getCode());
			}
			this.entityManager.merge(data);
			this.entityManager.flush();
		} finally {
			if (shouldChangeTenant) this.tenantScope.removeTempTenant(this.globalEntityManager);
		}

		return token;
	}

	@Override
	public ExternalTokenCreateResponse persist(IndicatorPointReportExternalTokenPersist model) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException, JsonProcessingException, NoSuchAlgorithmException {
		logger.debug(new MapLogEntry("persisting external token").And("model", model));

		if (!this.config.getEnabled()) throw new OperationNotSupportedException("ExternalToken not supported");
		
		this.authorizationService.authorizeForce(model.getLookups().size() > 1 ? Permission.CreateDashboardExternalToken: Permission.CreateChartExternalToken);

		ExternalTokenCreateResponse persist = new ExternalTokenCreateResponse();
		persist.setToken(this.createToken());
		if (model.getExpiresAt() != null) persist.setExpiresAt(model.getExpiresAt());
		else persist.setExpiresAt(Instant.now().plus(this.config.getExpirationInMinutes(), ChronoUnit.MINUTES));
		
		DefinitionEntity definitionEntity = new DefinitionEntity();
		definitionEntity.setMappers(new ArrayList<>());
		Map<UUID, IndicatorPointQueryDefinitionEntity> map = new HashMap<>();
		for (IndicatorPointChartExternalTokenPersist chartExternalTokenPersist : model.getLookups()){
			IndicatorPointQuery indicatorPointQuery = chartExternalTokenPersist.getLookup().getFilters().enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).indicatorIds(chartExternalTokenPersist.getIndicatorId());
			DefinitionMapperEntity definitionMapper = new DefinitionMapperEntity();
			definitionMapper.setExternalIds(List.of(chartExternalTokenPersist.getChartId(), chartExternalTokenPersist.getDashboardId()));
			definitionMapper.setIndicatorPointQueryId(UUID.randomUUID());
			definitionEntity.getMappers().add(definitionMapper);
			map.put(definitionMapper.getIndicatorPointQueryId(), indicatorPointQuery.toIndicatorPointQueryDefinitionEntity());
		}
		definitionEntity.setIndicatorPointQueryMap(map);
		
		ExternalTokenEntity data = new ExternalTokenEntity();
		data.setId(UUID.randomUUID());
		data.setIsActive(IsActive.ACTIVE);
		data.setType(map.size() > 1 ? ExternalTokenType.DashboardShare : ExternalTokenType.GraphShare);
		data.setCreatedAt(Instant.now());
		data.setExpiresAt(persist.getExpiresAt());
		data.setName(model.getName());
		data.setOwnerId(this.userScope.getUserId());
		data.setToken(this.cipherService.toSha512(persist.getToken()));
		data.setDefinition(this.jsonHandlingService.toJson(definitionEntity));
		data.setUpdatedAt(Instant.now());

		this.entityManager.persist(data);

		this.entityManager.flush();

		return persist;
	}

	private String createToken() {
		StringBuilder builder = new StringBuilder();
		do {
			SecureRandom random = new SecureRandom(UUID.randomUUID().toString().getBytes());
			if (random.nextBoolean() && this.config.getAllowedSpecialChars() != null && !this.config.getAllowedSpecialChars().isBlank()){
				builder.append(this.config.getAllowedSpecialChars().charAt(random.nextInt(this.config.getAllowedSpecialChars().length() -1)) ); //numbers
			}
			if (random.nextBoolean()){
				builder.append(random.nextInt(9)); //numbers
			}
			if (random.nextBoolean()){
				builder.append(this.getRandomChar(65,90)); //upperCaseLetters
			}
			if (random.nextBoolean()){
				builder.append(this.getRandomChar(97,122)); //lowerCaseLetters
			}
			
		} while (builder.length() < this.config.getPasswordLength());

		return  builder.substring(0, this.config.getPasswordLength());
	}

	public Character getRandomChar(int randomNumberOrigin, int randomNumberBound) {
		SecureRandom random = new SecureRandom(UUID.randomUUID().toString().getBytes());
		IntStream specialChars = random.ints(randomNumberOrigin, randomNumberBound);
		return specialChars.mapToObj(data -> (char) data).findFirst().orElseThrow();
	}
	
	public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException, OperationNotSupportedException {
		logger.debug("deleting externalToken: {}", id);

		if (!this.config.getEnabled()) throw new OperationNotSupportedException("ExternalToken not supported");
		
		this.authorizationService.authorizeForce(Permission.DeleteExternalToken);

		ExternalTokenEntity data  = this.entityManager.find(ExternalTokenEntity.class, id);
		if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, ExternalToken.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		if (tenantScope.isSet() && !data.getTenantId().equals(tenantScope.getTenant())) throw  new MyForbiddenException("tenant is not allowed by user");
		boolean shouldChangeTenant = !tenantScope.isSet();
		try {

			if (shouldChangeTenant) {
				this.authorizationService.authorizeForce(Permission.AllowNoTenant);
				TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
				this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId(), tenant.getCode());
			}
			this.deleterFactory.deleter(ExternalTokenDeleter.class).deleteAndSaveByIds(Arrays.asList(new UUID[]{id}));
		} finally {
			if (shouldChangeTenant) this.tenantScope.removeTempTenant(this.globalEntityManager);
		}
		
	}

	@Override
	public DefinitionEntity getValidDefintionForce(String token) throws NoSuchAlgorithmException, OperationNotSupportedException {
		if (!this.config.getEnabled()) throw new OperationNotSupportedException("ExternalToken not supported");
		ExternalTokenEntity tokenEntity = this.getValidForce(token);
		DefinitionEntity definitionEntity = this.jsonHandlingService.fromJsonSafe(DefinitionEntity.class, tokenEntity.getDefinition());
		if (definitionEntity == null)  throw new MyForbiddenException("Access is denied");
		return definitionEntity;
	}

	@Override
	public ExternalTokenEntity getValidForce(String token) throws NoSuchAlgorithmException, OperationNotSupportedException {
		if (!this.config.getEnabled()) throw new OperationNotSupportedException("ExternalToken not supported");
		
		if (this.conventionService.isNullOrEmpty(token)) throw new MyForbiddenException("Access is denied");
		ExternalTokenEntity tokenEntity = this.queryFactory.query(ExternalTokenQuery.class).tokens(this.cipherService.toSha512(token)).isActive(IsActive.ACTIVE).first();
		if (tokenEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{token, ExternalTokenEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		if (tokenEntity.getExpiresAt().isBefore(Instant.now()))  throw new MyForbiddenException("Access is denied");
		return tokenEntity;
	}
}
