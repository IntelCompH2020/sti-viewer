package gr.cite.intelcomp.stiviewer.model.deleter;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.DynamicPageContentEntity;
import gr.cite.intelcomp.stiviewer.data.DynamicPageEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.query.DynamicPageContentQuery;
import gr.cite.intelcomp.stiviewer.query.DynamicPageQuery;
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
public class DynamicPageDeleter implements Deleter {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DynamicPageDeleter.class));

	private final TenantEntityManager entityManager;
	protected final QueryFactory queryFactory;
	protected final DeleterFactory deleterFactory;

	@Autowired
	public DynamicPageDeleter(
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
		List<DynamicPageEntity> data = this.queryFactory.query(DynamicPageQuery.class).ids(ids).collect();
		logger.trace("retrieved {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		this.deleteAndSave(data);
	}

	public void deleteAndSave(List<DynamicPageEntity> data) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		this.delete(data);
		logger.trace("saving changes");
		this.entityManager.flush();
		logger.trace("changes saved");
	}

	public void delete(List<DynamicPageEntity> data) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		if (data == null || data.isEmpty()) return;

		Instant now = Instant.now();

		List<UUID> ids = data.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
		{
			logger.debug("checking related - {}", DynamicPageContentEntity.class.getSimpleName());
			List<DynamicPageContentEntity> items = this.queryFactory.query(DynamicPageContentQuery.class).pageIds(ids).collect();
			DynamicPageContentDeleter deleter = this.deleterFactory.deleter(DynamicPageContentDeleter.class);
			deleter.delete(items);
		}

		for (DynamicPageEntity item : data) {
			logger.trace("deleting item {}", item.getId());
			item.setIsActive(IsActive.INACTIVE);
			item.setUpdatedAt(now);
			logger.trace("updating item");
			this.entityManager.merge(item);
			logger.trace("updated item");
		}
	}

}
