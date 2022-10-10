package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.intelcomp.stiviewer.config.elastic.AppElasticProperties;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import gr.cite.tools.data.query.QueryFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class IndicatorConfigServiceImpl implements IndicatorConfigService {
	private final QueryFactory queryFactory;
	private final ElasticsearchRestTemplate elasticsearchTemplate;
	private final IndicatorConfigCacheService indicatorConfigCacheService;
	private final AppElasticProperties appElasticProperties;

	public IndicatorConfigServiceImpl(
			QueryFactory queryFactory,
			ElasticsearchRestTemplate elasticsearchTemplate,
			IndicatorConfigCacheService indicatorConfigCacheService,
			AppElasticProperties appElasticProperties
	) {
		this.queryFactory = queryFactory;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.indicatorConfigCacheService = indicatorConfigCacheService;
		this.appElasticProperties = appElasticProperties;
	}

	@Override
	public IndicatorConfigItem getConfig(@NotNull UUID indicatorId) {
		IndicatorConfigCacheService.IndicatorConfigCacheValue indicatorConfigItem = this.indicatorConfigCacheService.lookup(this.indicatorConfigCacheService.buildKey(indicatorId));
		if (indicatorConfigItem == null) {
			IndicatorElasticEntity indicatorElasticEntity = this.elasticsearchTemplate.get(indicatorId.toString(), IndicatorElasticEntity.class, IndexCoordinates.of(this.appElasticProperties.getIndicatorIndexName()));
			indicatorConfigItem = new IndicatorConfigCacheService.IndicatorConfigCacheValue(indicatorId, new IndicatorConfigItem(indicatorId, indicatorElasticEntity.getSchema().getFields()));
			this.indicatorConfigCacheService.put(indicatorConfigItem);
		}
		return indicatorConfigItem.getConfig();
	}


	@Override
	public String ensurePropertyName(@NotNull String prop) {
		return prop.replace(".", "_dot_");
	}
}
