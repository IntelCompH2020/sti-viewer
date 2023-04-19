package gr.cite.notification.cache;

import gr.cite.notification.config.notification.NotificationTemplateCacheOptions;
import gr.cite.tools.cache.CacheOptions;
import gr.cite.tools.cache.CacheService;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationTemplateCache extends CacheService<NotificationTemplateCache.NotificationTemplateCacheValue> {

    public static class NotificationTemplateCacheValue {
        private String prefix;
        private String key;
        private String pattern;

        public NotificationTemplateCacheValue() {
        }

        public NotificationTemplateCacheValue(String prefix, String key, String pattern) {
            this.prefix = prefix;
            this.key = key;
            this.pattern = pattern;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }

    public NotificationTemplateCache(NotificationTemplateCacheOptions options) {
        super(options);
    }

    private Cache cache(){
        return this.cacheManager.getCache(this.options.getName());
    }

    public String lookup(NotificationTemplateCacheValue key) {
        return this.cache().get(this.keyOf(key), String.class);
    }

    public void put(NotificationTemplateCacheValue key, String value) {
        this.cache().put(this.keyOf(key), value);
    }
    @Override
    protected Class<NotificationTemplateCacheValue> valueClass() {
        return NotificationTemplateCacheValue.class;
    }

    @Override
    public String keyOf(NotificationTemplateCacheValue value) {
        return this.buildKey(value);
    }

    private String buildKey(NotificationTemplateCacheValue value) {
        return value.getPattern()
                .replace("{prefix}", value.getPrefix())
                .replace("{key}", value.getKey());
    }
}
