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
        private UUID tenant;
        private String type;
        private String channel;
        private String language;

        public NotificationTemplateCacheValue() {
        }

        public NotificationTemplateCacheValue(String prefix, String key, String pattern) {
            this.prefix = prefix;
            this.key = key;
            this.pattern = pattern;
        }

        public NotificationTemplateCacheValue(String prefix, String pattern, UUID tenant, String type, String channel, String language) {
            this.prefix = prefix;
            this.pattern = pattern;
            this.tenant = tenant;
            this.type = type;
            this.channel = channel;
            this.language = language;
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

        public UUID getTenant() {
            return tenant;
        }

        public void setTenant(UUID tenant) {
            this.tenant = tenant;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
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
                .replace("{key}", value.getKey())
                .replace("{tenant}", value.getTenant().toString())
                .replace("{channel}", value.getChannel())
                .replace("{type}", value.getType())
                .replace("{{language}}", value.getLanguage());
    }
}
