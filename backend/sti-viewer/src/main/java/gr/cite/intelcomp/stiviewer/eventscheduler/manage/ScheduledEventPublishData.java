package gr.cite.intelcomp.stiviewer.eventscheduler.manage;

import gr.cite.intelcomp.stiviewer.common.enums.ScheduledEventType;

import java.time.Instant;
import java.util.UUID;

public class ScheduledEventPublishData {
	private ScheduledEventType type;
	private String key;
	private UUID creatorId;
	private String keyType;
	private Instant runAt;
	private String data;

	public ScheduledEventType getType() {
		return type;
	}

	public void setType(ScheduledEventType type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public UUID getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(UUID creatorId) {
		this.creatorId = creatorId;
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public Instant getRunAt() {
		return runAt;
	}

	public void setRunAt(Instant runAt) {
		this.runAt = runAt;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
