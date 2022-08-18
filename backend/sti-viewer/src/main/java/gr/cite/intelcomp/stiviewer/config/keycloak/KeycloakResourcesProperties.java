package gr.cite.intelcomp.stiviewer.config.keycloak;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.HashMap;

@ConstructorBinding
@ConfigurationProperties(prefix = "keycloak-resources")
@ConditionalOnProperty(prefix = "keycloak-resources", name = "enabled", havingValue = "true")
public class KeycloakResourcesProperties {

    private final String tenantGroupsNamingStrategy, guestsGroup, administratorsGroup;

    private final HashMap<String, TenantAuthorityGroupProperties> authorities;

    public KeycloakResourcesProperties(String tenantGroupsNamingStrategy, String guestsGroup, String administratorsGroup, HashMap<String, TenantAuthorityGroupProperties> authorities) {
        this.tenantGroupsNamingStrategy = tenantGroupsNamingStrategy;
        this.guestsGroup = guestsGroup;
        this.administratorsGroup = administratorsGroup;
        this.authorities = authorities;
    }

    public String getTenantGroupsNamingStrategy() {
        return tenantGroupsNamingStrategy;
    }

    public String getGuestsGroup() {
        return guestsGroup;
    }

    public String getAdministratorsGroup() {
        return administratorsGroup;
    }

    public HashMap<String, TenantAuthorityGroupProperties> getAuthorities() {
        return authorities;
    }

    @ConstructorBinding
    public static class TenantAuthorityGroupProperties {

        private final String parent, namingStrategy, title;

        public TenantAuthorityGroupProperties(String parent, String namingStrategy, String title) {
            this.parent = parent;
            this.namingStrategy = namingStrategy;
            this.title = title;
        }

        public String getParent() {
            return parent;
        }

        public String getNamingStrategy() {
            return namingStrategy;
        }

        public String getTitle() {
            return title;
        }
    }

}
