package gr.cite.notification.config.notification;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.types.notification.EmailOverrideMode;
import gr.cite.notification.common.types.notification.FieldInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ConstructorBinding
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

	private final Task task;
	private final Resolver resolver;
	private final Map<String, Template> message;
	private final Template overrideCache;
	private final StaticFieldItems staticFields;

	public NotificationProperties(Task task, Resolver resolver, Map<String, Template> message, Template overrideCache, StaticFieldItems staticFields) {
		this.task = task;
		this.resolver = resolver;
		this.message = message;
		this.overrideCache = overrideCache;
		this.staticFields = staticFields;
	}

	public Task getTask() {
		return task;
	}

	public Resolver getResolver() {
		return resolver;
	}

	public Map<String, Template> getMessage() {
		return message;
	}

	public Template getOverrideCache() {
		return overrideCache;
	}

	public StaticFieldItems getStaticFields() {
		return staticFields;
	}

	public static class Task {
		private final Processor processor;

		public Task(Processor processor) {
			this.processor = processor;
		}

		public Processor getProcessor() {
			return processor;
		}

		public static class Processor {
			private final Boolean enable;
			private final Long intervalSeconds;
			private final Options options;
			private final List<String> overrides;

			public Processor(Boolean enable, Long intervalSeconds, Options options, List<String> overrides) {
				this.enable = enable;
				this.intervalSeconds = intervalSeconds;
				this.options = options;
				this.overrides = overrides;
			}

			public Boolean getEnable() {
				return enable;
			}

			public Long getIntervalSeconds() {
				return intervalSeconds;
			}

			public Options getOptions() {
				return options;
			}

			public List<String> getOverrides() {
				return overrides;
			}

			public static class Options {
				private final Long retryThreshold;
				private final Long maxRetryDelaySeconds;
				private final Long tooOldToSendSeconds;
				private final Long tooOldToTrackSeconds;

				public Options(Long retryThreshold, Long maxRetryDelaySeconds, Long tooOldToSendSeconds, Long tooOldToTrackSeconds) {
					this.retryThreshold = retryThreshold;
					this.maxRetryDelaySeconds = maxRetryDelaySeconds;
					this.tooOldToSendSeconds = tooOldToSendSeconds;
					this.tooOldToTrackSeconds = tooOldToTrackSeconds;
				}

				public Long getRetryThreshold() {
					return retryThreshold;
				}

				public Long getMaxRetryDelaySeconds() {
					return maxRetryDelaySeconds;
				}

				public Long getTooOldToSendSeconds() {
					return tooOldToSendSeconds;
				}

				public Long getTooOldToTrackSeconds() {
					return tooOldToTrackSeconds;
				}
			}
		}
	}

	public static class Resolver {
		private final List<GlobalPolicy> globalPolicies;

		public Resolver(List<GlobalPolicy> globalPolicies) {
			this.globalPolicies = globalPolicies;
		}

		public List<GlobalPolicy> getGlobalPolicies() {
			return globalPolicies;
		}

		public static class GlobalPolicy {
			private final UUID type;
			private final List<NotificationContactType> contacts;

			public GlobalPolicy(UUID type, List<NotificationContactType> contacts) {
				this.type = type;
				this.contacts = contacts;
			}

			public UUID getType() {
				return type;
			}

			public List<NotificationContactType> getContacts() {
				return contacts;
			}
		}
	}

	public static class Template {
		private final TemplateCache templateCache;

		private final List<Flow> flows;

		public Template(TemplateCache templateCache, List<Flow> flows) {
			this.templateCache = templateCache;
			this.flows = flows;
		}

		public TemplateCache getTemplateCache() {
			return templateCache;
		}


		public List<Flow> getFlows() {
			return flows;
		}

		public static class TemplateCache {
			private final String prefix;
			private final String keyPattern;

			public TemplateCache(String prefix, String keyPattern) {
				this.prefix = prefix;
				this.keyPattern = keyPattern;
			}

			public String getPrefix() {
				return prefix;
			}

			public String getKeyPattern() {
				return keyPattern;
			}
		}
	}

	public static class StaticFieldItems {
		private final List<Field> Fields;

		public StaticFieldItems(List<Field> fields) {
			Fields = fields;
		}

		public List<Field> getFields() {
			return Fields;
		}
	}

	public static class Field {
		private final String key;
		private final String type;
		private final String value;

		public Field(String key, String type, String value) {
			this.key = key;
			this.type = type;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getType() {
			return type;
		}

		public String getValue() {
			return value;
		}
	}

	public static class Flow {
		private final UUID key;
		private final String subjectPath;
		private final String subjectKey;
		private final FieldOption subjectFieldOptions;
		private final String bodyPath;
		private final String bodyKey;
		private final FieldOption bodyFieldOptions;
		private final List<String> cc;
		private final EmailOverrideMode ccMode;
		private final List<String> bcc;
		private final EmailOverrideMode bccMode;
		private final Boolean allowAttachments;
		private final List<String> cipherFields;
		private final String priorityKey;
		private final List<String> extraDataKeys;

		public Flow(UUID key, String subjectPath, String subjectKey, FieldOption subjectFieldOptions, String bodyPath, String bodyKey, FieldOption bodyFieldOptions, List<String> cc, EmailOverrideMode ccMode, List<String> bcc, EmailOverrideMode bccMode, Boolean allowAttachments, List<String> cipherFields, String priorityKey, List<String> extraDataKeys) {
			this.key = key;
			this.subjectPath = subjectPath;
			this.subjectKey = subjectKey;
			this.subjectFieldOptions = subjectFieldOptions;
			this.bodyPath = bodyPath;
			this.bodyKey = bodyKey;
			this.bodyFieldOptions = bodyFieldOptions;
			this.cc = cc;
			this.ccMode = ccMode;
			this.bcc = bcc;
			this.bccMode = bccMode;
			this.allowAttachments = allowAttachments;
			this.cipherFields = cipherFields;
			this.priorityKey = priorityKey;
			this.extraDataKeys = extraDataKeys;
		}

		public UUID getKey() {
			return key;
		}

		public String getSubjectPath() {
			return subjectPath;
		}

		public String getSubjectKey() {
			return subjectKey;
		}

		public FieldOption getSubjectFieldOptions() {
			return subjectFieldOptions;
		}

		public String getBodyPath() {
			return bodyPath;
		}

		public String getBodyKey() {
			return bodyKey;
		}

		public FieldOption getBodyFieldOptions() {
			return bodyFieldOptions;
		}

		public List<String> getCc() {
			return cc;
		}

		public EmailOverrideMode getCcMode() {
			return ccMode;
		}

		public List<String> getBcc() {
			return bcc;
		}

		public EmailOverrideMode getBccMode() {
			return bccMode;
		}

		public Boolean getAllowAttachments() {
			return allowAttachments;
		}

		public List<String> getCipherFields() {
			return cipherFields;
		}

		public String getPriorityKey() {
			return priorityKey;
		}

		public List<String> getExtraDataKeys() {
			return extraDataKeys;
		}

		public static class FieldOption {
			private final List<String> mandatory;
			private final List<FieldInfo> optional;

			private final Map<String, String> formatting;

			public FieldOption(List<String> mandatory, List<FieldInfo> optional, Map<String, String> formatting) {
				this.mandatory = mandatory;
				this.optional = optional;
				this.formatting = formatting;
			}

			public List<String> getMandatory() {
				return mandatory;
			}

			public List<FieldInfo> getOptional() {
				return optional;
			}

			public Map<String, String> getFormatting() {
				return formatting;
			}

		}
	}
}
