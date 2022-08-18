package gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint;

import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Document(indexName = "indicator-point")
public class IndicatorPointEntity {

	public static final class Fields {
		public static final String id = "id";
		public static final String batchId = "batch_id";
		public static final String groupHash = "group_hash";
		public static final String batchTimestamp = "batch_timestamp";
		public static final String timestamp = "timestamp";
		public static final String groupInfo = "group_info";
	}

	@Id
	@Field(Fields.id)
	private UUID id;

	@Field(value = Fields.timestamp, type = FieldType.Date)
	private Date timestamp;

	@Field(value = Fields.batchId, type = FieldType.Keyword)
	private String batchId;
	@Field(value = Fields.batchTimestamp, type = FieldType.Date)
	private Date batchTimestamp;

	@Field(value = Fields.groupHash, type = FieldType.Keyword)
	private String groupHash;

	@Field(value = Fields.groupInfo, type = FieldType.Object)
	private DataGroupInfoEntity groupInfo;

	@JsonIgnore
	private Map<String, Object> properties;

	@JsonIgnore
	private UUID indicatorId;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(UUID indicatorId) {
		this.indicatorId = indicatorId;
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

	public Map<String, java.lang.Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, java.lang.Object> properties) {
		this.properties = properties;
	}

	public String getGroupHash() {
		return groupHash;
	}

	public void setGroupHash(String groupHash) {
		this.groupHash = groupHash;
	}

	public DataGroupInfoEntity getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(DataGroupInfoEntity groupInfo) {
		this.groupInfo = groupInfo;
	}
}

