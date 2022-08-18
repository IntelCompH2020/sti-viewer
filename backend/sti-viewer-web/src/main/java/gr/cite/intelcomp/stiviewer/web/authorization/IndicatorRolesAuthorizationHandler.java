package gr.cite.intelcomp.stiviewer.web.authorization;

import gr.cite.commons.web.authz.configuration.AuthorizationConfiguration;
import gr.cite.commons.web.authz.handler.AuthorizationHandler;
import gr.cite.commons.web.authz.handler.AuthorizationHandlerContext;
import gr.cite.commons.web.authz.policy.AuthorizationRequirement;
import gr.cite.commons.web.oidc.principal.MyPrincipal;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.authorization.IndicatorRolesAuthorizationRequirement;
import gr.cite.intelcomp.stiviewer.authorization.IndicatorRolesResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("indicatorRolesAuthorizationHandler")
public class IndicatorRolesAuthorizationHandler extends AuthorizationHandler<IndicatorRolesAuthorizationRequirement> {

	private final AuthorizationConfiguration configuration;
	private final MyCustomPermissionAttributesConfiguration myConfiguration;
	private final ClaimExtractor claimExtractor;

	@Autowired
	public IndicatorRolesAuthorizationHandler(AuthorizationConfiguration configuration, MyCustomPermissionAttributesConfiguration myConfiguration, ClaimExtractor claimExtractor) {
		this.configuration = configuration;
		this.myConfiguration = myConfiguration;
		this.claimExtractor = claimExtractor;
	}

	@Override
	public int handleRequirement(AuthorizationHandlerContext context, Object resource, AuthorizationRequirement requirement) {
		IndicatorRolesAuthorizationRequirement req = (IndicatorRolesAuthorizationRequirement) requirement;
		if (req.getRequiredPermissions() == null) return ACCESS_NOT_DETERMINED;

		IndicatorRolesResource rs = (IndicatorRolesResource) resource;

		boolean isAuthenticated = ((MyPrincipal) context.getPrincipal()).isAuthenticated();
		if (!isAuthenticated) return ACCESS_NOT_DETERMINED;

		if (myConfiguration.getMyPolicies() == null) return ACCESS_NOT_DETERMINED;

		int hits = 0;
		if (isAuthenticated) {

			List<String> roles = rs != null && rs.getIndicatorRoles() != null ? rs.getIndicatorRoles() : null;

			for (String permission : req.getRequiredPermissions()) {
				MyCustomPermissionAttributesProperties.MyPermission policy = myConfiguration.getMyPolicies().get(permission);
				boolean hasPermission = hasPermission(policy.getIndicator(), roles);
				if (hasPermission) hits += 1;
			}
		}
		if ((req.getMatchAll() && req.getRequiredPermissions().size() == hits) || (!req.getMatchAll() && hits > 0)) return ACCESS_GRANTED;

		return ACCESS_NOT_DETERMINED;
	}

	private Boolean hasPermission(IndicatorRole indicatorRole, List<String> roles) {
		if (roles == null) return false;
		if (indicatorRole == null || indicatorRole.getRoles() == null) return false;
		Boolean hasRole = false;
		for (String role : indicatorRole.getRoles()) {
			if (roles.contains(role)) return true;
		}
		return false;
	}

	@Override
	public Class<? extends AuthorizationRequirement> supporting() {
		return IndicatorRolesAuthorizationRequirement.class;
	}

}
