package gr.cite.notification.service.userNotificationPreference;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.model.UserNotificationPreference;
import gr.cite.notification.model.persist.UserNotificationPreferencePersist;
import gr.cite.tools.fieldset.FieldSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UserNotificationPreferenceService {

    List<UserNotificationPreference> persist(UserNotificationPreferencePersist model, FieldSet fieldSet);
    NotifierListConfigurationDataContainer collectUserAvailableNotifierList(Set<UUID> notificationTypes);
    List<UserNotificationPreference> collectUserNotificationPreferences(UUID id);
    Map<UUID, List<UserNotificationPreference>> collectUserNotificationPreferences(List<UUID> ids);
}
