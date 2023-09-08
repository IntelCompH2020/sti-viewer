package gr.cite.intelcomp.stiviewer.model.deleter;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.*;
import gr.cite.intelcomp.stiviewer.query.*;
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
public class UserDeleter implements Deleter {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserDeleter.class));

    private final TenantEntityManager entityManager;

    private final QueryFactory queryFactory;

    private final DeleterFactory deleterFactory;

    @Autowired
    public UserDeleter(
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
        List<UserEntity> data = this.queryFactory.query(UserQuery.class).ids(ids).collect();
        logger.trace("retrieved {} items", Optional.ofNullable(data).map(List::size).orElse(0));
        this.deleteAndSave(data);
    }

    public void deleteAndSave(List<UserEntity> data) throws InvalidApplicationException {
        logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
        this.delete(data);
        logger.trace("saving changes");
        this.entityManager.flush();
        logger.trace("changes saved");
    }

    public void delete(List<UserEntity> data) throws InvalidApplicationException {
        logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty())
            return;

        List<UUID> ids = data.stream().map(UserEntity::getId).distinct().collect(Collectors.toList());
        {
            logger.debug("checking related - {}", TenantUserEntity.class.getSimpleName());
            List<TenantUserEntity> items = this.queryFactory.query(TenantUserQuery.class).userIds(ids).collect();
            TenantUserDeleter deleter = this.deleterFactory.deleter(TenantUserDeleter.class);
            deleter.delete(items);
        }
        {
            logger.debug("checking related - {}", BookmarkEntity.class.getSimpleName());
            List<BookmarkEntity> items = this.queryFactory.query(BookmarkQuery.class).userIds(ids).collect();
            BookmarkDeleter deleter = this.deleterFactory.deleter(BookmarkDeleter.class);
            deleter.delete(items);
        }
        {
            logger.debug("checking related - {}", TenantRequestEntity.class.getSimpleName());
            List<TenantRequestEntity> items = this.queryFactory.query(TenantRequestQuery.class).forUserIds(ids).collect();
            TenantRequestDeleter deleter = this.deleterFactory.deleter(TenantRequestDeleter.class);
            deleter.delete(items);
        }
        {
            logger.debug("checking related - {}", IndicatorAccessEntity.class.getSimpleName());
            List<IndicatorAccessEntity> items = this.queryFactory.query(IndicatorAccessQuery.class).userIds(ids).collect();
            IndicatorAccessDeleter deleter = this.deleterFactory.deleter(IndicatorAccessDeleter.class);
            deleter.delete(items);
        }

        Instant now = Instant.now();

        for (UserEntity item : data) {
            logger.trace("deleting item {}", item.getId());
            item.setIsActive(IsActive.INACTIVE);
            item.setUpdatedAt(now);
            logger.trace("updating item");
            this.entityManager.merge(item);
            logger.trace("updated item");
        }
    }
}
