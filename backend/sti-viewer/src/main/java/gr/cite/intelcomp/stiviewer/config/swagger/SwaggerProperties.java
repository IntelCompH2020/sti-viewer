package gr.cite.intelcomp.stiviewer.config.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    private boolean enabled;
    private String title;
    private String version;
    private String license;
    private String licenseUrl;
    private String termsUrl;
    private Boolean enableBearerAuth;
    private List<ApiServer> apiServers;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getTermsUrl() {
        return termsUrl;
    }

    public void setTermsUrl(String termsUrl) {
        this.termsUrl = termsUrl;
    }

    public Boolean getEnableBearerAuth() {
        return enableBearerAuth;
    }

    public void setEnableBearerAuth(Boolean enableBearerAuth) {
        this.enableBearerAuth = enableBearerAuth;
    }

    public List<ApiServer> getApiServers() {
        return apiServers;
    }

    public void setApiServers(List<ApiServer> apiServers) {
        this.apiServers = apiServers;
    }

    public static class ApiServer {
        private String url;
        private String description;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
