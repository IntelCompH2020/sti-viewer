package gr.cite.user.locale;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LocaleProperties.class)
public class LocaleConfiguration {
}
