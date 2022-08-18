package gr.cite.intelcomp.stiviewer.authorization.cache;

import gr.cite.tools.cache.CacheOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.authorization-affiliated-indicators")
public class AffiliatedIndicatorsCacheOptions extends CacheOptions {
}
