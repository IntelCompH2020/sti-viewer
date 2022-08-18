package gr.cite.intelcomp.stiviewer.model.indicatorpoint;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class IndicatorPoint {

	public final static String _id = "id";
	private UUID id;

	public final static String _timestamp = "timestamp";
	private Date timestamp;

	public final static String _batchId = "batchId";
	private String batchId;

	public final static String _batchTimestamp = "batchTimestamp";
	private Date batchTimestamp;

	public final static String _groupHash = "groupHash";
	private String groupHash;

	public final static String _groupInfo = "groupInfo";
	private DataGroupInfo groupInfo;

	private Map<String, Object> properties;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Date getBatchTimestamp() {
		return batchTimestamp;
	}

	public void setBatchTimestamp(Date batchTimestamp) {
		this.batchTimestamp = batchTimestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getGroupHash() {
		return groupHash;
	}

	public void setGroupHash(String groupHash) {
		this.groupHash = groupHash;
	}

	public DataGroupInfo getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(DataGroupInfo groupInfo) {
		this.groupInfo = groupInfo;
	}
}
