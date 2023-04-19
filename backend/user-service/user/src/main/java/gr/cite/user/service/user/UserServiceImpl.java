package gr.cite.user.service.user;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.user.authorization.AuthorizationFlags;
import gr.cite.user.authorization.OwnedResource;
import gr.cite.user.authorization.Permission;
import gr.cite.user.common.enums.ContactInfoType;
import gr.cite.user.common.enums.IsActive;
import gr.cite.user.common.enums.UserContactType;
import gr.cite.user.common.scope.tenant.TenantScope;
import gr.cite.user.common.scope.user.UserScope;
import gr.cite.user.convention.ConventionService;
import gr.cite.user.data.*;
import gr.cite.user.errorcode.ErrorThesaurusProperties;
import gr.cite.user.event.EventBroker;
import gr.cite.user.event.UserTouchedEvent;
import gr.cite.user.locale.LocaleService;
import gr.cite.user.model.User;
import gr.cite.user.model.UserContactInfo;
import gr.cite.user.model.builder.UserBuilder;
import gr.cite.user.model.deleter.UserDeleter;
import gr.cite.user.model.persist.*;
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
import gr.cite.user.query.TenantUserQuery;
import gr.cite.user.query.UserContactInfoQuery;
import gr.cite.user.query.UserQuery;
import gr.cite.user.service.user.contactinfo.UserContactInfoService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequestScope
public class UserServiceImpl implements UserService {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserServiceImpl.class));
    private final TenantEntityManager entityManager;
    private final AuthorizationService authorizationService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final QueryFactory queryFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;
    private final EventBroker eventBroker;
    private final LocaleService localeService;
    private final UserContactInfoService contactInfoService;
    private final UserScope userScope;
    private final TenantScope tenantScope;
    private final ClaimExtractor claimExtractor;


    @Autowired
    public UserServiceImpl(
            TenantEntityManager entityManager,
            AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            QueryFactory queryFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource,
            EventBroker eventBroker,
            LocaleService localeService,
            TenantScope tenantScope, UserContactInfoService contactInfoService, UserScope userScope, ClaimExtractor claimExtractor) {
        this.entityManager = entityManager;
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.queryFactory = queryFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.eventBroker = eventBroker;
        this.localeService = localeService;
        this.contactInfoService = contactInfoService;
        this.userScope =userScope;
        this.tenantScope = tenantScope;
        this.claimExtractor = claimExtractor;
    }

    public User persist(UserPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting User").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditUser);

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        UserEntity data = null;
        if (isUpdate) {
            data = this.entityManager.find(UserEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            data = new UserEntity();
            data.setId(UUID.randomUUID());
            data.setIsActive(IsActive.ACTIVE);
            data.setCreatedAt(Instant.now());
        }

        this.setModelDataToEntityIfNotNull(data, model);
        data.setUpdatedAt(Instant.now());

        if (isUpdate) data = this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        if (model.getContacts() != null && !model.getContacts().isEmpty()) {
            UserContactInfoPatch contactInfo = new UserContactInfoPatch();
            contactInfo.setId(data.getId());
            contactInfo.setHash(conventionService.hashValue(data.getUpdatedAt()));
            contactInfo.setContacts(model.getContacts());
            this.persistContactsInfo(contactInfo, null);
        }

        this.eventBroker.emit(new UserTouchedEvent(data.getId(), data.getSubjectId(), data.getSubjectId()));

        return this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, User._id, User._hash), data);
    }

    public User persist(UserTouchedIntegrationEventPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting User").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditUser);

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        UserEntity data = null;
        if (isUpdate) {
            data = this.entityManager.find(UserEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));
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

        User persisted = this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, User._id, User._hash), data);
        return persisted;
    }

    public User updateLanguage(UserProfileLanguagePatch model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting User Language").And("model", model).And("fields", fields));

        this.authorizationService.authorizeAtLeastOne(List.of(new OwnedResource(model.getId())));

        UserPersist modelToUpdate = new UserPersist();
        modelToUpdate.setLanguage(model.getLanguage());
        modelToUpdate.setId(model.getId());

        logger.debug(new MapLogEntry("persisting User language").And("model", model).And("fields", fields));

        Boolean isValid = this.conventionService.isValidGuid(model.getId());
        if (!isValid)
            throw new MyValidationException(this.errors.getModelValidation().getCode(), this.errors.getModelValidation().getMessage());

        UserEntity data = this.entityManager.find(UserEntity.class, model.getId());
        if (data == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.setModelDataToEntityIfNotNull(data, modelToUpdate);
        data.setUpdatedAt(Instant.now());

        data = this.entityManager.merge(data);

        this.entityManager.flush();

        this.eventBroker.emit(new UserTouchedEvent(data.getId(), data.getSubjectId(), data.getSubjectId()));

        return this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, User._id, User._hash), data);

    }

    public User updateName(UserNamePatch model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting User name ").And("model", model).And("fields", fields));

        this.authorizationService.authorizeAtLeastOne(List.of(new OwnedResource(model.getId())));

        UserPersist modelToUpdate = new UserPersist();
        modelToUpdate.setId(model.getId());
        modelToUpdate.setHash(model.getHash());
        modelToUpdate.setFirstName(model.getFirstName());
        modelToUpdate.setLastName(model.getLastName());

        Boolean isValid = this.conventionService.isValidGuid(model.getId());
        if (!isValid)
            throw new MyValidationException(this.errors.getModelValidation().getCode(), this.errors.getModelValidation().getMessage());
        UserEntity data = this.entityManager.find(UserEntity.class, model.getId());
        if (data == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
            throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

        this.setModelDataToEntityIfNotNull(data, modelToUpdate);
        data.setUpdatedAt(Instant.now());

        data = this.entityManager.merge(data);

        this.entityManager.flush();

        this.eventBroker.emit(new UserTouchedEvent(data.getId(), data.getSubjectId(), data.getSubjectId()));

        return this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, User._id, User._hash), data);
    }

    public User persistUserContactInfo(UserContactInfoPatch model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {

        this.authorizationService.authorizeAtLeastOne(List.of(new OwnedResource(model.getId())));

        boolean isValid = this.conventionService.isValidGuid(model.getId());
        if (!isValid)
            throw new MyValidationException(this.errors.getModelValidation().getCode(), this.errors.getModelValidation().getMessage());

        UserEntity user = this.queryFactory.query(UserQuery.class).authorize(AuthorizationFlags.OwnerOrPermission).ids(model.getId()).first();

        if (user == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        List<UserContactInfoPersist> existingContact = this.queryFactory.query(UserContactInfoQuery.class).userIds(user.getId())
                .collect()
                .stream().filter(x -> !x.getType().equals(UserContactType.Email)).map(this::userContactEntityToPersist).collect(Collectors.toList());

        if (!this.conventionService.hashValue(user.getUpdatedAt()).equals(model.getHash()))
            throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

        Set<UserContactType> newContactInfoSet = model.getContacts().stream().filter(x -> x.getType() != null).map(UserContactInfoPersist::getType).collect(Collectors.toSet());

        List<UserContactInfoPersist> toDelete = existingContact.stream().filter(x -> !newContactInfoSet.contains(x.getType())).collect(Collectors.toList());
        List<UserContactInfoPersist> toAdd = new ArrayList<>();
        List<UserContactInfoPersist> toUpdate = new ArrayList<>();

        UUID userId = user.getId();


        for (UserContactInfoPersist m : model.getContacts()) {

            Optional<UserContactInfoPersist> exist = existingContact.stream().filter(x -> x.getType().equals(m.getType())).findFirst();
            if (exist.isPresent() && Objects.equals(exist.get().getValue(), m.getValue())) continue;
            else if (exist.isPresent()) {
                exist.get().setValue(m.getValue());
                toUpdate.add(exist.get());
            } else {
                UserContactInfoPersist contactInfoToAdd = new UserContactInfoPersist();
                contactInfoToAdd.setUserId(userId);
                contactInfoToAdd.setValue(m.getValue());
                contactInfoToAdd.setType(m.getType());

                Optional<TenantUserEntity> tenant = this.queryFactory.query(TenantUserQuery.class).userIds(userId).collect().stream().findFirst();
                tenant.ifPresent(tenantEntity -> {
                    contactInfoToAdd.setTenantId(tenantEntity.getId());
                });
                toAdd.add(contactInfoToAdd);
            }

        }
        this.contactInfoService.deleteAndSave(toDelete);
        this.contactInfoService.batchPersist(toAdd, fields);
        this.contactInfoService.batchPersist(toUpdate, fields);

        return this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, User._id,
                User._hash,
                User._contacts + "." + UserContactInfo._value,
                User._contacts + "." + UserContactInfo._type), user);

    }

    public User updateUserProfile(UserProfilePatch model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting User Profile").And("model", model).And("fields", fields));

        this.authorizationService.authorizeAtLeastOne(List.of(new OwnedResource(model.getId())));

        UserPersist modelToUpdate = new UserPersist();
        modelToUpdate.setId(model.getId());
        modelToUpdate.setHash(model.getHash());
        modelToUpdate.setCulture(model.getCulture());
        modelToUpdate.setTimezone(model.getTimezone());
        modelToUpdate.setLanguage(model.getLanguage());

        Boolean isValid = this.conventionService.isValidGuid(model.getId());
        if (!isValid)
            throw new MyValidationException(this.errors.getModelValidation().getCode(), this.errors.getModelValidation().getMessage());
        UserEntity data = this.entityManager.find(UserEntity.class, model.getId());
        if (data == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
            throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

        this.setModelDataToEntityIfNotNull(data, modelToUpdate);
        data.setUpdatedAt(Instant.now());

        data = this.entityManager.merge(data);

        this.entityManager.flush();

        this.eventBroker.emit(new UserTouchedEvent(data.getId(), data.getSubjectId(), data.getSubjectId()));

        return this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, User._id, User._hash), data);
    }

    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting User: {}", id);

        this.authorizationService.authorizeForce(Permission.DeleteUser);

        this.deleterFactory.deleter(UserDeleter.class).deleteAndSaveByIds(List.of(id));
    }

    private void persistContactsInfo(UserContactInfoPatch model, FieldSet fields) throws InvalidApplicationException {
        if(model == null) return;

        this.authorizationService.authorizeAtLeastOne(List.of(new OwnedResource(model.getId())));

        boolean isValid = this.conventionService.isValidGuid(model.getId());
        if (!isValid)
            throw new MyValidationException(this.errors.getModelValidation().getCode(), this.errors.getModelValidation().getMessage());

        UserEntity user = this.queryFactory.query(UserQuery.class).authorize(AuthorizationFlags.OwnerOrPermission).ids(model.getId()).first();
        if (user == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        UUID userId = user.getId();
        List<String> emails = model.getContacts().stream().filter(x->x.getType().name().equals(ContactInfoType.Email.name()))
                .map(UserContactInfoPersist::getValue).collect(Collectors.toList());
        for(String email :emails){

            List<UUID> userIds= this.queryFactory.query(UserContactInfoQuery.class).values(email).collect().stream().map(UserContactInfoEntity::getUserId).collect(Collectors.toList());
            boolean isUnique = userIds.stream().noneMatch(x -> x.equals(model.getId()));
            logger.debug("email {} excluding {} is unique: {}", email,model.getId(),isUnique);
            if(!isUnique) throw new MyValidationException(this.errors.getModelValidation().getCode(),this.errors.getModelValidation().getMessage());
        }
        if(!userId.equals(model.getId())) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        if (!this.conventionService.hashValue(user.getUpdatedAt()).equals(model.getHash()))
            throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

        List<UserContactInfoPersist> existingContact = this.queryFactory.query(UserContactInfoQuery.class).userIds(user.getId())
                .collect()
                .stream().map(this::userContactEntityToPersist).collect(Collectors.toList());

        Set<UserContactType> newContactInfoSet = model.getContacts().stream().map(UserContactInfoPersist::getType).filter(Objects::nonNull).collect(Collectors.toSet());

        List<UserContactInfoPersist> toDelete = existingContact.stream().filter(x -> !newContactInfoSet.contains(x.getType())).collect(Collectors.toList());
        List<UserContactInfoPersist> toAdd = new ArrayList<>();
        List<UserContactInfoPersist> toUpdate = new ArrayList<>();

        for (UserContactInfoPersist m : model.getContacts()) {

            Optional<UserContactInfoPersist> exist = existingContact.stream().filter(x -> x.getType().equals(m.getType())).findFirst();
            if (exist.isPresent() && Objects.equals(exist.get().getValue(), m.getValue())) continue;
            else if (exist.isPresent()) {
                exist.get().setValue(m.getValue());
                toUpdate.add(exist.get());
            } else {
                UserContactInfoPersist contactInfoToAdd = new UserContactInfoPersist();
                contactInfoToAdd.setUserId(userId);
                contactInfoToAdd.setValue(m.getValue());
                contactInfoToAdd.setType(m.getType());

                Optional<TenantUserEntity> tenant = this.queryFactory.query(TenantUserQuery.class).userIds(userId).collect().stream().findFirst();
                tenant.ifPresent(tenantEntity -> {
                    contactInfoToAdd.setTenantId(tenantEntity.getTenantId());
                });
                toAdd.add(contactInfoToAdd);
            }

        }
        this.contactInfoService.deleteAndSave(toDelete);
        this.contactInfoService.batchPersist(toAdd, fields);
        this.contactInfoService.batchPersist(toUpdate, fields);

    }

    private UserContactInfoPersist userContactEntityToPersist(UserContactInfoEntity data) {
        UserContactInfoPersist persist = new UserContactInfoPersist();
        persist.setUserId(data.getUserId());
        persist.setType(data.getType());
        persist.setValue(data.getValue());
        persist.setTenantId(data.getTenantId());
        return persist;
    }

    private void setModelDataToEntityIfNotNull(UserEntity data, UserPersist model) {
        if (model.getFirstName() != null) data.setFirstName(model.getFirstName());
        if (model.getLastName() != null) data.setLastName(model.getLastName());
        if (model.getTimezone() != null) data.setTimezone(model.getTimezone());
        if (model.getCulture() != null) data.setCulture(model.getCulture());
        if (model.getLanguage() != null) data.setLanguage(model.getLanguage());
        //if (model.getSubjectId() != null) data.setSubjectId(model.getSubjectId());
    }
}
