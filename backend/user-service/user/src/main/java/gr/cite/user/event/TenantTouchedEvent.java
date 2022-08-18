package gr.cite.user.event;

import java.util.UUID;

public class TenantTouchedEvent {
	public TenantTouchedEvent() {
	}

	public TenantTouchedEvent(UUID tenantId, String tenantCode, String previousTenantCode) {
		this.tenantId = tenantId;
		this.tenantCode = tenantCode;
		this.previousTenantCode = previousTenantCode;
	}

	private UUID tenantId;
	private String tenantCode;
	private String previousTenantCode;

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getPreviousTenantCode() {
		return previousTenantCode;
	}

	public void setPreviousTenantCode(String previousTenantCode) {
		this.previousTenantCode = previousTenantCode;
	}
}
