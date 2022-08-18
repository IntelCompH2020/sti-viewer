package gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IndicatorPointPersist {

	public IndicatorPointPersist() {
		properties = new HashMap<>();
	}

	private Date timestamp;

	private String batchId;

	private String groupHash;

	@Valid
	private DataGroupInfoPersist groupInfo;

	private Date batchTimestamp;

	private Map<String, Object> properties;

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

	public String getGroupHash() {
		return groupHash;
	}

	public void setGroupHash(String groupHash) {
		this.groupHash = groupHash;
	}

	public DataGroupInfoPersist getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(DataGroupInfoPersist groupInfo) {
		this.groupInfo = groupInfo;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@JsonAnySetter
	public void add(String property, Object value) {
		properties.put(property, value);
	}
}
