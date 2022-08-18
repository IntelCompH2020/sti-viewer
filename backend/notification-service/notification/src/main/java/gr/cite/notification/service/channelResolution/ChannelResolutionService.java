package gr.cite.notification.service.channelResolution;

import gr.cite.notification.common.enums.NotificationContactType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChannelResolutionService {

    List<NotificationContactType> resolve(UUID type);
    List<NotificationContactType> resolve(UUID type, UUID userId);
    Map<UUID, List<NotificationContactType>> resolve(UUID type, List<UUID> userIds);
}
