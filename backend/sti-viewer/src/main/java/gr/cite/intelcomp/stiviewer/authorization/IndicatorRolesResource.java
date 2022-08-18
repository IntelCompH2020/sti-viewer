package gr.cite.intelcomp.stiviewer.authorization;

import gr.cite.commons.web.authz.policy.AuthorizationResource;

import java.util.List;
import java.util.UUID;

public class IndicatorRolesResource extends AuthorizationResource {
	private List<String> indicatorRoles;
	private final UUID userId;

	public IndicatorRolesResource(UUID userId) {
		this.userId = userId;
	}

	public List<String> getIndicatorRoles() {
		return indicatorRoles;
	}

	public void setIndicatorRoles(List<String> indicatorRoles) {
		this.indicatorRoles = indicatorRoles;
	}

	public UUID getUserId() {
		return userId;
	}
}
