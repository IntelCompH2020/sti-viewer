package gr.cite.user.model.persist;

import gr.cite.user.common.enums.UserSettingsEntityType;
import gr.cite.user.common.enums.UserSettingsType;
import gr.cite.user.common.validation.FieldNotNullIfOtherSet;
import gr.cite.user.common.validation.ValidEnum;
import gr.cite.user.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@FieldNotNullIfOtherSet(message = "{validation.hashempty}")
public class UserSettingsPersist {

	@ValidId(message = "{validation.invalidid}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String key;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String name;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String value;

	@NotNull(message = "{validation.empty}")
	private UserSettingsEntityType entityType;

	@ValidId(message = "{validation.invalidid}")
	private UUID entityId;

	@NotNull(message = "{validation.empty}")
	private UserSettingsType type;

	@NotNull(message = "{validation.empty}")
	private boolean isDefault;

	private String hash;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean aDefault) {
		isDefault = aDefault;
	}

	public static class UserSettingsConfigPersist{

		private UUID defaultSetting;

		public UUID getDefaultSetting() {
			return defaultSetting;
		}

		public void setDefaultSetting(UUID defaultSetting) {
			this.defaultSetting = defaultSetting;
		}
	}
}
