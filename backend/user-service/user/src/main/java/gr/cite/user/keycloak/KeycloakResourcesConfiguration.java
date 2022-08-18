package gr.cite.user.keycloak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(KeycloakResourcesProperties.class)
public class KeycloakResourcesConfiguration {

    private final KeycloakResourcesProperties properties;

    @Autowired
    public KeycloakResourcesConfiguration(KeycloakResourcesProperties properties) {
        this.properties = properties;
    }

    public KeycloakResourcesProperties getProperties() {
        return properties;
    }

    public String getGroupName(String tenantCode, String tenantId) {
        return properties.getTenantGroupsNamingStrategy()
                .replace("{tenantCode}", tenantCode)
                .replace("{tenantId}", tenantId);
    }

    public String getAuthorityName(String tenantCode, String key) {
        return properties.getAuthorities().get(key).getNamingStrategy()
                .replace("{tenantCode}", tenantCode);
    }

    public boolean hasAuthority(String authority, String tenantCode, String key) {
        return getAuthorityName(tenantCode, key).equals(authority);
    }

    public List<String> extractAuthoritiesForTenant(List<String> authorities, String tenantCode) {
        List<String> extractedAuthorities = new ArrayList<>();
        List<String> markedForRemoval = new ArrayList<>();
        authorities.forEach(auth -> {
            properties.getAuthorities().keySet().forEach(key -> {
                if (hasAuthority(auth, tenantCode, key)) {
                    extractedAuthorities.add(properties.getAuthorities().get(key).getTitle());
                    markedForRemoval.add(auth);
                }
            });
        });
        authorities.removeAll(markedForRemoval);
        authorities.addAll(extractedAuthorities);
        return extractedAuthorities;
    }

}
