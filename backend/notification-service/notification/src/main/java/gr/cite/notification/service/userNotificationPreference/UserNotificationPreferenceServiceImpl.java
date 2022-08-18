package gr.cite.notification.service.userNotificationPreference;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.OwnedResource;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.config.notification.NotificationConfig;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.data.UserNotificationPreferenceEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.model.UserNotificationPreference;
import gr.cite.notification.model.builder.UserNotificationPreferenceBuilder;
import gr.cite.notification.model.persist.UserNotificationPreferencePersist;
import gr.cite.notification.query.UserNotificationPreferenceQuery;
import gr.cite.notification.service.tenantconfiguration.TenantConfigurationService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequestScope
public class UserNotificationPreferenceServiceImpl implements UserNotificationPreferenceService{
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserNotificationPreferenceServiceImpl.class));

    private final QueryFactory queryFactory;
    private final BuilderFactory builderFactory;
    private final AuthorizationService authService;
    private final TenantConfigurationService tenantConfigurationService;
    private final Map<UUID, List<NotificationContactType>> globalPoliciesMap;
    private final ErrorThesaurusProperties errors;
    private final TenantScopedEntityManager entityManager;

    @Autowired
    public UserNotificationPreferenceServiceImpl(QueryFactory queryFactory,
                                                 BuilderFactory builderFactory,
                                                 AuthorizationService authService,
                                                 TenantConfigurationService tenantConfigurationService,
                                                 @Qualifier(NotificationConfig.BeanQualifier.GLOBAL_POLICIES_MAP)
                                                 Map<UUID, List<NotificationContactType>> globalPoliciesMap,
                                                 ErrorThesaurusProperties errors, TenantScopedEntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
        this.authService = authService;
        this.tenantConfigurationService = tenantConfigurationService;
        this.globalPoliciesMap = globalPoliciesMap;
        this.errors = errors;
        this.entityManager = entityManager;
    }

    @Override
    public List<UserNotificationPreference> persist(UserNotificationPreferencePersist model, FieldSet fieldSet) {
        logger.debug(new MapLogEntry("persisting").And("model", model).And("fields", fieldSet));

        this.authService.authorizeForce(Permission.EditUserNotificationPreference);

        Map<UUID, List<NotificationContactType>> currentNotificationListPolicies;
        NotifierListConfigurationDataContainer tenantNotifierListPolicies = this.tenantConfigurationService.collectTenantNotifierList();
        if (tenantNotifierListPolicies != null)
        {
            currentNotificationListPolicies = mergeNotifierPolicies(tenantNotifierListPolicies.getNotifiers(), this.globalPoliciesMap);
        }
        else
        {
            currentNotificationListPolicies = this.globalPoliciesMap;
        }

        if (model.getNotificationPreferences().entrySet().stream().anyMatch(entry -> model.getNotificationPreferences().get(entry.getKey()).stream().anyMatch(contactType -> !currentNotificationListPolicies.get(entry.getKey()).contains(contactType))))
        {
            throw new MyValidationException(this.errors.getOverlappingTenantConfigurationNotifierList().getCode(), this.errors.getOverlappingTenantConfigurationNotifierList().getMessage());
        }

        List<UserNotificationPreferenceEntity> datas = this.patchAndSave(List.of(model));
        return this.builderFactory.builder(UserNotificationPreferenceBuilder.class).build(fieldSet, datas);
    }

    @Override
    public NotifierListConfigurationDataContainer collectUserAvailableNotifierList(Set<UUID> notificationTypes) {
        Map<UUID, List<NotificationContactType>> currentNotificationListPolicies;
        NotifierListConfigurationDataContainer tenantNotifierListPolicies = this.tenantConfigurationService.collectTenantNotifierList();
        if (tenantNotifierListPolicies != null)
        {
            currentNotificationListPolicies = mergeNotifierPolicies(tenantNotifierListPolicies.getNotifiers(), this.globalPoliciesMap);
        }
        else
        {
            currentNotificationListPolicies = this.globalPoliciesMap;
        }

        if (notificationTypes != null && !notificationTypes.isEmpty())
        {
            return new NotifierListConfigurationDataContainer(currentNotificationListPolicies
                    .entrySet().stream()
                    .filter(x -> notificationTypes.contains(x.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        else return new NotifierListConfigurationDataContainer(currentNotificationListPolicies);
    }

    @Override
    public List<UserNotificationPreference> collectUserNotificationPreferences(UUID id) {
        Map<UUID, List<UserNotificationPreference>> result = this.collectUserNotificationPreferences(List.of(id));
        if (result != null) {
            return result.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        logger.error("failed to collect user notification preferences for user " + id);
        return new ArrayList<>();
    }

    @Override
    public Map<UUID, List<UserNotificationPreference>> collectUserNotificationPreferences(List<UUID> ids) {
        return this.builderFactory.builder(UserNotificationPreferenceBuilder.class)
                        .build(new BaseFieldSet(UserNotificationPreference.Field.USER_ID, UserNotificationPreference.Field.TYPE,
                                UserNotificationPreference.Field.CHANNEL, UserNotificationPreference.Field.ORDINAL), this.queryFactory
                                .query(UserNotificationPreferenceQuery.class)
                                .userId(ids).collect()).stream().collect(Collectors.groupingBy(UserNotificationPreference::getUserId)); //GK: Yep that exist on JAVA Streams
    }

    private Map<UUID, List<NotificationContactType>> mergeNotifierPolicies(Map<UUID, List<NotificationContactType>> overrides, Map<UUID, List<NotificationContactType>> bases)
    {
        Map<UUID, List<NotificationContactType>> mergedPolicies = new HashMap<>();
        for(Map.Entry<UUID, List<NotificationContactType>> policyEntry:  bases.entrySet())
        {
            if (overrides.containsKey(policyEntry.getKey()))
            {
                List<NotificationContactType> notifierList = overrides.get(policyEntry.getKey());
                if(notifierList == null) throw new MyApplicationException(this.errors.getSystemError().getCode(), this.errors.getSystemError().getMessage());
                mergedPolicies.put(policyEntry.getKey(), notifierList);
            }
            else
            {
                mergedPolicies.put(policyEntry.getKey(), policyEntry.getValue());
            }
        }
        return mergedPolicies;
    }

    private List<UserNotificationPreferenceEntity> patchAndSave(List<UserNotificationPreferencePersist> models)
    {
        List<UserNotificationPreferenceEntity> datas = new ArrayList<>();
        for (UserNotificationPreferencePersist model: models)
        {
            for (Map.Entry<UUID, List<NotificationContactType>> notificationPreference: model.getNotificationPreferences().entrySet())
            {
                this.patchAndSave(model.getUserId(), notificationPreference.getKey(), notificationPreference.getValue());
            }
        }
        return datas;
    }

    private List<UserNotificationPreferenceEntity> patchAndSave(UUID userId, UUID type, List<NotificationContactType> contactTypes)
    {
        List<UserNotificationPreferenceEntity> preferences = null;
        try {
            preferences = this.queryFactory
                    .query(UserNotificationPreferenceQuery.class)
                    .type(type)
                    .userId(userId).collect();
            int ordinal = 0;

            List<UserNotificationPreferenceEntity> updatedPreferences = new ArrayList<>();
            for (NotificationContactType contactType : contactTypes) {
                UserNotificationPreferenceEntity preference = preferences.stream().filter(x -> x.getChannel() == contactType).findFirst().orElse(null);

                if (preference != null) {
                    preference.setOrdinal(ordinal);

                } else {
                    preference = new UserNotificationPreferenceEntity();
                    preference.setUserId(userId);
                    preference.setType(type);
                    preference.setOrdinal(ordinal);
                    preference.setChannel(contactType);
                    preference.setCreatedAt(Instant.now());
                }
                this.entityManager.merge(preference);
                this.entityManager.persist(preference);
                updatedPreferences.add(preference);
                ordinal++;
            }
            List<UserNotificationPreferenceEntity> toDelete = preferences.stream().filter(x -> !updatedPreferences.stream().map(y-> y.getChannel()).collect(Collectors.toList()).contains(x.getChannel())).collect(Collectors.toList());
            for (UserNotificationPreferenceEntity deletable: toDelete) {
                this.entityManager.remove(deletable);
            }
        } catch (InvalidApplicationException e) {
            logger.error(e.getMessage(), e);
        }
        return preferences;
    }
}
