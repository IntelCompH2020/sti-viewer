package gr.cite.notification.service.user.settings;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.UserSettingsEntityType;
import gr.cite.notification.common.scope.user.UserScope;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.data.UserSettingsEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.model.UserSettings;
import gr.cite.notification.model.builder.UserSettingsBuilder;
import gr.cite.notification.model.persist.UserSettingsPersist;
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

@Service
@RequestScope
public class UserSettingsServiceImpl implements UserSettingsService {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserSettingsServiceImpl.class));
	private final TenantScopedEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;

	private final UserScope userScope;

	@Autowired
	public UserSettingsServiceImpl(
			TenantScopedEntityManager entityManager,
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

		boolean isUpdate = this.conventionService.isValidGuid(model.getId()) != null;

		UserSettingsEntity data;
		if (isUpdate) {
			data = this.entityManager.find(UserSettingsEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		} else {
			data = new UserSettingsEntity();
			data.setCreatedAt(Instant.now());
		}

		data.setType(model.getType());
		data.setKey(model.getKey());
		data.setValue(model.getValue());
		data.setEntityId(userScope.getUserIdSafe());
		data.setEntityType(UserSettingsEntityType.User);
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		return this.builderFactory.builder(UserSettingsBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, UserSettings._id, UserSettings._key), data);
	}

}
