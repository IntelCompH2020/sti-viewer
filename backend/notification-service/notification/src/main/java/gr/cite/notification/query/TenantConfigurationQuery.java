package gr.cite.notification.query;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.data.TenantConfigurationEntity;
import gr.cite.notification.data.TenantEntity;
import gr.cite.notification.model.TenantConfiguration;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.*;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TenantConfigurationQuery extends QueryBase<TenantConfigurationEntity> {
    private List<UUID> ids;
    private List<IsActive> isActives;
    private List<TenantConfigurationType> type;

    public TenantConfigurationQuery ids(UUID... ids) {
        this.ids = Arrays.asList(ids);
        return this;
    }

    public TenantConfigurationQuery ids(List<UUID> ids) {
        this.ids = ids;
        return this;
    }

    public TenantConfigurationQuery isActive(IsActive... isActives) {
        this.isActives = Arrays.asList(isActives);
        return this;
    }

    public TenantConfigurationQuery isActive(List<IsActive> isActive) {
        this.isActives = isActive;
        return this;
    }

    public TenantConfigurationQuery type(TenantConfigurationType... type) {
        this.type = Arrays.asList(type);
        return this;
    }

    public TenantConfigurationQuery type(List<TenantConfigurationType> type) {
        this.type = type;
        return this;
    }


    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.isActives);
    }

    @Override
    protected Class<TenantConfigurationEntity> entityClass() {
        return TenantConfigurationEntity.class;
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.ids != null) {
            predicates.add(queryContext.Root.get(TenantConfigurationEntity.Field.ID).in(ids));
        }

        if (this.isActives != null) {
            predicates.add(queryContext.Root.get(TenantConfigurationEntity.Field.IS_ACTIVE).in(isActives));
        }

        if (type != null) {
            predicates.add(queryContext.Root.get(TenantConfigurationEntity.Field.TYPE).in(type));
        }
        if (predicates.size() > 0) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }

    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(TenantConfiguration.Field.ID)) return TenantConfigurationEntity.Field.ID;
        else if (item.match(TenantConfiguration.Field.TENANT_ID)) return TenantConfigurationEntity.Field.TENANT_ID;
        else if (item.match(TenantConfiguration.Field.CREATED_AT)) return TenantConfigurationEntity.Field.CREATED_AT;
        else if (item.match(TenantConfiguration.Field.IS_ACTIVE)) return TenantConfigurationEntity.Field.IS_ACTIVE;
        else if (item.match(TenantConfiguration.Field.DEFAULT_USER_LOCALE_DATA)) return TenantConfiguration.Field.DEFAULT_USER_LOCALE_DATA;
        else if (item.match(TenantConfiguration.Field.EMAIL_CLIENT_DATA)) return TenantConfiguration.Field.EMAIL_CLIENT_DATA;
        else if (item.match(TenantConfiguration.Field.NOTIFIER_LIST_DATA)) return TenantConfiguration.Field.NOTIFIER_LIST_DATA;
        else if (item.match(TenantConfiguration.Field.TYPE)) return TenantConfigurationEntity.Field.TYPE;
        else if (item.match(TenantConfiguration.Field.UPDATED_AT)) return TenantConfigurationEntity.Field.UPDATED_AT;
        else if (item.match(TenantConfiguration.Field.VALUE)) return TenantConfigurationEntity.Field.VALUE;
        else return null;
    }

    @Override
    protected TenantConfigurationEntity convert(Tuple tuple, Set<String> columns) {
        TenantConfigurationEntity item = new TenantConfigurationEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, TenantConfigurationEntity.Field.ID, UUID.class));
        item.setValue(QueryBase.convertSafe(tuple, columns, TenantConfigurationEntity.Field.VALUE, String.class));
        item.setType(QueryBase.convertSafe(tuple, columns, TenantConfigurationEntity.Field.TYPE, TenantConfigurationType.class));
        item.setTenantId(QueryBase.convertSafe(tuple, columns, TenantConfigurationEntity.Field.TENANT_ID, UUID.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, TenantConfigurationEntity.Field.CREATED_AT, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, TenantConfigurationEntity.Field.UPDATED_AT, Instant.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, TenantConfigurationEntity.Field.IS_ACTIVE, IsActive.class));
        return item;
    }
}
