package gr.cite.user.model.deleter;

import gr.cite.user.common.enums.ContactInfoType;
import gr.cite.user.common.enums.IsActive;
import gr.cite.user.common.enums.UserContactType;
import gr.cite.user.data.TenantEntityManager;
import gr.cite.user.data.UserContactInfoCompositeKey;
import gr.cite.user.data.UserContactInfoEntity;
import gr.cite.user.model.persist.UserContactInfoPersist;
import gr.cite.user.query.UserContactInfoQuery;
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
public class UserContactInfoDeleter implements Deleter {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserContactInfoDeleter.class));

	private final TenantEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final DeleterFactory deleterFactory;

	@Autowired
	public UserContactInfoDeleter(
			TenantEntityManager entityManager,
			QueryFactory queryFactory,
			DeleterFactory deleterFactory
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.deleterFactory = deleterFactory;
	}

	public void deleteAndSaveByIds(List<UserContactInfoCompositeKey> ids) throws InvalidApplicationException {
		List<UUID> userIds  = ids.stream().map(UserContactInfoCompositeKey::getUserId).distinct().collect(Collectors.toList());
		List<UserContactType> types = ids.stream().map(UserContactInfoCompositeKey::getType).distinct().collect(Collectors.toList());
		logger.debug(new MapLogEntry("collecting to delete").And("count", Optional.ofNullable(ids).map(List::size).orElse(0)).And("ids", ids));
		List<UserContactInfoEntity> data = this.queryFactory.query(UserContactInfoQuery.class).userIds(userIds).types(types).collect();
		logger.trace("retrieved {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		this.deleteAndSave(data);
	}

	public void deleteAndSave(List<UserContactInfoEntity> data) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		this.delete(data);
		logger.trace("saving changes");
		this.entityManager.flush();
		logger.trace("changes saved");
	}

	public void delete(List<UserContactInfoEntity> data) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		if (data == null || data.isEmpty()) return;
		for (UserContactInfoEntity item : data) {
			logger.trace("deleting item of type {} of user {}", item.getType(),item.getUserId());
			this.entityManager.remove(item);
			logger.trace("deleted item");
		}
	}

}
