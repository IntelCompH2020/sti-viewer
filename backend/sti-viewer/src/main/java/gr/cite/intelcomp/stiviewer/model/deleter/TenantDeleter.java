package gr.cite.intelcomp.stiviewer.model.deleter;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.*;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
import gr.cite.intelcomp.stiviewer.query.TenantQuery;
import gr.cite.intelcomp.stiviewer.query.TenantRequestQuery;
import gr.cite.intelcomp.stiviewer.query.TenantUserQuery;
import gr.cite.tools.data.deleter.Deleter;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantDeleter implements Deleter {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantDeleter.class));

    private final TenantEntityManager entityManager;

    protected final QueryFactory queryFactory;

    private final DeleterFactory deleterFactory;

    @Autowired
    public TenantDeleter(
            TenantEntityManager entityManager,
            QueryFactory queryFactory,
            DeleterFactory deleterFactory
    ) {
        this.entityManager = entityManager;
        this.queryFactory = queryFactory;
        this.deleterFactory = deleterFactory;
    }

    public void deleteAndSaveByIds(List<UUID> ids) throws InvalidApplicationException {
        logger.debug(new MapLogEntry("collecting to delete").And("count", Optional.ofNullable(ids).map(List::size).orElse(0)).And("ids", ids));
        List<TenantEntity> data = this.queryFactory.query(TenantQuery.class).ids(ids).collect();
        logger.trace("received {} items", Optional.of(data).map(List::size).orElse(0));
        this.deleteAndSave(data);
    }

    public void deleteAndSave(List<TenantEntity> data) throws InvalidApplicationException {
        logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
        this.delete(data);
        logger.trace("saving changes");
        this.entityManager.flush();
        logger.trace("changes saved");
    }

    public void delete(List<TenantEntity> data) throws InvalidApplicationException {
        logger.debug("will delete {}  items", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty())
            return;

        List<UUID> ids = data.stream().map(TenantEntity::getId).distinct().collect(Collectors.toList());
        {
            logger.debug("checking related - {}", DetailItemEntity.class.getSimpleName());
            List<TenantUserEntity> items = this.queryFactory.query(TenantUserQuery.class).tenantIds(ids).collect();
            TenantUserDeleter deleter = this.deleterFactory.deleter(TenantUserDeleter.class);
            deleter.delete(items);
        }
        {
            logger.debug("checking related - {}", TenantRequestEntity.class.getSimpleName());
            List<TenantRequestEntity> items = this.queryFactory.query(TenantRequestQuery.class).assignedTenantIds(ids).collect();
            TenantRequestDeleter deleter = this.deleterFactory.deleter(TenantRequestDeleter.class);
            deleter.delete(items);
        }
        {
            logger.debug("checking related - {}", IndicatorAccessEntity.class.getSimpleName());
            List<IndicatorAccessEntity> items = this.queryFactory.query(IndicatorAccessQuery.class).tenantIds(ids).collect();
            IndicatorAccessDeleter deleter = this.deleterFactory.deleter(IndicatorAccessDeleter.class);
            deleter.delete(items);
        }

        Instant now = Instant.now();

        for (TenantEntity item : data) {
            logger.trace("deleting item {}", item.getId());
            item.setIsActive(IsActive.INACTIVE);
            item.setUpdatedAt(now);
            logger.trace("updating item");
            this.entityManager.merge(item);
            logger.trace("updated item");
        }

    }
}
