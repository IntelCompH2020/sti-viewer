package gr.cite.intelcomp.stiviewer.authorization.cache;

import gr.cite.intelcomp.stiviewer.authorization.HierarchyIndicatorColumnAccess;
import gr.cite.tools.cache.CacheService;
import gr.cite.tools.cipher.CipherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HierarchyIndicatorColumnAccessCacheService extends CacheService<HierarchyIndicatorColumnAccessCacheService.HierarchyIndicatorColumnAccessCacheValue> {

    public static class HierarchyIndicatorColumnAccessCacheValue {

        public HierarchyIndicatorColumnAccessCacheValue() {
        }


        public HierarchyIndicatorColumnAccessCacheValue(UUID userId, List<UUID> indicatorIds, List<HierarchyIndicatorColumnAccess> hierarchyIndicatorColumnAccesses) {
            this.userId = userId;
            this.indicatorIds = indicatorIds;
            this.hierarchyIndicatorColumnAccesses = hierarchyIndicatorColumnAccesses;
        }

        private UUID userId;

        private List<UUID> indicatorIds;
        private List<HierarchyIndicatorColumnAccess> hierarchyIndicatorColumnAccesses;

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public List<UUID> getIndicatorIds() {
            return indicatorIds;
        }

        public void setIndicatorIds(List<UUID> indicatorIds) {
            this.indicatorIds = indicatorIds;
        }

        public List<HierarchyIndicatorColumnAccess> getHierarchyIndicatorColumnAccesses() {
            return hierarchyIndicatorColumnAccesses;
        }

        public void setHierarchyIndicatorColumnAccesses(List<HierarchyIndicatorColumnAccess> hierarchyIndicatorColumnAccesses) {
            this.hierarchyIndicatorColumnAccesses = hierarchyIndicatorColumnAccesses;
        }
    }

    private final CipherService cipherService;

    @Autowired
    public HierarchyIndicatorColumnAccessCacheService(HierarchyIndicatorColumnAccessCacheOptions options, CipherService cipherService) {
        super(options);
        this.cipherService = cipherService;
    }

    @Override
    protected Class<HierarchyIndicatorColumnAccessCacheValue> valueClass() {
        return HierarchyIndicatorColumnAccessCacheValue.class;
    }

    @Override
    public String keyOf(HierarchyIndicatorColumnAccessCacheValue value) {
        return this.buildKey(value.getUserId(), value.getIndicatorIds());
    }

    public String buildKey(UUID userId, List<UUID> indicatorIds) {
        String indicatorHash = "";
        if (indicatorIds != null && !indicatorIds.isEmpty()) {
            try {
                indicatorHash = this.cipherService.toSha1(
                        indicatorIds
                                .stream()
                                .map(x -> x.toString().toLowerCase(Locale.ROOT))
                                .sorted()
                                .collect(Collectors.joining("")));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        String indicatorFinalHash = indicatorHash;
        HashMap<String, String> keyParts = new HashMap<>();
        keyParts.put("$user_id$", userId.toString().toLowerCase(Locale.ROOT));
        keyParts.put("$indicators$", indicatorFinalHash);
        return this.generateKey(keyParts);
    }
}
