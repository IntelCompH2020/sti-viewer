package gr.cite.intelcomp.stiviewer.web.apikey;
import gr.cite.commons.web.oidc.configuration.filter.MutableHttpServletRequest;
import gr.cite.commons.web.oidc.token.ApiKeyAccessToken;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.tools.logging.LoggerService;
import org.apache.http.HttpHeaders;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Instant;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Component
public class StiApiKeyFilter implements Filter {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(StiApiKeyFilter.class));
    private final ConventionService conventionService;
    private final WebClient webClient;
    private final StiApiKeyCacheService apiKeyCacheService;
    private final StiApiKeyFilterOptions config;

    public StiApiKeyFilter(
            ConventionService conventionService,
            StiApiKeyCacheService apiKeyCacheService,
            StiApiKeyFilterOptions config) {
        this.conventionService = conventionService;
        this.apiKeyCacheService = apiKeyCacheService;
        this.config = config;
        webClient =
                WebClient
                        .builder()
                        .baseUrl(config.getIdpUri())
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .build();
    }

    public void init(FilterConfig filterConfig) {
    }

    @Override

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!config.isEnabled()) filterChain.doFilter(servletRequest, servletResponse);
        
        HttpServletRequest httpRequest = ((HttpServletRequest) servletRequest);
        String apiKey = httpRequest.getHeader(config.getApiKeyHeader());
        if (!this.conventionService.isNullOrEmpty(apiKey) && !this.conventionService.isListNullOrEmpty(this.config.getApiKeys())) {
            StiApiKeyFilterOptions.ApiKey key = this.config.getApiKeys().stream().filter(x-> apiKey.equals(x.getKey())).findFirst().orElse(null);
            if (key == null)  filterChain.doFilter(servletRequest, servletResponse);
            MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);
            ApiKeyAccessToken accessToken = this.getAccessTokenFor(key);
            mutableRequest.putHeader(config.getAuthorizationHeader(),  "Bearer " + accessToken.getAccessToken());
            filterChain.doFilter(mutableRequest, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private ApiKeyAccessToken getAccessTokenFor(StiApiKeyFilterOptions.ApiKey key) {
        ApiKeyAccessToken accessToken;
        StiApiKeyCacheService.AccessKey accessKey = this.apiKeyCacheService.lookup(key.getKey());
        if (accessKey == null) {
            accessToken = exchangeApiKeyForAccessToken(key);
            if (accessToken == null) {
                throw new IllegalArgumentException("API key response is empty");
            }
            accessKey = new StiApiKeyCacheService.AccessKey(accessToken, Instant.now().plusSeconds(accessToken.getExpiresIn()));
            this.apiKeyCacheService.put(key.getKey(), accessKey);
        } else {
            accessToken = accessKey.getToken();
        }
        return accessToken;
    }


    private ApiKeyAccessToken exchangeApiKeyForAccessToken(StiApiKeyFilterOptions.ApiKey key) {
        ApiKeyAccessToken accessToken;
        accessToken =
                webClient.post()
                        .body(BodyInserters
                                .fromFormData("grant_type", "password")
                                .with("client_id", this.config.getClientId())
                                .with("client_secret", this.config.getClientSecret())
                                .with("scope", this.config.getScope())
                                .with("username", key.getUserName())
                                .with("password", key.getPassword()))
                        .retrieve()
                        .bodyToMono(ApiKeyAccessToken.class)
                        .onErrorMap(IllegalArgumentException::new)
                        .block();
        return accessToken;
    }
}
