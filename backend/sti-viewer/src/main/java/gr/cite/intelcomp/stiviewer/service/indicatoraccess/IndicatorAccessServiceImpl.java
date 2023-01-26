package gr.cite.intelcomp.stiviewer.service.indicatoraccess;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccess.FilterColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccess.IndicatorAccessConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorAccessBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.IndicatorAccessDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorAccessPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.FilterColumnConfigPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.IndicatorAccessConfigPersist;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.*;

@Service
@RequestScope
public class IndicatorAccessServiceImpl implements IndicatorAccessService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorAccessServiceImpl.class));
	private final TenantEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final JsonHandlingService jsonHandlingService;

	@Autowired
	public IndicatorAccessServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			JsonHandlingService jsonHandlingService
	) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.jsonHandlingService = jsonHandlingService;
	}

	public IndicatorAccess persist(IndicatorAccessPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting dataset").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditIndicatorAccess);

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		IndicatorAccessEntity data = null;
		if (isUpdate) {
			data = this.entityManager.find(IndicatorAccessEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), IndicatorAccess.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			data = new IndicatorAccessEntity();
			data.setId(UUID.randomUUID());
			data.setIsActive(IsActive.ACTIVE);
			data.setCreatedAt(Instant.now());
		}

		data.setIndicatorId(model.getIndicatorId());
		data.setUserId(model.getUserId());
		data.setConfig(this.jsonHandlingService.toJsonSafe(this.buildIndicatorAccessConfig(data, model.getConfig())));
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		IndicatorAccess persisted = this.builderFactory.builder(IndicatorAccessBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, IndicatorAccess._id, IndicatorAccess._hash), data);
		return persisted;
	}

	private IndicatorAccessConfigEntity buildIndicatorAccessConfig(IndicatorAccessEntity data, IndicatorAccessConfigPersist persist) {
		if (persist == null) return null;
		IndicatorAccessConfigEntity config = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, data.getConfig());
		if (config == null) {
			config = new IndicatorAccessConfigEntity();
		}

		if (persist.getGlobalFilterColumns() != null && !persist.getGroupFilterColumns().isEmpty()){
			List<FilterColumnConfigEntity> filterColumnConfigEntities = new ArrayList<>();
			for (FilterColumnConfigPersist filterColumnConfigPersist : persist.getGlobalFilterColumns()) {
				FilterColumnConfigEntity columnConfig = new FilterColumnConfigEntity();
				columnConfig.setColumn(filterColumnConfigPersist.getColumn());
				columnConfig.setValues(filterColumnConfigPersist.getValues());
				filterColumnConfigEntities.add(columnConfig);
			}
			config.setGlobalFilterColumns(filterColumnConfigEntities);
		}
		if (persist.getGroupFilterColumns() != null && !persist.getGroupFilterColumns().isEmpty()){
			Map<UUID, List<FilterColumnConfigEntity>> filterColumnConfigEntitiesMap = new HashMap<>();
			for (UUID groupId : persist.getGroupFilterColumns() .keySet()) {
				List<FilterColumnConfigPersist> filterColumnConfigEntitiesToPersist = persist.getGroupFilterColumns().get(groupId);
				if (filterColumnConfigEntitiesToPersist != null && !filterColumnConfigEntitiesToPersist.isEmpty()){
					List<FilterColumnConfigEntity> filterColumnConfigEntities = new ArrayList<>();
					for (FilterColumnConfigPersist filterColumnConfigPersist : filterColumnConfigEntitiesToPersist) {
						FilterColumnConfigEntity columnConfig = new FilterColumnConfigEntity();
						columnConfig.setColumn(filterColumnConfigPersist.getColumn());
						columnConfig.setValues(filterColumnConfigPersist.getValues());
						filterColumnConfigEntities.add(columnConfig);
					}
					filterColumnConfigEntitiesMap.put(groupId, filterColumnConfigEntities);
				}
			}
			config.setGroupFilterColumns(filterColumnConfigEntitiesMap.isEmpty() ? null : filterColumnConfigEntitiesMap);
		}

		return config;
	}

	public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug("deleting dataset: {}", id);

		this.authorizationService.authorizeForce(Permission.DeleteIndicatorAccess);

		this.deleterFactory.deleter(IndicatorAccessDeleter.class).deleteAndSaveByIds(Arrays.asList(new UUID[]{id}));
	}
}
