package gr.cite.intelcomp.stiviewer.service.tenantrequest;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;

@ConfigurationProperties(prefix = "tenant-request")
public class TenantRequestProperties {
	private UUID tenantRequestApprovedNotificationKey;
	private UUID tenantRequestRejectedNotificationKey;
	private TenantRequestApprovedTemplateKeys  tenantRequestApprovedTemplate;
	private TenantRequestRejectedTemplateKeys  tenantRequestRejectedTemplate;

	public UUID getTenantRequestApprovedNotificationKey() {
		return tenantRequestApprovedNotificationKey;
	}

	public void setTenantRequestApprovedNotificationKey(UUID tenantRequestApprovedNotificationKey) {
		this.tenantRequestApprovedNotificationKey = tenantRequestApprovedNotificationKey;
	}

	public UUID getTenantRequestRejectedNotificationKey() {
		return tenantRequestRejectedNotificationKey;
	}

	public void setTenantRequestRejectedNotificationKey(UUID tenantRequestRejectedNotificationKey) {
		this.tenantRequestRejectedNotificationKey = tenantRequestRejectedNotificationKey;
	}

	public TenantRequestApprovedTemplateKeys getTenantRequestApprovedTemplate() {
		return tenantRequestApprovedTemplate;
	}

	public void setTenantRequestApprovedTemplate(TenantRequestApprovedTemplateKeys tenantRequestApprovedTemplate) {
		this.tenantRequestApprovedTemplate = tenantRequestApprovedTemplate;
	}

	public TenantRequestRejectedTemplateKeys getTenantRequestRejectedTemplate() {
		return tenantRequestRejectedTemplate;
	}

	public void setTenantRequestRejectedTemplate(TenantRequestRejectedTemplateKeys tenantRequestRejectedTemplate) {
		this.tenantRequestRejectedTemplate = tenantRequestRejectedTemplate;
	}
}

