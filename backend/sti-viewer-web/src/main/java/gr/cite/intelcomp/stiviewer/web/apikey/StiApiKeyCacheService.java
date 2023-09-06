package gr.cite.intelcomp.stiviewer.web.apikey;

import gr.cite.commons.web.oidc.apikey.events.ApiKeyStaleEvent;
import gr.cite.commons.web.oidc.token.ApiKeyAccessToken;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;

@Service
public class StiApiKeyCacheService extends CacheService<StiApiKeyCacheService.AccessKey> {

    public static class AccessKey {

        public AccessKey() {
        }

        public AccessKey(ApiKeyAccessToken token, Instant expiration) {
            this.token = token;
            this.expiration = expiration;
        }

        private ApiKeyAccessToken token;

        public ApiKeyAccessToken getToken() {
            return token;
        }

        public void setToken(ApiKeyAccessToken token) {
            this.token = token;
        }

        private Instant expiration;

        public Instant getExpiration() {
            return expiration;
        }

        public void setExpiration(Instant expiration) {
            this.expiration = expiration;
        }
    }

    @Autowired
    public StiApiKeyCacheService(StiApiKeyCacheOptions options) {
        super(options);
    }

    @EventListener
    public void handleApiKeyStale(ApiKeyStaleEvent event) {
        this.evict(event.getApiKey());
    }

    @Override
    protected Class<AccessKey> valueClass() {
        return AccessKey.class;
    }

    @Override
    public AccessKey lookup(String key) {
        String generatedKey = this.buildKey(key);
        AccessKey token = super.lookup(generatedKey);
        if (token == null) return null;
        if (token.getExpiration().isBefore(Instant.now())) {
            this.evict(key);
            return null;
        }
        return token;
    }

    @Override
    public void put(String key, AccessKey value) {
        String generatedKey = this.buildKey(key);
        super.put(generatedKey, value);
    }

    @Override
    public void evict(String key) {
        String generatedKey = this.buildKey(key);
        super.evict(generatedKey);
    }

    @Override
    public String keyOf(AccessKey value) {
        throw new UnsupportedOperationException("key generation not supported");
    }

    @Override
    public void put(AccessKey value) {
        throw new UnsupportedOperationException("key generation not supported");
    }

    private String buildKey(String apiKey) {
        HashMap<String, String> keyParts = new HashMap<>();
        keyParts.put("$keyhash$", StiApiKeyCacheService.getHash(apiKey));
        return this.generateKey(keyParts);
    }

    private static String getHash(String apiKey) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA3-512");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        byte[] rawData = messageDigest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
        return String.format("%x", new BigInteger(1, rawData));
    }
}
