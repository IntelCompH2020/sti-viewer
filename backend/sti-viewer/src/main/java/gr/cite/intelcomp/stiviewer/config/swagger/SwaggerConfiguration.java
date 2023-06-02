package gr.cite.intelcomp.stiviewer.config.swagger;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "swagger", name = "enabled", matchIfMissing = false)
public class SwaggerConfiguration {
	private final SwaggerProperties properties;
	private final String securitySchemeName = "bearerAuth";
	private final String securityScheme = "bearer";
	private final String securityBearerFormat = "JWT";

	@Autowired
	public SwaggerConfiguration(SwaggerProperties properties) {
		this.properties = properties;
	}

	public SwaggerProperties getProperties() {
		return properties;
	}

	@Bean
	public OpenAPI openAPI() {
		License license = new License();
		Info info = new Info().license(license);
		if (this.getProperties().getTitle() != null && !this.getProperties().getTitle().isBlank()) info.title(this.getProperties().getTitle());
		if (this.getProperties().getVersion() != null && !this.getProperties().getVersion().isBlank()) info.version(this.getProperties().getVersion());
		if (this.getProperties().getTermsUrl() != null && !this.getProperties().getTermsUrl().isBlank()) info.termsOfService(this.getProperties().getTermsUrl());
		if (this.getProperties().getTitle() != null && !this.getProperties().getTitle().isBlank()) info.title(this.getProperties().getTitle());
		if (this.getProperties().getLicense() != null && !this.getProperties().getLicense().isBlank()) license.name(this.getProperties().getLicense());
		if (this.getProperties().getLicenseUrl() != null && !this.getProperties().getLicenseUrl().isBlank()) license.setUrl(this.getProperties().getTitle());

		OpenAPI openAPI = new OpenAPI().info(info);
		if (this.getProperties().getEnableBearerAuth()){
			openAPI.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
					.components(
							new Components()
									.addSecuritySchemes(securitySchemeName,
											new SecurityScheme()
													.name(securitySchemeName)
													.type(SecurityScheme.Type.HTTP)
													.scheme(securityScheme)
													.bearerFormat(securityBearerFormat)
									)
					);
		}
		if (this.getProperties().getApiServers() != null && !this.getProperties().getApiServers().isEmpty()) {
			List<Server> servers = new ArrayList<>();
			for (SwaggerProperties.ApiServer apiServer: this.getProperties().getApiServers()) {
				Server server = new Server();
				if (apiServer.getUrl() != null && !apiServer.getUrl().isBlank()) server.setUrl(apiServer.getUrl());
				if (apiServer.getDescription() != null && !apiServer.getDescription().isBlank()) server.setDescription(apiServer.getDescription());
				servers.add(server);
			}
			openAPI.servers(servers);
		}
				
		return openAPI;
	}
}

