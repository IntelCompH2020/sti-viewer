package gr.cite.intelcomp.stiviewer.authorization.cache;

import gr.cite.tools.cache.CacheOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.authorization-indicator-column-access")
public class IndicatorColumnAccessCacheOptions extends CacheOptions {
}
