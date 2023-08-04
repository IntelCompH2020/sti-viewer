package gr.cite.intelcomp.stiviewer.web.apikey;

import gr.cite.tools.cache.CacheOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.sti-api-key")
public class StiApiKeyCacheOptions extends CacheOptions {

}