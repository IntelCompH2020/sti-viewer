package gr.cite.intelcomp.stiviewer.elastic;


import gr.cite.intelcomp.stiviewer.elastic.converter.*;
import gr.cite.tools.elastic.configuration.AbstractElasticConfiguration;
import gr.cite.tools.elastic.configuration.ElasticCertificateProvider;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

import java.util.List;

@Configuration
@EnableConfigurationProperties(ElasticProperties.class)
@ConditionalOnProperty(prefix = "elastic", name = "enabled", matchIfMissing = false)
public class ElasticConfiguration extends AbstractElasticConfiguration {
	private final ApplicationContext applicationContext;

	public ElasticConfiguration(ApplicationContext applicationContext, ElasticProperties elasticProperties, ElasticCertificateProvider elasticCertificateProvider) {
		super(elasticProperties, elasticCertificateProvider);
		this.applicationContext = applicationContext;
	}

	@Bean
	@Override
	public ElasticsearchCustomConversions elasticsearchCustomConversions() {
		return new ElasticsearchCustomConversions(
				List.of(new IndicatorPointEntityConverter(this.applicationContext)
						, new IndicatorFieldBaseTypeToStringConverter()
						, new StringToIndicatorFieldBaseTypeConverter()
						, new IndicatorPointValidationTypeToStringConverter()
						, new StringToIndicatorPointValidationTypeConverter()
				));
	}
}
