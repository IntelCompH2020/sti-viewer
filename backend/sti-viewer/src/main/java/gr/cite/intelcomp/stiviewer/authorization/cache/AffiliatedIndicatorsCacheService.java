package gr.cite.intelcomp.stiviewer.authorization.cache;

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
public class AffiliatedIndicatorsCacheService extends CacheService<AffiliatedIndicatorsCacheService.AffiliatedIndicatorsCacheValue> {

    public static class AffiliatedIndicatorsCacheValue {

        public AffiliatedIndicatorsCacheValue() {
        }


        public AffiliatedIndicatorsCacheValue(UUID userId, List<UUID> indicatorIds, List<String> permissions) {
            this.userId = userId;
            this.indicatorIds = indicatorIds;
            this.permissions = permissions;
        }

        private UUID userId;

        private List<UUID> indicatorIds;
        private List<String> permissions;

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

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }

    private final CipherService cipherService;

    @Autowired
    public AffiliatedIndicatorsCacheService(AffiliatedIndicatorsCacheOptions options, CipherService cipherService) {
        super(options);
        this.cipherService = cipherService;
    }

    @Override
    protected Class<AffiliatedIndicatorsCacheValue> valueClass() {
        return AffiliatedIndicatorsCacheValue.class;
    }

    @Override
    public String keyOf(AffiliatedIndicatorsCacheValue value) {
        return this.buildKey(value.getUserId(), value.getPermissions());
    }

    public String buildKey(UUID userId, List<String> permissions) {
        String permissionsHash = "";
        if (permissions != null && !permissions.isEmpty()) {
            try {
                permissionsHash = this.cipherService.toSha1(
                        permissions
                                .stream()
                                .sorted()
                                .map(x -> x.toLowerCase(Locale.ROOT))
                                .collect(Collectors.joining("")))
                ;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        String finalPermissionsHash = permissionsHash;
        HashMap<String, String> keyParts = new HashMap<>();
        keyParts.put("$user_id$", userId.toString().toLowerCase(Locale.ROOT));
        keyParts.put("$permissions$", finalPermissionsHash);
        return this.generateKey(keyParts);
    }
}
