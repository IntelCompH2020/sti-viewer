package gr.cite.notification.service.user;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.data.UserEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.event.EventBroker;
import gr.cite.notification.event.UserTouchedEvent;
import gr.cite.notification.locale.LocaleService;
import gr.cite.notification.model.User;
import gr.cite.notification.model.builder.UserBuilder;
import gr.cite.notification.model.deleter.UserDeleter;
import gr.cite.notification.model.persist.UserPersist;
import gr.cite.notification.model.persist.UserTouchedIntegrationEventPersist;
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
import java.util.List;
import java.util.UUID;

@Service
@RequestScope
public class UserServiceImpl implements UserService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserServiceImpl.class));
	private final TenantScopedEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final EventBroker eventBroker;
	private final LocaleService localeService;

	@Autowired
	public UserServiceImpl(
			TenantScopedEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			EventBroker eventBroker,
			LocaleService localeService
	) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.eventBroker = eventBroker;
		this.localeService = localeService;
	}

	public User persist(UserPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting User").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditUser);

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		UserEntity data = null;
		if (isUpdate) {
			data = this.entityManager.find(UserEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			data = new UserEntity();
			data.setId(UUID.randomUUID());
			data.setIsActive(IsActive.ACTIVE);
			data.setCreatedAt(Instant.now());
		}
		String previousSubjectId = data.getSubjectId();

		data.setFirstName(model.getFirstName());
		data.setLastName(model.getLastName());
		data.setTimezone(model.getTimezone());
		data.setCulture(model.getCulture());
		data.setLanguage(model.getLanguage());
		data.setSubjectId(model.getSubjectId());
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		this.eventBroker.emit(new UserTouchedEvent(data.getId(), data.getSubjectId(), previousSubjectId));

		User persisted = this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, User._id, User._hash), data);
		return persisted;
	}

	public User persist(UserTouchedIntegrationEventPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting User").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditUser);

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		UserEntity data = null;
		if (isUpdate) {
			data = this.entityManager.find(UserEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		} else {
			data = new UserEntity();
			data.setId(model.getId());
			data.setIsActive(IsActive.ACTIVE);
			data.setCreatedAt(Instant.now());
		}
		String previousSubjectId = data.getSubjectId();

		data.setFirstName(model.getFirstName());
		data.setLastName(model.getLastName());
		data.setTimezone(localeService.timezoneName());
		data.setCulture(localeService.cultureName());
		data.setLanguage(localeService.language());
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		this.eventBroker.emit(new UserTouchedEvent(data.getId(), data.getSubjectId(), previousSubjectId));

		User persisted = this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, User._id, User._hash), data);
		return persisted;
	}

	public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug("deleting User: {}", id);

		this.authorizationService.authorizeForce(Permission.DeleteUser);

		this.deleterFactory.deleter(UserDeleter.class).deleteAndSaveByIds(List.of(id));
	}
}
