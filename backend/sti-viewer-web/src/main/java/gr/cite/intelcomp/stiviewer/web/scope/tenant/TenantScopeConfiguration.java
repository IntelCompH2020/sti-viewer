package gr.cite.intelcomp.stiviewer.web.scope.tenant;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TenantScopeProperties.class)
public class TenantScopeConfiguration {
}
