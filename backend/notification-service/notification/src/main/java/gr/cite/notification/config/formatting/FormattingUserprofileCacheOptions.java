package gr.cite.notification.config.formatting;

import gr.cite.tools.cache.CacheOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.formatting-user-profile-cache")
public class FormattingUserprofileCacheOptions extends CacheOptions {
}
