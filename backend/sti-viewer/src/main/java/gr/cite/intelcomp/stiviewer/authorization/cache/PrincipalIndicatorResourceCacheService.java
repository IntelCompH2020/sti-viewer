package gr.cite.intelcomp.stiviewer.authorization.cache;

import gr.cite.intelcomp.stiviewer.authorization.IndicatorRolesResource;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

@Service
public class PrincipalIndicatorResourceCacheService extends CacheService<PrincipalIndicatorResourceCacheService.PrincipalIndicatorResourceCacheValue> {

    public static class PrincipalIndicatorResourceCacheValue {

        public PrincipalIndicatorResourceCacheValue() {
        }

        public PrincipalIndicatorResourceCacheValue(UUID userId, UUID entityId, String entity, IndicatorRolesResource indicatorRolesResource) {
            this.userId = userId;
            this.entityId = entityId;
            this.entity = entity;
            this.indicatorRolesResource = indicatorRolesResource;
        }

        private UUID userId;
        private UUID entityId;
        private String entity;
        private IndicatorRolesResource indicatorRolesResource;

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        public IndicatorRolesResource getIndicatorRolesResource() {
            return indicatorRolesResource;
        }

        public void setIndicatorRolesResource(IndicatorRolesResource indicatorRolesResource) {
            this.indicatorRolesResource = indicatorRolesResource;
        }

        public UUID getEntityId() {
            return entityId;
        }

        public void setEntityId(UUID entityId) {
            this.entityId = entityId;
        }
    }

    @Autowired
    public PrincipalIndicatorResourceCacheService(PrincipalIndicatorResourceCacheOptions options) {
        super(options);
    }

    @Override
    protected Class<PrincipalIndicatorResourceCacheValue> valueClass() {
        return PrincipalIndicatorResourceCacheValue.class;
    }

    @Override
    public String keyOf(PrincipalIndicatorResourceCacheValue value) {
        return this.buildKey(value.getUserId(), value.getEntityId(), value.getEntity());
    }

    public String buildKey(UUID userId, UUID entityId, String entity) {
        HashMap<String, String> keyParts = new HashMap<>();
        keyParts.put("$user_id$", userId.toString().toLowerCase(Locale.ROOT));
        keyParts.put("$entity_id$", entityId.toString().toLowerCase(Locale.ROOT));
        keyParts.put("$entity_type$", entity.toLowerCase(Locale.ROOT));
        return this.generateKey(keyParts);
    }
}
