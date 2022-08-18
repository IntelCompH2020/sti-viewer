package gr.cite.notification.errorcode;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error-thesaurus")
public class ErrorThesaurusProperties {

	private ErrorDescription hashConflict;
	private ErrorDescription forbidden;
	private ErrorDescription systemError;
	private ErrorDescription missingTenant;
	private ErrorDescription invalidApiKey;
	private ErrorDescription staleApiKey;
	private ErrorDescription modelValidation;
	private ErrorDescription sensitiveInfo;
	private ErrorDescription nonPersonPrincipal;
	private ErrorDescription RouteeAuthenticationFailed;
	private ErrorDescription RouteeSendSmsFailed;
	private ErrorDescription RouteeTrackSmsFailed;
	private ErrorDescription slackBroadcastFailed;
	private ErrorDescription blockingConsent;
	private ErrorDescription whatYouKnowAboutMeIncompatibleState;
	private ErrorDescription existingPrimaryNotificationTemplate;
	private ErrorDescription singleTenantConfigurationPerTypeSupported;
	private ErrorDescription incompatibleTenantConfigurationTypes;
	private ErrorDescription missingTotpToken;
	private ErrorDescription overlappingTenantConfigurationNotifierList;
	private ErrorDescription notificationKindMustBeSet;

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

	public ErrorDescription getInvalidApiKey() {
		return invalidApiKey;
	}

	public void setInvalidApiKey(ErrorDescription invalidApiKey) {
		this.invalidApiKey = invalidApiKey;
	}

	public ErrorDescription getStaleApiKey() {
		return staleApiKey;
	}

	public void setStaleApiKey(ErrorDescription staleApiKey) {
		this.staleApiKey = staleApiKey;
	}

	public ErrorDescription getModelValidation() {
		return modelValidation;
	}

	public void setModelValidation(ErrorDescription modelValidation) {
		this.modelValidation = modelValidation;
	}

	public ErrorDescription getSensitiveInfo() {
		return sensitiveInfo;
	}

	public void setSensitiveInfo(ErrorDescription sensitiveInfo) {
		this.sensitiveInfo = sensitiveInfo;
	}

	public ErrorDescription getNonPersonPrincipal() {
		return nonPersonPrincipal;
	}

	public void setNonPersonPrincipal(ErrorDescription nonPersonPrincipal) {
		this.nonPersonPrincipal = nonPersonPrincipal;
	}

	public ErrorDescription getRouteeAuthenticationFailed() {
		return RouteeAuthenticationFailed;
	}

	public void setRouteeAuthenticationFailed(ErrorDescription routeeAuthenticationFailed) {
		RouteeAuthenticationFailed = routeeAuthenticationFailed;
	}

	public ErrorDescription getRouteeSendSmsFailed() {
		return RouteeSendSmsFailed;
	}

	public void setRouteeSendSmsFailed(ErrorDescription routeeSendSmsFailed) {
		RouteeSendSmsFailed = routeeSendSmsFailed;
	}

	public ErrorDescription getRouteeTrackSmsFailed() {
		return RouteeTrackSmsFailed;
	}

	public void setRouteeTrackSmsFailed(ErrorDescription routeeTrackSmsFailed) {
		RouteeTrackSmsFailed = routeeTrackSmsFailed;
	}

	public ErrorDescription getSlackBroadcastFailed() {
		return slackBroadcastFailed;
	}

	public void setSlackBroadcastFailed(ErrorDescription slackBroadcastFailed) {
		this.slackBroadcastFailed = slackBroadcastFailed;
	}

	public ErrorDescription getBlockingConsent() {
		return blockingConsent;
	}

	public void setBlockingConsent(ErrorDescription blockingConsent) {
		this.blockingConsent = blockingConsent;
	}

	public ErrorDescription getWhatYouKnowAboutMeIncompatibleState() {
		return whatYouKnowAboutMeIncompatibleState;
	}

	public void setWhatYouKnowAboutMeIncompatibleState(ErrorDescription whatYouKnowAboutMeIncompatibleState) {
		this.whatYouKnowAboutMeIncompatibleState = whatYouKnowAboutMeIncompatibleState;
	}

	public ErrorDescription getExistingPrimaryNotificationTemplate() {
		return existingPrimaryNotificationTemplate;
	}

	public void setExistingPrimaryNotificationTemplate(ErrorDescription existingPrimaryNotificationTemplate) {
		this.existingPrimaryNotificationTemplate = existingPrimaryNotificationTemplate;
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

	public ErrorDescription getMissingTotpToken() {
		return missingTotpToken;
	}

	public void setMissingTotpToken(ErrorDescription missingTotpToken) {
		this.missingTotpToken = missingTotpToken;
	}

	public ErrorDescription getOverlappingTenantConfigurationNotifierList() {
		return overlappingTenantConfigurationNotifierList;
	}

	public void setOverlappingTenantConfigurationNotifierList(ErrorDescription overlappingTenantConfigurationNotifierList) {
		this.overlappingTenantConfigurationNotifierList = overlappingTenantConfigurationNotifierList;
	}

	public ErrorDescription getNotificationKindMustBeSet() {
		return notificationKindMustBeSet;
	}

	public void setNotificationKindMustBeSet(ErrorDescription notificationKindMustBeSet) {
		this.notificationKindMustBeSet = notificationKindMustBeSet;
	}
}
