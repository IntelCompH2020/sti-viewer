package gr.cite.notification.service.message.builder;

import gr.cite.notification.cache.NotificationTemplateCache;
import gr.cite.notification.common.StringUtils;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.types.notification.FieldInfo;
import gr.cite.notification.config.notification.NotificationConfig;
import gr.cite.notification.config.notification.NotificationProperties;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.service.formatting.FormattingService;
import gr.cite.notification.service.message.common.MessageBuilderBase;
import gr.cite.notification.service.message.infobuilder.MessageInfoBuilderService;
import gr.cite.notification.service.message.model.EmailMessage;
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

import java.util.Map;
import java.util.UUID;

@Component
@RequestScope
public class EmailMessageBuilder extends MessageBuilderBase implements MessageBuilder{
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(EmailMessageBuilder.class));
    private final NotificationProperties properties;
    private final Map<String, Map<UUID, NotificationProperties.Flow>> flowMap;
    private final MessageInfoBuilderService messageInfoBuilderService;
    private final Map<UUID, FieldCiphering> cipherFields;

    @Autowired
    public EmailMessageBuilder(NotificationProperties properties,
                               @Qualifier(NotificationConfig.BeanQualifier.FLOW_MAP)
                               Map<String, Map<UUID, NotificationProperties.Flow>> flowMap,
                               MessageInfoBuilderService messageInfoBuilderService,
                               ErrorThesaurusProperties errors,
                               @Qualifier(NotificationConfig.BeanQualifier.CIPHER_FIELDS)
                               Map<UUID, FieldCiphering> cipherFields,
                               CipherService cipherService,
                               CipherProfileProperties cipherProfileProperties,
                               FormattingService formattingService,
                               NotificationTemplateCache cache) {
        super(logger, errors, cipherService, cipherProfileProperties, formattingService, cache);
        this.properties = properties;
        this.flowMap = flowMap;
        this.messageInfoBuilderService = messageInfoBuilderService;
        this.cipherFields = cipherFields;
    }

    @Override
    public Message buildMessage(NotificationEntity notification) {
        MessageInfo messageInfo = this.messageInfoBuilderService.buildMessageInfo(notification);
        NotificationProperties.Flow flow = this.flowMap.get("email").getOrDefault(notification.getType(), null);
        if (flow == null) return null;
        String subjectTemplate = null;
        if(!StringUtils.isNullOrEmpty(flow.getSubjectKey())) subjectTemplate = messageInfo.getFields().stream().filter(fieldInfo -> fieldInfo.getKey().equals(flow.getSubjectKey())).findFirst().orElse(new FieldInfo()).getValue();
        if (StringUtils.isNullOrEmpty(subjectTemplate)) subjectTemplate = this.lookupOrReadLocalizedFile(this.getTemplate().getTemplateCache(), flow.getSubjectPath(), messageInfo.getLanguage());
        FieldFormatting subjectFormatting = this.buildFieldFormatting(flow.getSubjectFieldOptions());
        ReplaceResult subjectResult = this.buildTemplate(notification.getId(), subjectTemplate, messageInfo, flow.getSubjectFieldOptions(), subjectFormatting, cipherFields.get(notification.getType()));

        String bodyTemplate = null;
        if(!StringUtils.isNullOrEmpty(flow.getBodyKey())) bodyTemplate = messageInfo.getFields().stream().filter(fieldInfo -> fieldInfo.getKey().equals(flow.getBodyKey())).findFirst().orElse(new FieldInfo()).getValue();
        if (StringUtils.isNullOrEmpty(bodyTemplate)) bodyTemplate = this.lookupOrReadLocalizedFile(this.getTemplate().getTemplateCache(), flow.getBodyPath(), messageInfo.getLanguage());
        FieldFormatting bodyFormatting = this.buildFieldFormatting(flow.getBodyFieldOptions());
        ReplaceResult bodyResult = this.buildTemplate(notification.getId(), bodyTemplate, messageInfo, flow.getBodyFieldOptions(), bodyFormatting, cipherFields.get(notification.getType()));

        if (bodyResult != null && subjectResult != null) {
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setBody(bodyResult.getText());
            emailMessage.setSubject(subjectResult.getText());

            return emailMessage;
        }
        return  null;
    }

    private NotificationProperties.Template getTemplate() {
        return this.properties.getMessage().get("email");
    }

    @Override
    public NotificationContactType supports() {
        return NotificationContactType.EMAIL;
    }
}
