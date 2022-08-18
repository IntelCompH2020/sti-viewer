package gr.cite.intelcomp.stiviewer.data;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.queueoutbox.entity.QueueOutbox;
import gr.cite.queueoutbox.entity.QueueOutboxNotifyStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "queue_outbox")
public class QueueOutboxEntity implements QueueOutbox {
	@Id
	@Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
	public final static String _id = "id";

	@Column(name = "exchange", nullable = false, length = 50)
	private String exchange;
	public final static String _exchange = "exchange";

	@Column(name = "route", length = 50)
	private String route;
	public final static String _route = "route";

	@Column(name = "message_id", columnDefinition = "uuid", nullable = false)
	private UUID messageId;
	public final static String _messageId = "messageId";

	@Column(name = "message", columnDefinition = "json", nullable = false)
	private String message;
	public final static String _message = "message";

	//TODO: as integer
	@Column(name = "notify_status", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private QueueOutboxNotifyStatus notifyStatus;
	public final static String _notifyStatus = "notifyStatus";

	@Column(name = "retry_count", nullable = false)
	private int retryCount;
	public final static String _retryCount = "retryCount";

	@Column(name = "published_At", nullable = true)
	private Instant publishedAt;
	public final static String _publishedAt = "publishedAt";

	@Column(name = "confirmed_at", nullable = true)
	private Instant confirmedAt;
	public final static String _confirmedAt = "confirmedAt";

	@Column(name = "tenant_id", columnDefinition = "uuid", nullable = true)
	private UUID tenantId;
	public final static String _tenantId = "tenantId";

	//TODO: as integer
	@Column(name = "is_active", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private IsActive isActive;
	public final static String _isActive = "isActive";

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

	public QueueOutboxNotifyStatus getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(QueueOutboxNotifyStatus notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public Instant getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(Instant publishedAt) {
		this.publishedAt = publishedAt;
	}

	public Instant getConfirmedAt() {
		return confirmedAt;
	}

	public void setConfirmedAt(Instant confirmedAt) {
		this.confirmedAt = confirmedAt;
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
}

