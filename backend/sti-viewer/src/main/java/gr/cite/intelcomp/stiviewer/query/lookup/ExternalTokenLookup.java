package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.ExternalTokenType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.ExternalTokenQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ExternalTokenLookup extends Lookup {
    private String like;
    private List<IsActive> isActive;
    private List<ExternalTokenType> types;
    private List<UUID> ids;
    private List<String> tokens;
    private Instant expiresAtFrom;
    private Instant expiresAtTo;

    public List<IsActive> getIsActive() {
        return isActive;
    }

    public void setIsActive(List<IsActive> isActive) {
        this.isActive = isActive;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public Instant getExpiresAtFrom() {
        return expiresAtFrom;
    }

    public void setExpiresAtFrom(Instant expiresAtFrom) {
        this.expiresAtFrom = expiresAtFrom;
    }

    public Instant getExpiresAtTo() {
        return expiresAtTo;
    }

    public void setExpiresAtTo(Instant expiresAtTo) {
        this.expiresAtTo = expiresAtTo;
    }

    public List<ExternalTokenType> getTypes() {
        return types;
    }

    public void setTypes(List<ExternalTokenType> types) {
        this.types = types;
    }

    public ExternalTokenQuery enrich(QueryFactory queryFactory) {
        ExternalTokenQuery query = queryFactory.query(ExternalTokenQuery.class);
        if (this.expiresAtFrom != null)
            query.expiresAtFrom(this.expiresAtFrom);
        if (this.expiresAtTo != null)
            query.expiresAtTo(this.expiresAtTo);
        if (this.like != null)
            query.like(this.like);
        if (this.isActive != null)
            query.isActive(this.isActive);
        if (this.types != null)
            query.types(this.types);
        if (this.ids != null)
            query.ids(this.ids);
        if (this.tokens != null)
            query.tokens(this.tokens);

        this.enrichCommon(query);

        return query;
    }
}
