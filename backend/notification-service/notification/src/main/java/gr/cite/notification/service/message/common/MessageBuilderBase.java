package gr.cite.notification.service.message.common;

import gr.cite.notification.cache.NotificationTemplateCache;
import gr.cite.notification.common.StringUtils;
import gr.cite.notification.common.types.notification.FieldInfo;
import gr.cite.notification.config.notification.NotificationProperties;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.service.formatting.FormattingService;
import gr.cite.notification.service.message.model.MessageInfo;
import gr.cite.tools.cipher.CipherService;
import gr.cite.tools.cipher.config.CipherProfileProperties;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.logging.LoggerService;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public abstract class MessageBuilderBase {
	private final LoggerService logger;
	private final ErrorThesaurusProperties errors;
	private final CipherService cipherService;
	private final CipherProfileProperties cipherProfileProperties;

	private final FormattingService formattingService;
	private final NotificationTemplateCache cache;

	public MessageBuilderBase(LoggerService logger, ErrorThesaurusProperties errors, CipherService cipherService, CipherProfileProperties cipherProfileProperties, FormattingService formattingService, NotificationTemplateCache cache) {
		this.logger = logger;
		this.errors = errors;
		this.cipherService = cipherService;
		this.cipherProfileProperties = cipherProfileProperties;
		this.formattingService = formattingService;
		this.cache = cache;
	}

	//GK: Unlike in C# in Java Map is just an interface
	protected static class FieldFormatting extends HashMap<String, String> {
		public Boolean isSet(String key) {
			return this.containsKey(key) && (this.get(key) != null && !this.get(key).isEmpty());
		}
	}

	public static class FieldCiphering extends HashSet<String> {
		public FieldCiphering(List<String> cipherFields) {
			super(cipherFields);
		}

		public FieldCiphering() {
		}

		public Boolean isSet(String key) {
			return this.contains(key);
		}
	}

	protected static class ReplaceResult {
		private String text;
		private Set<String> replacedKeys;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public Set<String> getReplacedKeys() {
			return replacedKeys;
		}

		public void setReplacedKeys(Set<String> replacedKeys) {
			this.replacedKeys = replacedKeys;
		}
	}

	protected ReplaceResult applyFieldReplace(String text, MessageInfo messageInfo, FieldFormatting fieldFormatting, FieldCiphering fieldCiphering) {
		ReplaceResult result = new ReplaceResult();
		result.setReplacedKeys(new HashSet<>());

		if (messageInfo.getUserId() != null && (fieldCiphering != null && !fieldCiphering.isSet("{user-id}"))) {
			String txtValue = fieldFormatting.isSet("{user-id}") ? String.format(fieldFormatting.get("{user-id}"), messageInfo.getUserId().toString()) : messageInfo.getUserId().toString();
			text = text.replace("{user-id}", txtValue);
			result.getReplacedKeys().add("{user-id}");
		}
		//GOTCHA: fieldCiphering not supported for language-code. will not replace
		if (!StringUtils.isNullOrEmpty(messageInfo.getLanguage()) && (fieldCiphering != null && !fieldCiphering.isSet("{language-code}"))) {
			String txtValue = fieldFormatting.isSet("{language-code}") ? String.format(fieldFormatting.get("{language-code}"), messageInfo.getLanguage()) : messageInfo.getLanguage();
			text = text.replace("{language-code}", txtValue);
			result.getReplacedKeys().add("{language-code}");
		}
		//GOTCHA: fieldCiphering not supported for language-code. will not replace
		if (!StringUtils.isNullOrEmpty(messageInfo.getCulture()) && (fieldCiphering != null && !fieldCiphering.isSet("{culture-code}"))) {
			String txtValue = fieldFormatting.isSet("{culture-code}") ? String.format(fieldFormatting.get("{culture-code}"), messageInfo.getCulture()) : messageInfo.getCulture();
			text = text.replace("{culture-code}", txtValue);
			result.getReplacedKeys().add("{culture-code}");
		}
		//GOTCHA: fieldCiphering not supported for timezone-code. will not replace
		if (!StringUtils.isNullOrEmpty(messageInfo.getTimeZone()) && (fieldCiphering != null && !fieldCiphering.isSet("{timezone-code}"))) {
			String txtValue = fieldFormatting.isSet("{timezone-code}") ? String.format(fieldFormatting.get("{timezone-code}"), messageInfo.getTimeZone()) : messageInfo.getTimeZone();
			text = text.replace("{timezone-code}", txtValue);
			result.getReplacedKeys().add("{timezone-code}");
		}
		//GOTCHA: fieldCiphering not supported for tenant-id. will not replace
		if (!messageInfo.getTenantId().equals(new UUID(0L, 0L)) && (fieldCiphering != null && !fieldCiphering.isSet("{tenant-id}"))) {
			String txtValue = fieldFormatting.isSet("{tenant-id}") ? String.format(fieldFormatting.get("{tenant-id}"), messageInfo.getTenantId().toString()) : messageInfo.getTenantId().toString();
			text = text.replace("{tenant-id}", txtValue);
			result.getReplacedKeys().add("{tenant-id}");
		}
		//GOTCHA: fieldCiphering not supported for tenant-code. will not replace
		if (!StringUtils.isNullOrEmpty(messageInfo.getTenantCode()) && (fieldCiphering != null && !fieldCiphering.isSet("{tenant-code}"))) {
			String txtValue = fieldFormatting.isSet("{tenant-code}") ? String.format(fieldFormatting.get("{tenant-code}"), messageInfo.getTenantCode()) : messageInfo.getTenantCode();
			text = text.replace("{tenant-code}", txtValue);
			result.getReplacedKeys().add("{tenant-code}");
		}

		for (FieldInfo field : messageInfo.getFields()) {
			FieldInfo theField = field;
			if ((fieldCiphering != null && fieldCiphering.isSet(field.getKey())) && !StringUtils.isNullOrEmpty(field.getValue())) {
				theField = new FieldInfo();
				theField.setKey(field.getKey());
				theField.setType(field.getType());
				theField.setValue(this.decipherValue(field));
			}

			String format = fieldFormatting.isSet(theField.getKey()) ? fieldFormatting.get(theField.getKey()) : null;
			String value = this.format(messageInfo.getUserId(), theField, format);
			if (StringUtils.isNullOrEmpty(value)) continue;
			text = text.replace(theField.getKey(), value);
			result.getReplacedKeys().add(theField.getKey());
		}

		result.setText(text);

		return result;
	}

	protected FieldFormatting buildFieldFormatting(NotificationProperties.Flow.FieldOption options) {
		FieldFormatting formatting = new FieldFormatting();
		if (options == null || options.getFormatting() == null) return formatting;

		for (Map.Entry<String, String> pair : options.getFormatting().entrySet()) {
			if (StringUtils.isNullOrEmpty(pair.getValue())) continue;
			formatting.put(pair.getKey(), pair.getValue());
		}
		return formatting;
	}

	//TODO: Here check with a language accepted list and fallback to default
	protected String lookupOrReadLocalizedFile(NotificationProperties.Template.TemplateCache templateCache, String path, String language) {
		String filename = path.replace("{language}", language);
		File file = null;
		NotificationTemplateCache.NotificationTemplateCacheValue cacheKey =
				new NotificationTemplateCache.NotificationTemplateCacheValue(templateCache.getPrefix(),
						filename,
						templateCache.getKeyPattern());
		String content = this.cache.lookup(cacheKey);

		if (!StringUtils.isNullOrEmpty(content)) return content;
		try {
			file = ResourceUtils.getFile(filename);

		} catch (FileNotFoundException e) {
			throw new MyApplicationException(this.errors.getSystemError().getCode(), this.errors.getSystemError().getMessage());
		}
		try {
			content = new String(new FileInputStream(file).readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.cache.put(cacheKey, content);

		return content;
	}

	protected String decipherValue(FieldInfo field) {
		try {
			return this.cipherService.decryptSymetricAes(field.getValue(), this.cipherProfileProperties.getProfileMap().get("notification-profile-name"));
		} catch (Exception ex) {
			this.logger.error("could not decipher value " + field.getValue() + " of key " + field.getKey() + ". no value for key", ex);
			return null;
		}
	}

	protected ReplaceResult buildTemplate(UUID notificationId, String template, MessageInfo messageInfo, NotificationProperties.Flow.FieldOption options, FieldFormatting formatting, FieldCiphering ciphering) {
		ReplaceResult result = this.applyFieldReplace(template, messageInfo, formatting, ciphering);
		if (options != null) {
			Boolean allMandatory = this.ensureMandatoryFields(result, options.getMandatory() != null ? options.getMandatory() : new ArrayList<>());
			if (!allMandatory) {
				logger.error("Could not replace all subject mandatory field for notification {}", notificationId);
				return null;
			}
			if (options.getOptional() != null) {
				for (FieldInfo field : options.getOptional()) {
					result.setText(this.applyFieldReplace(result.getText(), field, "", formatting));
				}
			}
		}
		return result;
	}

	protected Boolean ensureMandatoryFields(ReplaceResult result, List<String> mandatory) {
		for (String field : mandatory) {
			if (!result.getReplacedKeys().contains(field)) {
				logger.warn("Mandatory field {} was not replaced in message", field);
				return false;
			}
		}
		return true;
	}

	protected String applyFieldReplace(String text, FieldInfo field, String defaultValue, FieldFormatting formatting) {
		String txtVal = defaultValue;
		if (!StringUtils.isNullOrEmpty(field.getValue())) {
			if (formatting.isSet(field.getKey())) txtVal = String.format(formatting.get(field.getKey()), field.getValue());
			else txtVal = field.getValue();
		}

		text = text.replace(field.getKey(), txtVal);

		return text;
	}

	protected String format(UUID userId, FieldInfo field, String format) {
		switch (field.getType()) {
			case DateTime:
				return this.dateTimeFormat(userId, field.getValue(), format);
			case Decimal:
				return this.decimalFormat(userId, field.getValue(), format);
			case Double:
				return this.doubleFormat(userId, field.getValue(), format);
			case Integer:
				return this.integerFormat(userId, field.getValue(), format);
			case String:
			default:
				return this.stringFormat(field.getValue(), format);
		}
	}

	private String stringFormat(String value, String format) {
		String val;
		if (!StringUtils.isNullOrEmpty(format)) val = String.format(format, value);
		else val = value;

		return val;
	}

	private String dateTimeFormat(UUID userId, String value, String format) {
		if (StringUtils.isNullOrEmpty(value)) return "";
		Instant time = Instant.parse(value);
		return formattingService.format(time, userId, null, format, null);
	}

	private String decimalFormat(UUID userId, String value, String format) {
		if (StringUtils.isNullOrEmpty(value)) return "";
		Double aDouble = Double.parseDouble(value);
		return formattingService.format(aDouble, userId, null, format, null);
	}

	private String doubleFormat(UUID userId, String value, String format) {
		return this.decimalFormat(userId, value, format);
	}

	private String integerFormat(UUID userId, String value, String format) {
		if (StringUtils.isNullOrEmpty(value)) return "";
		Integer aInteger = Integer.parseInt(value);
		return formattingService.format(aInteger, userId, format, null);
	}
}
