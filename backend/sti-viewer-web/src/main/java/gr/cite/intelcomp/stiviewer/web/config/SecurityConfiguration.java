package gr.cite.intelcomp.stiviewer.web.config;

import gr.cite.commons.web.authz.handler.AuthorizationHandler;
import gr.cite.commons.web.authz.handler.PermissionClientAuthorizationHandler;
import gr.cite.commons.web.authz.policy.AuthorizationRequirement;
import gr.cite.commons.web.authz.policy.AuthorizationRequirementMapper;
import gr.cite.commons.web.authz.policy.AuthorizationResource;
import gr.cite.commons.web.authz.policy.resolver.AuthorizationPolicyConfigurer;
import gr.cite.commons.web.authz.policy.resolver.AuthorizationPolicyResolverStrategy;
import gr.cite.commons.web.oidc.configuration.WebSecurityProperties;
import gr.cite.intelcomp.stiviewer.authorization.IndicatorRolesAuthorizationRequirement;
import gr.cite.intelcomp.stiviewer.authorization.IndicatorRolesResource;
import gr.cite.intelcomp.stiviewer.authorization.OwnedAuthorizationRequirement;
import gr.cite.intelcomp.stiviewer.authorization.OwnedResource;
import gr.cite.intelcomp.stiviewer.web.apikey.StiApiKeyFilter;
import gr.cite.intelcomp.stiviewer.web.authorization.IndicatorRolesAuthorizationHandler;
import gr.cite.intelcomp.stiviewer.web.authorization.OwnedAuthorizationHandler;
import gr.cite.intelcomp.stiviewer.web.authorization.TimeOfDayAuthorizationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final WebSecurityProperties webSecurityProperties;
	private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;
	private final Filter apiKeyFilter;
	private final TimeOfDayAuthorizationHandler timeOfDayAuthorizationHandler;
	private final IndicatorRolesAuthorizationHandler indicatorRolesAuthorizationHandler;
	private final OwnedAuthorizationHandler ownedAuthorizationHandler;

	@Autowired
	public SecurityConfiguration(WebSecurityProperties webSecurityProperties,
	                             @Qualifier("tokenAuthenticationResolver") AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
								 //@Qualifier("apiKeyFilter") Filter apiKeyFilter,
								 StiApiKeyFilter apiKeyFilter,
	                             @Qualifier("timeOfDayAuthorizationHandler") TimeOfDayAuthorizationHandler timeOfDayAuthorizationHandler,
	                             @Qualifier("indicatorRolesAuthorizationHandler") IndicatorRolesAuthorizationHandler indicatorRolesAuthorizationHandler,
	                             @Qualifier("ownedAuthorizationHandler") OwnedAuthorizationHandler ownedAuthorizationHandler) {
		this.webSecurityProperties = webSecurityProperties;
		this.authenticationManagerResolver = authenticationManagerResolver;
		this.apiKeyFilter = apiKeyFilter;
		this.timeOfDayAuthorizationHandler = timeOfDayAuthorizationHandler;
		this.indicatorRolesAuthorizationHandler = indicatorRolesAuthorizationHandler;
		this.ownedAuthorizationHandler = ownedAuthorizationHandler;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.cors()
				.and()
				.addFilterBefore(apiKeyFilter, AbstractPreAuthenticatedProcessingFilter.class)
				.authorizeRequests()
				.antMatchers(buildAntPatterns(webSecurityProperties.getAllowedEndpoints())).anonymous()
				.antMatchers(buildAntPatterns(webSecurityProperties.getAuthorizedEndpoints())).authenticated()
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
				.oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver));
	}

	@Bean
	AuthorizationPolicyConfigurer authorizationPolicyConfigurer() {
		return new AuthorizationPolicyConfigurer() {

			@Override
			public AuthorizationPolicyResolverStrategy strategy() {
				return AuthorizationPolicyResolverStrategy.STRICT_CONSENSUS_BASED;
			}

			//Here you can register your custom authorization handlers, which will get used as well as the existing ones
			//This is optional and can be omitted
			//If not set / set to null, only the default authorization handlers will be used
			@Override
			public List<AuthorizationHandler<? extends AuthorizationRequirement>> addCustomHandlers() {
				return List.of(timeOfDayAuthorizationHandler, indicatorRolesAuthorizationHandler, ownedAuthorizationHandler);
			}

			//Here you can register your custom authorization requirements (if any)
			//This is optional and can be omitted
			//If not set / set to null, only the default authorization requirements will be used
			@Override
			public List<? extends AuthorizationRequirement> extendRequirements() {
				return List.of(
//                        new TimeOfDayAuthorizationRequirement(new TimeOfDay("08:00","16:00"), true)
				);
			}

			//Here you can select handlers you want to disable by providing the classes they are implemented by
			//You can disable any handler (including any custom one)
			//This is optional and can be omitted
			//If not set / set to null, all the handlers will be invoked, based on their requirement support
			//In the example below, the default client handler will be ignored by the resolver
			@Override
			public List<Class<? extends AuthorizationHandler<? extends AuthorizationRequirement>>> disableHandlers() {
				return List.of(PermissionClientAuthorizationHandler.class);
			}
		};
	}

	@Bean
	AuthorizationRequirementMapper authorizationRequirementMapper() {
		return new AuthorizationRequirementMapper() {
			@Override
			public AuthorizationRequirement map(AuthorizationResource resource, boolean matchAll, String[] permissions) {
				Class<?> type = resource.getClass();
				if (!AuthorizationResource.class.isAssignableFrom(type)) throw new IllegalArgumentException("resource");

				if (IndicatorRolesResource.class.equals(type)) {
					return new IndicatorRolesAuthorizationRequirement(matchAll, permissions);
				}
				if (OwnedResource.class.equals(type)) {
					return new OwnedAuthorizationRequirement();
				}
				throw new IllegalArgumentException("resource");
			}
		};
	}

	private String[] buildAntPatterns(Set<String> endpoints) {
		if (endpoints == null) {
			return new String[0];
		}
		return endpoints.stream()
				.filter(endpoint -> endpoint != null && !endpoint.isBlank())
				.map(endpoint -> "/" + stripUnnecessaryCharacters(endpoint) + "/**")
				.toArray(String[]::new);
	}

	private String stripUnnecessaryCharacters(String endpoint) {
		endpoint = endpoint.strip();
		if (endpoint.startsWith("/")) {
			endpoint = endpoint.substring(1);
		}
		if (endpoint.endsWith("/")) {
			endpoint = endpoint.substring(0, endpoint.length() - 1);
		}
		return endpoint;
	}
}
