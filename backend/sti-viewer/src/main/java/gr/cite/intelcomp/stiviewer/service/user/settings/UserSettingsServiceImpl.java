package gr.cite.intelcomp.stiviewer.service.user.settings;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.model.builder.UserSettingsBuilder;
import gr.cite.intelcomp.stiviewer.model.persist.UserSettingsPersist;
import gr.cite.intelcomp.stiviewer.service.dataset.DatasetServiceImpl;
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
import java.util.UUID;

@Service
@RequestScope
public class UserSettingsServiceImpl implements UserSettingsService {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DatasetServiceImpl.class));
	private final TenantEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;

	private final UserScope userScope;

	@Autowired
	public UserSettingsServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			UserScope userScope) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.userScope = userScope;
	}

	@Override
	public UserSettings persist(UserSettingsPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting data access request").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditUserSettings);

		boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		UserSettingsEntity data;
		if (isUpdate) {
			data = this.entityManager.find(UserSettingsEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		} else {
			data = new UserSettingsEntity();
			data.setCreatedAt(Instant.now());
			data.setId(UUID.randomUUID());

		}

        data.setName(model.getKey());
		data.setType(model.getType());
		data.setKey(model.getKey());
		data.setValue(model.getValue());
		data.setEntityId(model.getEntityId());
		data.setEntityType(model.getEntityType());
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		return this.builderFactory.builder(UserSettingsBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, UserSettings._id, UserSettings._key), data);
	}

}
