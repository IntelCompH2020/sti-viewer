package gr.cite.notification.model.deleter;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.data.*;
import gr.cite.notification.query.*;
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

	private final TenantScopedEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final DeleterFactory deleterFactory;

	@Autowired
	public UserDeleter(
			TenantScopedEntityManager entityManager,
			QueryFactory queryFactory,
			DeleterFactory deleterFactory
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.deleterFactory = deleterFactory;
	}

	public void deleteAndSaveByIds(List<UUID> ids) throws InvalidApplicationException {
		logger.debug(new MapLogEntry("collecting to delete").And("count", Optional.ofNullable(ids).map(e -> e.size()).orElse(0)).And("ids", ids));
		List<UserEntity> datas = this.queryFactory.query(UserQuery.class).ids(ids).collect();
		logger.trace("retrieved {} items", Optional.ofNullable(datas).map(e -> e.size()).orElse(0));
		this.deleteAndSave(datas);
	}

	public void deleteAndSave(List<UserEntity> datas) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(datas).map(e -> e.size()).orElse(0));
		this.delete(datas);
		logger.trace("saving changes");
		this.entityManager.flush();
		logger.trace("changes saved");
	}

	public void delete(List<UserEntity> datas) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(datas).map(x -> x.size()).orElse(0));
		if (datas == null || datas.isEmpty()) return;

		List<UUID> ids = datas.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
		{
			logger.debug("checking related - {}", TenantUserEntity.class.getSimpleName());
			List<TenantUserEntity> items = this.queryFactory.query(TenantUserQuery.class).userIds(ids).collect();
			TenantUserDeleter deleter = this.deleterFactory.deleter(TenantUserDeleter.class);
			deleter.delete(items);
		}

		Instant now = Instant.now();

		for (UserEntity item : datas) {
			logger.trace("deleting item {}", item.getId());
			item.setIsActive(IsActive.INACTIVE);
			item.setUpdatedAt(now);
			logger.trace("updating item");
			this.entityManager.merge(item);
			logger.trace("updated item");
		}
	}
}
