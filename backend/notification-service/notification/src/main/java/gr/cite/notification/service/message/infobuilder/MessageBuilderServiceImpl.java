package gr.cite.notification.service.message.infobuilder;

import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.common.StringUtils;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.common.types.notification.DataType;
import gr.cite.notification.common.types.notification.FieldInfo;
import gr.cite.notification.common.types.notification.NotificationFieldData;
import gr.cite.notification.common.types.tenantconfiguration.DefaultUserLocaleConfigurationDataContainer;
import gr.cite.notification.config.notification.NotificationConfig;
import gr.cite.notification.config.notification.NotificationProperties;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.data.TenantEntity;
import gr.cite.notification.data.UserEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.locale.LocaleService;
import gr.cite.notification.query.TenantQuery;
import gr.cite.notification.query.UserQuery;
import gr.cite.notification.service.contact.extractor.EmailContactExtractor;
import gr.cite.notification.service.message.model.MessageInfo;
import gr.cite.notification.service.tenantconfiguration.TenantConfigurationService;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequestScope
public class MessageBuilderServiceImpl implements MessageInfoBuilderService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(EmailContactExtractor.class));

	private final List<NotificationProperties.Field> staticFieldList;
	private final JsonHandlingService jsonHandlingService;
	private final QueryFactory queryFactory;
	private final TenantConfigurationService tenantConfigurationService;
	private final TenantScope scope;
	private final ErrorThesaurusProperties errors;
	private final LocaleService localeService;

	@Autowired
	public MessageBuilderServiceImpl(@Qualifier(NotificationConfig.BeanQualifier.STATIC_FIELD_LIST)
	                                 List<NotificationProperties.Field> staticFieldList,
	                                 JsonHandlingService jsonHandlingService,
	                                 QueryFactory queryFactory,
	                                 TenantConfigurationService tenantConfigurationService,
	                                 TenantScope scope, ErrorThesaurusProperties errors,
	                                 LocaleService localeService) {
		this.staticFieldList = staticFieldList;
		this.jsonHandlingService = jsonHandlingService;
		this.queryFactory = queryFactory;
		this.tenantConfigurationService = tenantConfigurationService;
		this.scope = scope;
		this.errors = errors;
		this.localeService = localeService;
	}

	@Override
	public MessageInfo buildMessageInfo(NotificationEntity notification) {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setUserId(notification.getUserId());
		messageInfo.setFields(staticFieldList.stream()
				.filter(field -> !StringUtils.isNullOrEmpty(field.getKey()) && !StringUtils.isNullOrEmpty(field.getValue()))
				.map(field -> {
					FieldInfo fieldInfo = new FieldInfo();
					fieldInfo.setKey(field.getKey());
					fieldInfo.setType(DataType.valueOf(field.getType()));
					fieldInfo.setValue(field.getValue());
					return fieldInfo;
				}).collect(Collectors.toList()));
		NotificationFieldData data = this.jsonHandlingService.fromJsonSafe(NotificationFieldData.class, notification.getData());
		if (data != null && data.getFields() != null) messageInfo.getFields().addAll(data.getFields());

		UserEntity userProfile = null;
		if (notification.getUserId() != null) {
			userProfile = this.queryFactory.query(UserQuery.class)
					.ids(notification.getUserId())
					.isActive(IsActive.ACTIVE).first();
		}

		if (userProfile != null) {
			messageInfo.setLanguage(userProfile.getLanguage());
			messageInfo.setCulture(userProfile.getCulture());
			messageInfo.setTimeZone(userProfile.getTimezone());
		} else {
			DefaultUserLocaleConfigurationDataContainer defaultUserLocaleConfiguration = this.tenantConfigurationService.collectTenantUserLocale();
			if (defaultUserLocaleConfiguration != null) {
				messageInfo.setLanguage(defaultUserLocaleConfiguration.getLanguage());
				messageInfo.setCulture(defaultUserLocaleConfiguration.getCulture());
				messageInfo.setTimeZone(defaultUserLocaleConfiguration.getTimeZone());
			}
		}

		if (messageInfo.getLanguage() == null || messageInfo.getLanguage().isBlank()) messageInfo.setLanguage(this.localeService.language());
		if (messageInfo.getCulture() == null || messageInfo.getCulture().isBlank()) messageInfo.setCulture(this.localeService.cultureName());
		if (messageInfo.getTimeZone() == null || messageInfo.getTimeZone().isBlank()) messageInfo.setTimeZone(this.localeService.timezoneName());

		if (this.scope.isMultitenant()) {
			TenantEntity tenantInfo = this.queryFactory.query(TenantQuery.class)
					.isActive(IsActive.ACTIVE).firstAs(new BaseFieldSet(TenantEntity._id, TenantEntity._code));

			if (tenantInfo == null) {
				try {
					logger.error("Could not retrieve tenant info for notification {} in tenant {}", notification.getId(), scope.getTenant());
				} catch (InvalidApplicationException e) {
					throw new RuntimeException(e);
				}
				throw new MyForbiddenException(this.errors.getMissingTenant().getCode(), this.errors.getMissingTenant().getMessage());
			}

			messageInfo.setTenantId(tenantInfo.getId());
			messageInfo.setTenantCode(tenantInfo.getCode());
		}

		return messageInfo;
	}
}
