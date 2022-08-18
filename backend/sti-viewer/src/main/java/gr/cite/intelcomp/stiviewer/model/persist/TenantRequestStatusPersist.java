package gr.cite.intelcomp.stiviewer.model.persist;


import gr.cite.intelcomp.stiviewer.common.enums.TenantRequestStatus;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


@FieldNotNullIfOtherSet(notNullField = TenantRequestStatusPersist._id, otherSetField = TenantRequestStatusPersist._hash, failOn = TenantRequestStatusPersist._hash, message = "{validation.hashempty}")
public class TenantRequestStatusPersist {

	@ValidId(message = "{validation.invalidid}")
	private UUID id;
	public final static String _id = "id";

	@NotNull(message = "{validation.empty}")
	private TenantRequestStatus tenantRequestStatus;

	@Size(max = 500, message = "{validation.largerthanmax}")
	private String tenantName;

	@Size(max = 200, message = "{validation.largerthanmax}")
	private String tenantCode;

	private String hash;
	public final static String _hash = "hash";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public TenantRequestStatus getStatus() {
		return tenantRequestStatus;
	}

	public void setStatus(TenantRequestStatus tenantRequestStatus) {
		this.tenantRequestStatus = tenantRequestStatus;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
}
