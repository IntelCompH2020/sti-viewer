package gr.cite.notification.config.notification;

import gr.cite.tools.cache.CacheOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.template")
public class NotificationTemplateCacheOptions  extends CacheOptions {
}
