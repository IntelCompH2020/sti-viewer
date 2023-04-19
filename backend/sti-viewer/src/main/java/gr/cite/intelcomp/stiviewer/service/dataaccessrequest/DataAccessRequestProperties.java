package gr.cite.intelcomp.stiviewer.service.dataaccessrequest;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.UUID;

@ConfigurationProperties(prefix = "data-access-request")
public class DataAccessRequestProperties {
	private UUID dataAccessRequestApprovedNotificationKey;
	private UUID dataAccessRequestRejectedNotificationKey;
	private DataAccessRequestApprovedTemplateKeys dataAccessRequestApprovedTemplate;
	private DataAccessRequestRejectedTemplateKeys dataAccessRequestRejectedTemplate;

	public UUID getDataAccessRequestApprovedNotificationKey() {
		return dataAccessRequestApprovedNotificationKey;
	}

	public void setDataAccessRequestApprovedNotificationKey(UUID dataAccessRequestApprovedNotificationKey) {
		this.dataAccessRequestApprovedNotificationKey = dataAccessRequestApprovedNotificationKey;
	}

	public UUID getDataAccessRequestRejectedNotificationKey() {
		return dataAccessRequestRejectedNotificationKey;
	}

	public void setDataAccessRequestRejectedNotificationKey(UUID dataAccessRequestRejectedNotificationKey) {
		this.dataAccessRequestRejectedNotificationKey = dataAccessRequestRejectedNotificationKey;
	}

	public DataAccessRequestApprovedTemplateKeys getDataAccessRequestApprovedTemplate() {
		return dataAccessRequestApprovedTemplate;
	}

	public void setDataAccessRequestApprovedTemplate(DataAccessRequestApprovedTemplateKeys dataAccessRequestApprovedTemplate) {
		this.dataAccessRequestApprovedTemplate = dataAccessRequestApprovedTemplate;
	}

	public DataAccessRequestRejectedTemplateKeys getDataAccessRequestRejectedTemplate() {
		return dataAccessRequestRejectedTemplate;
	}

	public void setDataAccessRequestRejectedTemplate(DataAccessRequestRejectedTemplateKeys dataAccessRequestRejectedTemplate) {
		this.dataAccessRequestRejectedTemplate = dataAccessRequestRejectedTemplate;
	}

	

	
}

