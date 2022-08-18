package gr.cite.notification.config.formatting;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FormattingServiceProperties.class)
public class FormattingServiceConfiguration {
}
