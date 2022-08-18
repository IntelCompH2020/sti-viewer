package gr.cite.intelcomp.stiviewer.data;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.queueinbox.entity.QueueInbox;
import gr.cite.queueinbox.entity.QueueInboxStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "queue_inbox")
public class QueueInboxEntity implements QueueInbox {
	@Id
	@Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
	public final static String _id = "id";

	@Column(name = "queue", nullable = false, length = 50)
	private String queue;
	public final static String _queue = "queue";

	@Column(name = "exchange", nullable = false, length = 50)
	private String exchange;
	public final static String _exchange = "exchange";

	@Column(name = "route", nullable = false, length = 50)
	private String route;
	public final static String _route = "route";

	@Column(name = "application_id", nullable = false, length = 100)
	private String applicationId;
	public final static String _applicationId = "applicationId";

	@Column(name = "message_id", columnDefinition = "uuid", nullable = false)
	private UUID messageId;
	public final static String _messageId = "messageId";

	@Column(name = "message", columnDefinition = "json", nullable = false)
	private String message;
	public final static String _message = "message";

	@Column(name = "retry_count", nullable = true)
	private Integer retryCount;
	public final static String _retryCount = "retryCount";

	@Column(name = "tenant_id", columnDefinition = "uuid", nullable = true)
	private UUID tenantId;
	public final static String _tenantId = "tenantId";

	//TODO: as integer
	@Column(name = "is_active", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private IsActive isActive;
	public final static String _isActive = "isActive";

	//TODO: as integer
	@Column(name = "status", length = 50, nullable = false)
	@Enumerated(EnumType.STRING)
	private QueueInboxStatus status;
	public final static String _status = "status";

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	@Column(name = "updated_at", nullable = false)
	@Version
	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public UUID getMessageId() {
		return messageId;
	}

	public void setMessageId(UUID messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
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

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	@Override
	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public QueueInboxStatus getStatus() {
		return status;
	}

	public void setStatus(QueueInboxStatus status) {
		this.status = status;
	}
}

