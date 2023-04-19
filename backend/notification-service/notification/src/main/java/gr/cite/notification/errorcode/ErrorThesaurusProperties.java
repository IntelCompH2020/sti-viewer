package gr.cite.notification.errorcode;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error-thesaurus")
public class ErrorThesaurusProperties {

	private ErrorDescription hashConflict;
	private ErrorDescription forbidden;
	private ErrorDescription systemError;
	private ErrorDescription missingTenant;
	private ErrorDescription modelValidation;
	private ErrorDescription nonPersonPrincipal;
	private ErrorDescription singleTenantConfigurationPerTypeSupported;
	private ErrorDescription incompatibleTenantConfigurationTypes;
	private ErrorDescription overlappingTenantConfigurationNotifierList;

	public ErrorDescription getHashConflict() {
		return hashConflict;
	}

	public void setHashConflict(ErrorDescription hashConflict) {
		this.hashConflict = hashConflict;
	}

	public ErrorDescription getForbidden() {
		return forbidden;
	}

	public void setForbidden(ErrorDescription forbidden) {
		this.forbidden = forbidden;
	}

	public ErrorDescription getSystemError() {
		return systemError;
	}

	public void setSystemError(ErrorDescription systemError) {
		this.systemError = systemError;
	}

	public ErrorDescription getMissingTenant() {
		return missingTenant;
	}

	public void setMissingTenant(ErrorDescription missingTenant) {
		this.missingTenant = missingTenant;
	}

	public ErrorDescription getModelValidation() {
		return modelValidation;
	}

	public void setModelValidation(ErrorDescription modelValidation) {
		this.modelValidation = modelValidation;
	}

	public ErrorDescription getNonPersonPrincipal() {
		return nonPersonPrincipal;
	}

	public void setNonPersonPrincipal(ErrorDescription nonPersonPrincipal) {
		this.nonPersonPrincipal = nonPersonPrincipal;
	}


	public ErrorDescription getSingleTenantConfigurationPerTypeSupported() {
		return singleTenantConfigurationPerTypeSupported;
	}

	public void setSingleTenantConfigurationPerTypeSupported(ErrorDescription singleTenantConfigurationPerTypeSupported) {
		this.singleTenantConfigurationPerTypeSupported = singleTenantConfigurationPerTypeSupported;
	}

	public ErrorDescription getIncompatibleTenantConfigurationTypes() {
		return incompatibleTenantConfigurationTypes;
	}

	public void setIncompatibleTenantConfigurationTypes(ErrorDescription incompatibleTenantConfigurationTypes) {
		this.incompatibleTenantConfigurationTypes = incompatibleTenantConfigurationTypes;
	}

	public ErrorDescription getOverlappingTenantConfigurationNotifierList() {
		return overlappingTenantConfigurationNotifierList;
	}

	public void setOverlappingTenantConfigurationNotifierList(ErrorDescription overlappingTenantConfigurationNotifierList) {
		this.overlappingTenantConfigurationNotifierList = overlappingTenantConfigurationNotifierList;
	}

}
