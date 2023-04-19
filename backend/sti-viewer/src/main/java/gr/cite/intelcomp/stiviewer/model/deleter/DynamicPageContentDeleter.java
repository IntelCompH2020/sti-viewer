package gr.cite.intelcomp.stiviewer.model.deleter;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.DynamicPageContentEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.query.DynamicPageContentQuery;
import gr.cite.tools.data.deleter.Deleter;
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

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DynamicPageContentDeleter implements Deleter {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DynamicPageContentDeleter.class));

	private final TenantEntityManager entityManager;
	protected final QueryFactory queryFactory;

	@Autowired
	public DynamicPageContentDeleter(TenantEntityManager entityManager, QueryFactory queryFactory) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
	}

	public void deleteAndSaveByIds(List<UUID> ids) throws InvalidApplicationException {
		logger.debug(new MapLogEntry("collecting to delete").And("count", Optional.ofNullable(ids).map(List::size).orElse(0)).And("ids", ids));
		List<DynamicPageContentEntity> data = this.queryFactory.query(DynamicPageContentQuery.class).ids(ids).collect();
		logger.trace("received {} items", Optional.of(data).map(List::size).orElse(0));
		this.deleteAndSave(data);
	}

	public void deleteAndSave(List<DynamicPageContentEntity> data) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		this.delete(data);
		logger.trace("saving changes");
		this.entityManager.flush();
		logger.trace("changes saved");
	}

	public void delete(List<DynamicPageContentEntity> data) throws InvalidApplicationException {
		logger.debug("will delete {}  items", Optional.ofNullable(data).map(List::size).orElse(0));
		if (data == null || data.isEmpty()) return;
		Instant now = Instant.now();

		for (DynamicPageContentEntity item : data) {
			logger.trace("deleting item {}", item.getId());
			item.setIsActive(IsActive.INACTIVE);
			item.setUpdatedAt(now);
			logger.trace("updating item");
			this.entityManager.merge(item);
			logger.trace("updated item");
		}

	}
}
