package gr.cite.intelcomp.stiviewer.service.dataset;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DatasetEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.Dataset;
import gr.cite.intelcomp.stiviewer.model.builder.DatasetBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.DatasetDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.DatasetPersist;
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
import java.util.Arrays;
import java.util.UUID;

@Service
@RequestScope
public class DatasetServiceImpl implements DatasetService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DatasetServiceImpl.class));

	private final TenantEntityManager entityManager;

	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;

	@Autowired
	public DatasetServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource
	) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
	}

	public Dataset persist(DatasetPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting dataset").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditDataset);

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		DatasetEntity data = null;
		if (isUpdate) {
			data = this.entityManager.find(DatasetEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), Dataset.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			data = new DatasetEntity();
			data.setId(UUID.randomUUID());
			data.setIsActive(IsActive.ACTIVE);
			data.setCreatedAt(Instant.now());
		}

		data.setTitle(model.getTitle());
		data.setNotes(model.getNotes());
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		Dataset persisted = this.builderFactory.builder(DatasetBuilder.class).build(BaseFieldSet.build(fields, Dataset._id, Dataset._hash), data);
		return persisted;
	}

	public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug("deleting dataset: {}", id);

		this.authorizationService.authorizeForce(Permission.DeleteDataset);

		this.deleterFactory.deleter(DatasetDeleter.class).deleteAndSaveByIds(Arrays.asList(new UUID[]{id}));
	}
}
