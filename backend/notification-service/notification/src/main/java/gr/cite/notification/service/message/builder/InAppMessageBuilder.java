package gr.cite.notification.service.message.builder;

import gr.cite.notification.cache.NotificationTemplateCache;
import gr.cite.notification.common.StringUtils;
import gr.cite.notification.common.enums.InAppNotificationPriority;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.types.notification.FieldInfo;
import gr.cite.notification.config.notification.NotificationConfig;
import gr.cite.notification.config.notification.NotificationProperties;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.service.formatting.FormattingService;
import gr.cite.notification.service.message.common.MessageBuilderBase;
import gr.cite.notification.service.message.infobuilder.MessageInfoBuilderService;
import gr.cite.notification.service.message.model.InAppMessage;
import gr.cite.notification.service.message.model.Message;
import gr.cite.notification.service.message.model.MessageInfo;
import gr.cite.tools.cipher.CipherService;
import gr.cite.tools.cipher.config.CipherProfileProperties;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequestScope
public class InAppMessageBuilder extends MessageBuilderBase implements MessageBuilder{
    private final static LoggerService logger = new LoggerService(LoggerFactory.getLogger(InAppMessageBuilder.class));
    private final MessageInfoBuilderService messageInfoBuilderService;
    private final Map<String, Map<UUID, NotificationProperties.Flow>> flowMap;
    private final NotificationProperties properties;

    @Autowired
    public InAppMessageBuilder(MessageInfoBuilderService messageInfoBuilderService,
                               @Qualifier(NotificationConfig.BeanQualifier.FLOW_MAP)
                               Map<String, Map<UUID, NotificationProperties.Flow>> flowMap,
                               ErrorThesaurusProperties errors, NotificationProperties properties,
                               CipherService cipherService,
                               CipherProfileProperties cipherProfileProperties,
                               FormattingService formattingService,
                               NotificationTemplateCache cache) {
        super(logger, errors, cipherService, cipherProfileProperties, formattingService, cache);
        this.messageInfoBuilderService = messageInfoBuilderService;
        this.flowMap = flowMap;
        this.properties = properties;
    }

    @Override
    public Message buildMessage(NotificationEntity notification) {
        MessageInfo messageInfo = this.messageInfoBuilderService.buildMessageInfo(notification);
        if (messageInfo == null)
        {
            logger.error("Could not retrieve message info for notification " + notification.getId());
            return null;
        }

        NotificationProperties.Flow options = this.flowMap.get("in-app").getOrDefault(notification.getType(), null);

        if (options == null)
        {
            logger.error("Could not retrieve flow options for notification " + notification.getId() + " of type " + notification.getType());
            return null;
        }

        MessageBuilderBase.FieldCiphering ciphering = options.getCipherFields() == null || options.getCipherFields().isEmpty() ? new MessageBuilderBase.FieldCiphering() : new MessageBuilderBase.FieldCiphering(options.getCipherFields());

        String subjectTemplate = null;
        if (!StringUtils.isNullOrEmpty(options.getSubjectKey())) subjectTemplate = messageInfo.getFields().stream().filter(x -> x.getKey().equals(options.getSubjectKey())).findFirst().orElse(new FieldInfo()).getValue();
        if (StringUtils.isNullOrEmpty(subjectTemplate)) subjectTemplate = this.lookupOrReadLocalizedFile(this.getTemplate().getTemplateCache(), options.getSubjectPath(), messageInfo.getLanguage());
        FieldFormatting subjectFormatting = this.buildFieldFormatting(options.getSubjectFieldOptions());
        ReplaceResult subjectResult = this.buildTemplate(notification.getId(), subjectTemplate, messageInfo, options.getSubjectFieldOptions(), subjectFormatting, ciphering);

        String bodyTemplate = null;
        if (!StringUtils.isNullOrEmpty(options.getBodyKey())) bodyTemplate = messageInfo.getFields().stream().filter(x -> x.getKey().equals(options.getBodyKey())).findFirst().orElse(new FieldInfo()).getValue();
        if (StringUtils.isNullOrEmpty(bodyTemplate)) bodyTemplate = this.lookupOrReadLocalizedFile(this.getTemplate().getTemplateCache(), options.getBodyPath(), messageInfo.getLanguage());
        FieldFormatting bodyFormatting = this.buildFieldFormatting(options.getBodyFieldOptions());
        ReplaceResult bodyResult = this.buildTemplate(notification.getId(), bodyTemplate, messageInfo, options.getBodyFieldOptions(), bodyFormatting, ciphering);

        String priorityString = "";

        if (!StringUtils.isNullOrEmpty(options.getPriorityKey())) priorityString = messageInfo.getFields().stream().filter(x -> x.getKey().equals(options.getPriorityKey())).findFirst().orElse(new FieldInfo()).getValue();
        InAppNotificationPriority inAppNotificationPriority = InAppNotificationPriority.NORMAL;
        if (!StringUtils.isNullOrEmpty(priorityString)) {
            inAppNotificationPriority = InAppNotificationPriority.valueOf(priorityString.toLowerCase());
        }

        List<FieldInfo> extraData = null;
        if (options.getExtraDataKeys() != null && !options.getExtraDataKeys().isEmpty()) extraData = messageInfo.getFields().stream().filter(x -> options.getExtraDataKeys().contains(x.getKey())).collect(Collectors.toList());

        return new InAppMessage(subjectResult.getText(), bodyResult.getText(), notification.getType(), inAppNotificationPriority, extraData);
    }

    private NotificationProperties.Template getTemplate() {
        return this.properties.getMessage().get("in-app");
    }

    @Override
    public NotificationContactType supports() {
        return NotificationContactType.IN_APP;
    }
}
