package gr.cite.user.model;

import gr.cite.user.common.enums.UserSettingsEntityType;
import gr.cite.user.common.enums.UserSettingsType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserSettings {

	private String key;
	public static final String _key = "key";

	private List<UserSetting> settings;
	public static final String _settings = "settings";

	private UserSetting defaultSetting;
	public static final String _defaultSetting = "defaultSetting";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<UserSetting> getSettings() {
		return settings;
	}

	public void setSettings(List<UserSetting> settings) {
		this.settings = settings;
	}

	public UserSetting getDefaultSetting() {
		return defaultSetting;
	}

	public void setDefaultSetting(UserSetting defaultSetting) {
		this.defaultSetting = defaultSetting;
	}

	public static class UserSetting {

		private UUID id;
		public static final String _id = "id";

		private String key;
		public static final String _key = "key";

		private String name;
		public static final String _name = "name";

		private String value;
		public static final String _value = "value";

		private UserSettingsEntityType entityType;
		public static final String _entityType = "entityType";

		private UUID entityId;
		public static final String _entityId = "entityId";

		private Instant createdAt;
		public static final String _createdAt = "createdAt";

		private Instant updatedAt;
		public static final String _updatedAt = "updatedAt";

		private UserSettingsType type;
		public static final String _type = "type";

		private String hash;
		public static final String _hash = "hash";

		private boolean isDefault;
		public static final String _isDefault = "isDefault";

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public UserSettingsEntityType getEntityType() {
			return entityType;
		}

		public void setEntityType(UserSettingsEntityType entityType) {
			this.entityType = entityType;
		}

		public UUID getEntityId() {
			return entityId;
		}

		public void setEntityId(UUID entityId) {
			this.entityId = entityId;
		}

		public Instant getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Instant createdAt) {
			this.createdAt = createdAt;
		}

		public Instant getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(Instant updatedAt) {
			this.updatedAt = updatedAt;
		}

		public UserSettingsType getType() {
			return type;
		}

		public void setType(UserSettingsType type) {
			this.type = type;
		}

		public String getHash() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isDefault() {
			return isDefault;
		}

		public void setDefault(boolean aDefault) {
			isDefault = aDefault;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class UserSettingsConfig{

		private UUID defaultSetting;

		public UUID getDefaultSetting() {
			return defaultSetting;
		}

		public void setDefaultSetting(UUID defaultSetting) {
			this.defaultSetting = defaultSetting;
		}
	}
}
