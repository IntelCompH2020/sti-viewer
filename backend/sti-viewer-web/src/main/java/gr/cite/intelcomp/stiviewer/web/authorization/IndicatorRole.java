package gr.cite.intelcomp.stiviewer.web.authorization;

import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Set;

public class IndicatorRole {
	private final Set<String> roles;

	@ConstructorBinding
	public IndicatorRole(Set<String> roles) {
		this.roles = roles;
	}

	public Set<String> getRoles() {
		return roles;
	}

}
