package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.tools.cache.CacheOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.indicator-index")
public class IndicatorIndexCacheOptions extends CacheOptions {
}
