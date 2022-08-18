package gr.cite.intelcomp.stiviewer.web.authorization;

import gr.cite.commons.web.authz.configuration.AuthorizationConfiguration;
import gr.cite.commons.web.authz.handler.AuthorizationHandler;
import gr.cite.commons.web.authz.handler.AuthorizationHandlerContext;
import gr.cite.commons.web.authz.policy.AuthorizationRequirement;
import gr.cite.commons.web.oidc.principal.MyPrincipal;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Component("timeOfDayAuthorizationHandler")
public class TimeOfDayAuthorizationHandler extends AuthorizationHandler<TimeOfDayAuthorizationRequirement> {

	private final AuthorizationConfiguration configuration;
	private final MyCustomPermissionAttributesConfiguration myConfiguration;
	private final ClaimExtractor claimExtractor;

	@Autowired
	public TimeOfDayAuthorizationHandler(AuthorizationConfiguration configuration, MyCustomPermissionAttributesConfiguration myConfiguration, ClaimExtractor claimExtractor) {
		this.configuration = configuration;
		this.myConfiguration = myConfiguration;
		this.claimExtractor = claimExtractor;
	}

	@Override
	public int handleRequirement(AuthorizationHandlerContext context, Object resource, AuthorizationRequirement requirement) {
		TimeOfDayAuthorizationRequirement req = (TimeOfDayAuthorizationRequirement) requirement;
		if (req.getTimeOfDay() == null) return ACCESS_NOT_DETERMINED;

		boolean isAuthenticated = ((MyPrincipal) context.getPrincipal()).isAuthenticated();
		if (!isAuthenticated && req.isIgnoreIfNotAuthenticated()) return ACCESS_NOT_DETERMINED;

		if (myConfiguration.getMyPolicies() == null) return ACCESS_NOT_DETERMINED;

		int hits = 0;
		if (isAuthenticated) {
			List<String> roles = claimExtractor.roles((MyPrincipal) context.getPrincipal());
			Set<String> permissions = configuration.permissionsOfRoles(roles);
			for (String permission : permissions) {
				MyCustomPermissionAttributesProperties.MyPermission policy = myConfiguration.getMyPolicies().get(permission);
				if (policy != null) {
					LocalTime startingAt = LocalTime.of(
							Integer.parseInt(policy.getTimeOfDay().getStartingAt().split(":")[0]),
							Integer.parseInt(policy.getTimeOfDay().getStartingAt().split(":")[1])
					);
					LocalTime endingAt = LocalTime.of(
							Integer.parseInt(policy.getTimeOfDay().getEndingAt().split(":")[0]),
							Integer.parseInt(policy.getTimeOfDay().getEndingAt().split(":")[1])
					);
					LocalTime now = LocalTime.now();
					if (now.isAfter(startingAt) && now.isBefore(endingAt)) hits++;
				}
			}
		} else {
			if (!req.isIgnoreIfNotAuthenticated()) {
				LocalTime startingAt = LocalTime.of(
						Integer.parseInt(req.getTimeOfDay().getStartingAt().split(":")[0]),
						Integer.parseInt(req.getTimeOfDay().getStartingAt().split(":")[1])
				);
				LocalTime endingAt = LocalTime.of(
						Integer.parseInt(req.getTimeOfDay().getEndingAt().split(":")[0]),
						Integer.parseInt(req.getTimeOfDay().getEndingAt().split(":")[1])
				);
				LocalTime now = LocalTime.now();
				if (now.isAfter(startingAt) && now.isBefore(endingAt)) return ACCESS_GRANTED;
			}
		}
		if (hits == myConfiguration.getMyPolicies().size()) return ACCESS_GRANTED;

		return ACCESS_NOT_DETERMINED;
	}

	@Override
	public Class<? extends AuthorizationRequirement> supporting() {
		return TimeOfDayAuthorizationRequirement.class;
	}

}
