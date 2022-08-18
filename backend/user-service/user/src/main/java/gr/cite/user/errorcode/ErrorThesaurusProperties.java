package gr.cite.user.errorcode;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error-thesaurus")
public class ErrorThesaurusProperties {

	private ErrorDescription systemError;

	public ErrorDescription getSystemError() {
		return systemError;
	}

	public void setSystemError(ErrorDescription systemError) {
		this.systemError = systemError;
	}

	private ErrorDescription forbidden;

	public ErrorDescription getForbidden() {
		return forbidden;
	}

	public void setForbidden(ErrorDescription forbidden) {
		this.forbidden = forbidden;
	}

	private ErrorDescription hashConflict;

	public ErrorDescription getHashConflict() {
		return hashConflict;
	}

	public void setHashConflict(ErrorDescription hashConflict) {
		this.hashConflict = hashConflict;
	}


	private ErrorDescription missingTenant;

	public ErrorDescription getMissingTenant() {
		return missingTenant;
	}

	public void setMissingTenant(ErrorDescription missingTenant) {
		this.missingTenant = missingTenant;
	}

	private ErrorDescription modelValidation;

	public ErrorDescription getModelValidation() {
		return modelValidation;
	}

	public void setModelValidation(ErrorDescription modelValidation) {
		this.modelValidation = modelValidation;
	}

	private ErrorDescription tenantCodeRequired;

	public ErrorDescription getTenantCodeRequired() {
		return tenantCodeRequired;
	}

	public void setTenantCodeRequired(ErrorDescription tenantCodeRequired) {
		this.tenantCodeRequired = tenantCodeRequired;
	}


	private ErrorDescription tenantNameRequired;

	public ErrorDescription getTenantNameRequired() {
		return tenantNameRequired;
	}

	public void setTenantNameRequired(ErrorDescription tenantNameRequired) {
		this.tenantNameRequired = tenantNameRequired;
	}

	private ErrorDescription indexAlreadyExists;

	public ErrorDescription getIndexAlreadyExists() {
		return indexAlreadyExists;
	}

	public void setIndexAlreadyExists(ErrorDescription indexAlreadyExists) {
		this.indexAlreadyExists = indexAlreadyExists;
	}

	private ErrorDescription configRequired;

	public ErrorDescription getConfigRequired() {
		return configRequired;
	}

	public void setConfigRequired(ErrorDescription configRequired) {
		this.configRequired = configRequired;
	}

	private ErrorDescription configIndicatorsRequired;

	public ErrorDescription getConfigIndicatorsRequired() {
		return configIndicatorsRequired;
	}

	public void setConfigIndicatorsRequired(ErrorDescription configIndicatorsRequired) {
		this.configIndicatorsRequired = configIndicatorsRequired;
	}

	private ErrorDescription tenantNotAllowed;

	public ErrorDescription getTenantNotAllowed() {
		return tenantNotAllowed;
	}

	public void setTenantNotAllowed(ErrorDescription tenantNotAllowed) {
		this.tenantNotAllowed = tenantNotAllowed;
	}


}
