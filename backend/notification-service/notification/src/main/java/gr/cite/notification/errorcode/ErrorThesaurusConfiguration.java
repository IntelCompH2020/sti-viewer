package gr.cite.notification.errorcode;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ErrorThesaurusProperties.class)
public class ErrorThesaurusConfiguration {

//	private final ErrorThesaurusProperties properties;
//
//	@Autowired
//	public ErrorThesaurusConfiguration(ErrorThesaurusProperties properties) {
//		this.properties = properties;
//	}
//
//	public ErrorThesaurusProperties getProperties() {
//		return properties;
//	}
}
