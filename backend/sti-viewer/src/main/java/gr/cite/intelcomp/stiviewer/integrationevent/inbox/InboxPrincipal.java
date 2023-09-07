package gr.cite.intelcomp.stiviewer.integrationevent.inbox;

import gr.cite.commons.web.oidc.principal.MyPrincipal;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InboxPrincipal implements MyPrincipal, ClaimAccessor {
    private final Map<String, Object> claims;
    private final boolean isAuthenticated;

    public InboxPrincipal(Boolean isAuthenticated, String name) {
        this.claims = new HashMap<>();
        this.put(JwtClaimNames.SUB, name);
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public Boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.claims;
    }

    @Override
    public List<String> getClaimAsStringList(String claim) {
        if (claims == null)
            return null;
        return claims.values()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return this.getClaimAsString(JwtClaimNames.SUB);
    }

    public final void put(String key, Object value) {
        this.claims.put(key, value);
    }

    public static InboxPrincipal build(IntegrationEventProperties properties) {
        InboxPrincipal inboxPrincipal = new InboxPrincipal(Boolean.TRUE, "IntegrationEventQueueAppId");
        inboxPrincipal.put("client_id", properties.getAppId());
        inboxPrincipal.put("active", "true");
        inboxPrincipal.put("nbf", Instant.now().minus(30, ChronoUnit.SECONDS).toString());
        inboxPrincipal.put("exp", Instant.now().plus(10, ChronoUnit.MINUTES).toString());
        return inboxPrincipal;
    }
}
