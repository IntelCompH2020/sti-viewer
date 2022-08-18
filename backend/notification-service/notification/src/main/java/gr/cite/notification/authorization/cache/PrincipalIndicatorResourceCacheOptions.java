package gr.cite.notification.authorization.cache;

import gr.cite.tools.cache.CacheOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.authorization-principal-indicator-resource")
public class PrincipalIndicatorResourceCacheOptions extends CacheOptions {
}
