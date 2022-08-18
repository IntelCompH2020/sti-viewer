package gr.cite.user.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ContactInfoType {
	Email(0),
	MobilePhone(1),
	LandLinePhone(2);
	private static final Map<Integer, ContactInfoType> values = new HashMap<>();

	private final Integer mappedName;

	//For jackson parsing (used by MVC)
	@JsonValue
	public Integer getMappedName() {
		return mappedName;
	}

	static {
		for (ContactInfoType e : values()) {
			values.put(e.asInt(), e);
		}
	}

	private ContactInfoType(int mappedName) {
		this.mappedName = mappedName;
	}

	public Integer asInt() {
		return this.mappedName;
	}

	public static ContactInfoType fromString(Integer value) {
		return values.getOrDefault(value, Email);
	}
}
