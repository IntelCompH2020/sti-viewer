package gr.cite.notification.web.authorization;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.HashMap;
import java.util.List;

@ConstructorBinding
@ConfigurationProperties(prefix = "permissions")
@ConditionalOnProperty(prefix = "permissions", name = "enabled", havingValue = "true")
public class MyCustomPermissionAttributesProperties {

	private final List<String> extendedClaims;
	private final HashMap<String, MyPermission> policies;

	@ConstructorBinding
	public MyCustomPermissionAttributesProperties(List<String> extendedClaims, HashMap<String, MyPermission> policies) {
		this.extendedClaims = extendedClaims;
		this.policies = policies;
	}

	public List<String> getExtendedClaims() {
		return extendedClaims;
	}

	public HashMap<String, MyPermission> getPolicies() {
		return policies;
	}

	public static class MyPermission {

		private final TimeOfDay timeOfDay;
		private final IndicatorRole indicator;

		@ConstructorBinding
		public MyPermission(TimeOfDay timeOfDay, IndicatorRole indicator) {
			this.timeOfDay = timeOfDay;
			this.indicator = indicator;
		}

		public TimeOfDay getTimeOfDay() {
			return timeOfDay;
		}

		public IndicatorRole getIndicator() {
			return indicator;
		}
	}

}
