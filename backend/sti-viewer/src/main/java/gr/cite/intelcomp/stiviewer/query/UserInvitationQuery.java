package gr.cite.intelcomp.stiviewer.query;

import gr.cite.intelcomp.stiviewer.data.UserInvitationEntity;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.UserInvitation;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserInvitationQuery extends QueryBase<UserInvitationEntity> {

    private String like;
    private Collection<UUID> ids;
    private Collection<UUID> tenantIds;

    public UserInvitationQuery like(String like) {
        this.like = like;
        return this;
    }

    public UserInvitationQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public UserInvitationQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public UserInvitationQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public UserInvitationQuery tenantIds(UUID value) {
        this.tenantIds = List.of(value);
        return this;
    }

    public UserInvitationQuery tenantIds(UUID... value) {
        this.tenantIds = Arrays.asList(value);
        return this;
    }

    public UserInvitationQuery tenantIds(Collection<UUID> values) {
        this.tenantIds = values;
        return this;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.tenantIds);
    }

    @Override
    protected Class<UserInvitationEntity> entityClass() {
        return UserInvitationEntity.class;
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.like != null && !this.like.isEmpty()) {
            predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(UserInvitationEntity._email), this.like));
        }

        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserInvitationEntity._id));
            for (UUID item : this.ids) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.tenantIds != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserInvitationEntity._tenantId));
            for (UUID item : this.tenantIds) inClause.value(item);
            predicates.add(inClause);
        }

        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(UserInvitation._id))
            return UserInvitationEntity._id;
        else if (item.match(UserInvitation._tenant, Tenant._id))
            return UserInvitationEntity._tenantId;
        else if (item.prefix(UserInvitation._tenant))
            return UserInvitationEntity._tenantId;
        else if (item.match(UserInvitation._token))
            return UserInvitationEntity._token;
        else if (item.match(UserInvitation._email))
            return UserInvitationEntity._email;
        else if (item.match(UserInvitation._isConsumed))
            return UserInvitationEntity._isConsumed;
        else if (item.match(UserInvitation._expiresAt))
            return UserInvitationEntity._expiresAt;
        else if (item.match(UserInvitation._createdAt))
            return UserInvitationEntity._createdAt;
        else if (item.match(UserInvitation._updatedAt))
            return UserInvitationEntity._updatedAt;
        else if (item.match(UserInvitation._hash))
            return UserInvitationEntity._updatedAt;
        else
            return null;
    }

    @Override
    protected UserInvitationEntity convert(Tuple tuple, Set<String> columns) {
        UserInvitationEntity item = new UserInvitationEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._id, UUID.class));
        item.setTenantId(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._tenantId, UUID.class));
        item.setEmail(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._email, String.class));
        item.setToken(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._token, String.class));
        item.setConsumed(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._isConsumed, Boolean.class));
        item.setExpiresAt(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._expiresAt, Instant.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, UserInvitationEntity._updatedAt, BigInteger.class));
        return item;
    }
}
