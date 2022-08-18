package gr.cite.intelcomp.stiviewer.data;

import gr.cite.intelcomp.stiviewer.common.enums.TenantRequestStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_request")
public class TenantRequestEntity {
	@Id
	@Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
	public final static String _id = "id";

	@Column(name = "message")
	private String message;
	public final static String _message = "message";

	@Column(name = "status", length = 100, nullable = false)
	@Enumerated(EnumType.STRING)
	private TenantRequestStatus status;
	public final static String _status = "status";

	@Column(name = "for_user_id", columnDefinition = "uuid", nullable = false)
	private UUID forUserId;
	public final static String _forUserId = "forUserId";

	@Column(name = "email", length = 200, nullable = false)
	private String email;
	public final static String _email = "email";
	@Column(name = "assigned_tenant_id", columnDefinition = "uuid", nullable = true)
	private UUID assignedTenantId;
	public final static String _assignedTenantId = "assignedTenantId";

//    @Column(name = "is_active", length = 20, nullable = false)
//    @Enumerated(EnumType.STRING)
//    private IsActive isActive;
//    public final static String _isActive = "isActive";

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public String getEmail() {
		return email;
	}

	public String getMessage() {
		return message;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TenantRequestStatus getStatus() {
		return status;
	}

	public void setStatus(TenantRequestStatus tenantRequestStatus) {

		this.status = tenantRequestStatus;
	}

	public UUID getForUserId() {
		return forUserId;
	}

	public void setForUserId(UUID forUserId) {
		this.forUserId = forUserId;
	}

	public UUID getAssignedTenantId() {
		return assignedTenantId;
	}

	public void setAssignedTenantId(UUID assignedTenantId) {
		this.assignedTenantId = assignedTenantId;
	}
}
