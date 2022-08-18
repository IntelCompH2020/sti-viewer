package gr.cite.notification.common.types.notification;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum EmailOverrideMode {
	NotOverride(0),
	Additive(1),
	Replace(2);
	private static final Map<Integer, EmailOverrideMode> values = new HashMap<>();

	private final Integer mappedName;

	//For jackson parsing (used by MVC)
	@JsonValue
	public Integer getMappedName() {
		return mappedName;
	}

	static {
		for (EmailOverrideMode e : values()) {
			values.put(e.asInt(), e);
		}
	}

	private EmailOverrideMode(int mappedName) {
		this.mappedName = mappedName;
	}

	public Integer asInt() {
		return this.mappedName;
	}

	public static EmailOverrideMode fromString(Integer value) {
		return values.getOrDefault(value, NotOverride);
	}
}
