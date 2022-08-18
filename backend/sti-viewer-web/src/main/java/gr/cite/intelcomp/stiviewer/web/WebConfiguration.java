package gr.cite.intelcomp.stiviewer.web;

import gr.cite.intelcomp.stiviewer.web.scope.tenant.TenantInterceptor;
import gr.cite.intelcomp.stiviewer.web.scope.tenant.TenantScopeClaimInterceptor;
import gr.cite.intelcomp.stiviewer.web.scope.tenant.TenantScopeHeaderInterceptor;
import gr.cite.intelcomp.stiviewer.web.scope.user.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final TenantInterceptor tenantInterceptor;
    private final TenantScopeHeaderInterceptor scopeHeaderInterceptor;
    private final TenantScopeClaimInterceptor scopeClaimInterceptor;
    private final UserInterceptor userInterceptor;

    @Autowired
    public WebConfiguration(
            TenantInterceptor tenantInterceptor,
            TenantScopeHeaderInterceptor scopeHeaderInterceptor,
            TenantScopeClaimInterceptor scopeClaimInterceptor,
            UserInterceptor userInterceptor
    ) {
        this.tenantInterceptor = tenantInterceptor;
        this.scopeHeaderInterceptor = scopeHeaderInterceptor;
        this.scopeClaimInterceptor = scopeClaimInterceptor;
        this.userInterceptor = userInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        int order = 1;
        registry.addWebRequestInterceptor(scopeHeaderInterceptor).order(order++);
        registry.addWebRequestInterceptor(scopeClaimInterceptor).order(order++);
        registry.addWebRequestInterceptor(userInterceptor).order(order++);
        registry.addWebRequestInterceptor(tenantInterceptor).order(order++);
    }
}
