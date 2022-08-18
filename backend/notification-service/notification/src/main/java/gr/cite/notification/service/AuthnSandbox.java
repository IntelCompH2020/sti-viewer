package gr.cite.notification.service;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.MyPrincipal;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class AuthnSandbox {

    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ClaimExtractor claimExtractor;

    @Autowired
    public AuthnSandbox(
            CurrentPrincipalResolver currentPrincipalResolver,
            ClaimExtractor claimExtractor) {
        this.currentPrincipalResolver = currentPrincipalResolver;
        this.claimExtractor = claimExtractor;
    }

    public String sayHi() {
        MyPrincipal principal = this.currentPrincipalResolver.currentPrincipal();
        return "Hi " + this.claimExtractor.name(principal);
    }
}
