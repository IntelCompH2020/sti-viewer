package gr.cite.notification.service.channelResolution;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.config.notification.NotificationConfig;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.model.UserNotificationPreference;
import gr.cite.notification.service.tenantconfiguration.TenantConfigurationService;
import gr.cite.notification.service.userNotificationPreference.UserNotificationPreferenceService;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequestScope
public class ChannelResolutionServiceImpl implements ChannelResolutionService{
    private static final LoggerService loggerService = new LoggerService(LoggerFactory.getLogger(ChannelResolutionServiceImpl.class));

    private final ErrorThesaurusProperties errors;
    private final Map<UUID, List<NotificationContactType>> globalPoliciesMap;
    private final TenantConfigurationService tenantConfigurationService;
    private final UserNotificationPreferenceService userNotificationPreferenceService;

    @Autowired
    public ChannelResolutionServiceImpl(ErrorThesaurusProperties errors,
                                        @Qualifier(NotificationConfig.BeanQualifier.GLOBAL_POLICIES_MAP)
                                        Map<UUID, List<NotificationContactType>> globalPoliciesMap,
                                        TenantConfigurationService tenantConfigurationService, UserNotificationPreferenceService userNotificationPreferenceService) {
        this.errors = errors;
        this.globalPoliciesMap = globalPoliciesMap;
        this.tenantConfigurationService = tenantConfigurationService;
        this.userNotificationPreferenceService = userNotificationPreferenceService;
    }

    private List<NotificationContactType> lookupGlobalPolicy(UUID type) {
        if (!this.globalPoliciesMap.containsKey(type)) throw new MyApplicationException(this.errors.getSystemError().getCode(), this.errors.getSystemError().getMessage());
        return this.globalPoliciesMap.get(type);
    }

    @Override
    public List<NotificationContactType> resolve(UUID type) {
        return this.lookupGlobalPolicy(type);
    }

    @Override
    public List<NotificationContactType> resolve(UUID type, UUID userId) {
        if (userId == null) return this.resolve(type);

        Map<UUID, List<NotificationContactType>> resolved = this.resolve(type, List.of(userId));
        if (resolved == null || !resolved.containsKey(userId)) return new ArrayList<>();
        return resolved.get(userId);
    }

    @Override
    public Map<UUID, List<NotificationContactType>> resolve(UUID type, List<UUID> userIds) {
        List<UUID> users = userIds.stream().distinct().collect(Collectors.toList());

        List<NotificationContactType> globals = this.lookupGlobalPolicy(type);
        List<NotificationContactType> tenantPolicies = this.lookupOrCollectTenantPolicies(type);
        Map<UUID, List<NotificationContactType>> userPolicies = this.lookupOrCollectUserPolicies(users, type);
        Map<UUID, List<NotificationContactType>> curatedUserPolicies = new HashMap<>();
        for (UUID userId: users)
        {
            List<NotificationContactType> curatedTypes = null;
            if (userPolicies == null || !userPolicies.containsKey(userId))
            {
                if (tenantPolicies != null)
                {
                    curatedTypes = tenantPolicies;
                }
                else
                {
                    curatedTypes = globals;
                }
            }
            else
            {
                curatedTypes = new ArrayList<>();
                for (NotificationContactType userPolicy : userPolicies.get(userId))
                {
                    if (globals == null || !globals.contains(userPolicy)) continue;
                    if (tenantPolicies != null && !tenantPolicies.contains(userPolicy)) continue;
                    curatedTypes.add(userPolicy);
                }
            }
            curatedUserPolicies.put(userId, curatedTypes);
        }

        return curatedUserPolicies;
    }

    private List<NotificationContactType> lookupOrCollectTenantPolicies(UUID type) {
        NotifierListConfigurationDataContainer container = this.tenantConfigurationService.collectTenantNotifierList();
        if (container == null || container.getNotifiers() == null) return null;
        return container.getNotifiers().get(type);
    }

    private Map<UUID, List<NotificationContactType>> lookupOrCollectUserPolicies(List<UUID> users, UUID type) {
        Map<UUID, List<NotificationContactType>> contactsByUser = new HashMap<>();
        Map<UUID, List<UserNotificationPreference>> userNotificationPreferences = this.userNotificationPreferenceService.collectUserNotificationPreferences(users);
        for (Map.Entry<UUID, List<UserNotificationPreference>> notificationPreference: userNotificationPreferences.entrySet())
        {
            contactsByUser.put(notificationPreference.getKey(), notificationPreference.getValue().stream().filter(x -> x.getType() != null && x.getType() == type && x.getChannel() != null).sorted(Comparator.comparingInt(x -> x.getOrdinal())).map(x -> x.getChannel()).collect(Collectors.toList()));
        }
        return contactsByUser;
    }
}
