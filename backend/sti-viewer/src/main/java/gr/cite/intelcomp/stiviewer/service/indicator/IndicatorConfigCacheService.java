package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.intelcomp.stiviewer.event.IndicatorTouchedEvent;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

@Service
public class IndicatorConfigCacheService extends CacheService<IndicatorConfigCacheService.IndicatorConfigCacheValue> {

    public static class IndicatorConfigCacheValue {

        public IndicatorConfigCacheValue() {
        }

        public IndicatorConfigCacheValue(UUID id, IndicatorConfigItem config) {
            this.id = id;
            this.config = config;
        }

        private UUID id;

        private IndicatorConfigItem config;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public IndicatorConfigItem getConfig() {
            return config;
        }

        public void setConfig(IndicatorConfigItem config) {
            this.config = config;
        }
    }

    @Autowired
    public IndicatorConfigCacheService(IndicatorConfigCacheOptions options) {
        super(options);
    }

    @EventListener
    public void handleIndicatorTouchedEvent(IndicatorTouchedEvent event) {
        if (event.getId() != null)
            this.evict(this.buildKey(event.getId()));
    }

    @Override
    protected Class<IndicatorConfigCacheValue> valueClass() {
        return IndicatorConfigCacheValue.class;
    }

    @Override
    public String keyOf(IndicatorConfigCacheValue value) {
        return this.buildKey(value.getId());
    }

    public String buildKey(UUID indicatorId) {
        HashMap<String, String> keyParts = new HashMap<>();
        keyParts.put("$indicator$", indicatorId.toString().toLowerCase(Locale.ROOT));
        return this.generateKey(keyParts);
    }
}
