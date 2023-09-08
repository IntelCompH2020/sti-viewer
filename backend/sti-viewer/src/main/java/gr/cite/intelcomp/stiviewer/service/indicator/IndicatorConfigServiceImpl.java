package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.intelcomp.stiviewer.config.elastic.AppElasticProperties;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Objects;
import java.util.UUID;

@Service
@RequestScope
public class IndicatorConfigServiceImpl implements IndicatorConfigService {
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final IndicatorConfigCacheService indicatorConfigCacheService;
    private final AppElasticProperties appElasticProperties;

    public IndicatorConfigServiceImpl(
            ElasticsearchRestTemplate elasticsearchTemplate,
            IndicatorConfigCacheService indicatorConfigCacheService,
            AppElasticProperties appElasticProperties
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.indicatorConfigCacheService = indicatorConfigCacheService;
        this.appElasticProperties = appElasticProperties;
    }

    @Override
    public IndicatorConfigItem getConfig(@NotNull UUID indicatorId) {
        IndicatorConfigCacheService.IndicatorConfigCacheValue indicatorConfigItem = this.indicatorConfigCacheService.lookup(this.indicatorConfigCacheService.buildKey(indicatorId));
        if (indicatorConfigItem == null) {
            IndicatorElasticEntity indicatorElasticEntity = this.elasticsearchTemplate.get(indicatorId.toString(), IndicatorElasticEntity.class, IndexCoordinates.of(this.appElasticProperties.getIndicatorIndexName()));
            indicatorConfigItem = new IndicatorConfigCacheService.IndicatorConfigCacheValue(indicatorId, new IndicatorConfigItem(indicatorId, Objects.requireNonNull(indicatorElasticEntity).getSchema().getFields()));
            this.indicatorConfigCacheService.put(indicatorConfigItem);
        }
        return indicatorConfigItem.getConfig();
    }


    @Override
    public String ensurePropertyName(@NotNull String prop) {
        return prop.replace(".", "_dot_");
    }
}
