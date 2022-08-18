package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import gr.cite.tools.data.query.QueryFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Service
@RequestScope
public class IndicatorConfigServiceImpl implements IndicatorConfigService {
	private final QueryFactory queryFactory;
	private final ElasticsearchRestTemplate elasticsearchTemplate;
	private final IndicatorConfigCacheService indicatorConfigCacheService;

	public IndicatorConfigServiceImpl(
			QueryFactory queryFactory,
			ElasticsearchRestTemplate elasticsearchTemplate,
			IndicatorConfigCacheService indicatorConfigCacheService
	) {
		this.queryFactory = queryFactory;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.indicatorConfigCacheService = indicatorConfigCacheService;
	}

	@Override
	public IndicatorConfigItem getConfig(@NotNull UUID indicatorId) {
		IndicatorConfigCacheService.IndicatorConfigCacheValue indicatorConfigItem = this.indicatorConfigCacheService.lookup(this.indicatorConfigCacheService.buildKey(indicatorId));
		if (indicatorConfigItem == null) {
			IndicatorElasticEntity indicatorElasticEntity = this.elasticsearchTemplate.get(indicatorId.toString(), IndicatorElasticEntity.class);
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
