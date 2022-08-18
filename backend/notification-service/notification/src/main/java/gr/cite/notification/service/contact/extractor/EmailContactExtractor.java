package gr.cite.notification.service.contact.extractor;

import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.common.StringUtils;
import gr.cite.notification.common.enums.ContactInfoType;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.enums.UserContactType;
import gr.cite.notification.common.types.notification.ContactPair;
import gr.cite.notification.common.types.notification.EmailOverrideMode;
import gr.cite.notification.common.types.notification.NotificationContactData;
import gr.cite.notification.config.notification.NotificationConfig;
import gr.cite.notification.config.notification.NotificationProperties;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.data.UserContactInfoEntity;
import gr.cite.notification.model.UserContactInfo;
import gr.cite.notification.query.UserContactInfoQuery;
import gr.cite.notification.service.contact.model.Contact;
import gr.cite.notification.service.contact.model.EmailContact;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmailContactExtractor implements ContactExtractor {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(EmailContactExtractor.class));

	private final JsonHandlingService jsonHandlingService;
	private final QueryFactory queryFactory;
	private final Map<String, Map<UUID, NotificationProperties.Flow>> flowMap;

	@Autowired
	public EmailContactExtractor(JsonHandlingService jsonHandlingService,
								 @Qualifier(NotificationConfig.BeanQualifier.FLOW_MAP)
	                             Map<String, Map<UUID, NotificationProperties.Flow>> flowMap,
	                             QueryFactory queryFactory
	) {
		this.jsonHandlingService = jsonHandlingService;
		this.queryFactory = queryFactory;
		this.flowMap = flowMap;
	}

	@Override
	public Contact extract(NotificationEntity notification) {
		EmailContact emailContact = new EmailContact();
		NotificationContactData data = this.jsonHandlingService.fromJsonSafe(NotificationContactData.class, notification.getContactHint());

		List<ContactPair> contactPairs = data != null && data.getContacts() != null ? data.getContacts().stream().filter(contactPair -> contactPair.getType().equals(ContactInfoType.Email) && !StringUtils.isNullOrEmpty(contactPair.getContact())).collect(Collectors.toList()) : null;

		if (contactPairs != null && !contactPairs.isEmpty()) {
			emailContact.setEmails(contactPairs.stream().map(ContactPair::getContact).collect(Collectors.toList()));
		}
		else if (notification.getUserId() != null) {
			logger.trace("extracting from user");
			UserContactInfoEntity userContactInfo = this.queryFactory.query(UserContactInfoQuery.class)
					.userIds(notification.getUserId())
					.isActive(IsActive.ACTIVE)
					.type(UserContactType.Email).firstAs(new BaseFieldSet().ensure(UserContactInfo._value));

			if (userContactInfo != null && userContactInfo.getValue() != null && !userContactInfo.getValue().isBlank()) emailContact.setEmails(List.of(userContactInfo.getValue()));
		}
		else {
			logger.warn("Could not retrieve contact information for notification {}", notification.getId());
		}
		NotificationProperties.Flow options = this.locateFlowOptions(notification.getType());

		List<String> defaultCCs = options.getCc() != null ? options.getCc().stream().filter(x -> x != null && !x.isBlank()).collect(Collectors.toList()) : new ArrayList<>();
		List<String> defaultBCCs = options.getBcc() != null ? options.getBcc().stream().filter(x -> x != null && !x.isBlank()).collect(Collectors.toList()) : new ArrayList<>();

		List<String> ccContacts = data != null && data.getCc() != null ? data.getCc().stream().filter(contactPair -> contactPair.getType().equals(ContactInfoType.Email) && !StringUtils.isNullOrEmpty(contactPair.getContact())).map(ContactPair::getContact).collect(Collectors.toList()) : new ArrayList<>();
		List<String> bccContacts = data != null && data.getBcc() != null ? data.getBcc().stream().filter(contactPair -> contactPair.getType().equals(ContactInfoType.Email) && !StringUtils.isNullOrEmpty(contactPair.getContact())).map(ContactPair::getContact).collect(Collectors.toList()) : new ArrayList<>();

		emailContact.setCcEmails(this.mergeContacts(options.getCcMode(), defaultCCs, ccContacts).stream().distinct().collect(Collectors.toList()));
		emailContact.setBccEmails(this.mergeContacts(options.getBccMode(), defaultBCCs, bccContacts).stream().distinct().collect(Collectors.toList()));

		return emailContact;
	}

	private List<String> mergeContacts(EmailOverrideMode overrideMode, List<String> defaultContacts, List<String> messageContacts) {
		switch (overrideMode) {
			case NotOverride:
				return defaultContacts;
			case Replace:
				return messageContacts != null && !messageContacts.isEmpty() ? messageContacts : defaultContacts;
			case Additive: {
				List<String> contacts = new ArrayList<>();
				if (messageContacts != null) contacts.addAll(messageContacts);
				if (defaultContacts != null) contacts.addAll(defaultContacts);
				return contacts;
			}
			default:
				throw new MyApplicationException("Invalid type " + overrideMode.toString());
		}
	}

	private NotificationProperties.Flow locateFlowOptions(UUID type) {
		return this.flowMap.get("email").getOrDefault(type, null);
	}

	@Override
	public NotificationContactType supports() {
		return NotificationContactType.EMAIL;
	}
}
