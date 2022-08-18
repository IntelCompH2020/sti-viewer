package gr.cite.notification.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum NotificationContactType {
	EMAIL(0),
	SLACK_BROADCAST(1),
	SMS(2),
	IN_APP(3);
	private static final Map<Integer, NotificationContactType> values = new HashMap<>();

	private final Integer mappedName;

	//For jackson parsing (used by MVC)
	@JsonValue
	public Integer getMappedName() {
		return mappedName;
	}

	static {
		for (NotificationContactType e : values()) {
			values.put(e.asInt(), e);
		}
	}

	private NotificationContactType(int mappedName) {
		this.mappedName = mappedName;
	}

	public Integer asInt() {
		return this.mappedName;
	}

	public static NotificationContactType fromString(Integer value) {
		return values.getOrDefault(value, EMAIL);
	}
}
