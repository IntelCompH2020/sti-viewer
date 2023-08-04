package gr.cite.intelcomp.stiviewer.service.externaltoken;


import gr.cite.intelcomp.stiviewer.service.dataaccessrequest.DataAccessRequestApprovedTemplateKeys;
import gr.cite.intelcomp.stiviewer.service.dataaccessrequest.DataAccessRequestRejectedTemplateKeys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;

@ConfigurationProperties(prefix = "external-token")
public class ExternalTokenServiceProperties {
	private Boolean enabled;
	private String allowedSpecialChars;
	private int expirationInMinutes;
	private int passwordLength;

	public String getAllowedSpecialChars() {
		return allowedSpecialChars;
	}

	public void setAllowedSpecialChars(String allowedSpecialChars) {
		this.allowedSpecialChars = allowedSpecialChars;
	}

	public int getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(int passwordLength) {
		this.passwordLength = passwordLength;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public int getExpirationInMinutes() {
		return expirationInMinutes;
	}

	public void setExpirationInMinutes(int expirationInMinutes) {
		this.expirationInMinutes = expirationInMinutes;
	}
}

