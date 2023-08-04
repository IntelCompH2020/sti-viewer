package gr.cite.intelcomp.stiviewer.model;

import java.time.Instant;

public class ExternalTokenCreateResponse {
    private Instant expiresAt;
    public final static String _expiresAt = "expiresAt";

    private String token;
    public final static String _token = "token";

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
