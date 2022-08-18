package gr.cite.notification.config.notification;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.schedule.NotificationScheduleTask;
import gr.cite.notification.service.message.common.MessageBuilderBase;
import gr.cite.notification.service.notificationscheduling.NotificationSchedulingService;
import gr.cite.notification.service.notificationscheduling.NotificationSchedulingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationConfig {

    public static class BeanQualifier {
        public static final String GLOBAL_POLICIES_MAP = "globalPoliciesMap";
        public static final String FLOW_MAP = "flowMap";
        public static final String STATIC_FIELD_LIST = "staticFieldList";
        public static final String CIPHER_FIELDS = "cipherFields";

    }
    private final ApplicationContext applicationContext;
    private final NotificationProperties properties;

    @Autowired
    public NotificationConfig(ApplicationContext applicationContext, NotificationProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Bean(BeanQualifier.GLOBAL_POLICIES_MAP)
    public Map<UUID, List<NotificationContactType>> getGlobalPoliciesMap() {
        return properties.getResolver().getGlobalPolicies().stream()
                .collect(Collectors.toMap(NotificationProperties.Resolver.GlobalPolicy::getType,
                        NotificationProperties.Resolver.GlobalPolicy::getContacts));
    }

    @Bean(BeanQualifier.FLOW_MAP)
    public Map<String, Map<UUID, NotificationProperties.Flow>> getFlowMap() {
        return properties.getMessage().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        stringTemplateEntry -> stringTemplateEntry.getValue().getFlows()
                                .stream().collect(Collectors.toMap(NotificationProperties.Flow::getKey, flow -> flow))));
    }

    @Bean(BeanQualifier.STATIC_FIELD_LIST)
    public List<NotificationProperties.Field> getStaticFieldList() {
        return properties.getMessage().get("staticFields") != null ? new ArrayList<>(properties.getMessage()
                .get("staticFields").getFields().values()) : new ArrayList<>();
    }

    @Bean(BeanQualifier.CIPHER_FIELDS)
    public Map<UUID, MessageBuilderBase.FieldCiphering> getFieldCiphers() {
        return properties.getMessage().values().stream().flatMap(template -> template.getFlows().stream())
                .filter(flow -> flow.getCipherFields() != null && !flow.getCipherFields().isEmpty())
                .collect(Collectors.toMap(NotificationProperties.Flow::getKey, flow -> new MessageBuilderBase.FieldCiphering(flow.getCipherFields())));
    }

    @Bean
    public NotificationSchedulingService notificationSchedulingService() {
        return new NotificationSchedulingServiceImpl(this.applicationContext, this.properties);
    }

    @Bean
    @ConditionalOnProperty(name = "notification.task.processor.enable", havingValue = "true")
    public NotificationScheduleTask notificationScheduleTask() {
        return new NotificationScheduleTask(this.applicationContext, this.properties);
    }
}
