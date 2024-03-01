package gr.cite.intelcomp.stiviewer.model.deleter;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.DetailItemEntity;
import gr.cite.intelcomp.stiviewer.data.MasterItemEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.query.DetailItemQuery;
import gr.cite.intelcomp.stiviewer.query.MasterItemQuery;
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
public class MasterItemDeleter implements Deleter {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(MasterItemDeleter.class));
	private final TenantEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final DeleterFactory deleterFactory;

	@Autowired
	public MasterItemDeleter(
			TenantEntityManager entityManager, QueryFactory queryFactory,
			DeleterFactory deleterFactory
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.deleterFactory = deleterFactory;
	}

	public void deleteAndSaveByIds(List<UUID> ids) throws InvalidApplicationException {
		logger.debug(new MapLogEntry("collecting to delete").And("count", Optional.ofNullable(ids).map(e -> e.size()).orElse(0)).And("ids", ids));
		List<MasterItemEntity> datas = this.queryFactory.query(MasterItemQuery.class).ids(ids).collect();
		logger.trace("retrieved {} items", Optional.ofNullable(datas).map(e -> e.size()).orElse(0));
		this.deleteAndSave(datas);
	}

	public void deleteAndSave(List<MasterItemEntity> datas) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(datas).map(e -> e.size()).orElse(0));
		this.delete(datas);
		logger.trace("saving changes");
		this.entityManager.flush();
		logger.trace("changes saved");
	}

	public void delete(List<MasterItemEntity> datas) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(datas).map(x -> x.size()).orElse(0));
		if (datas == null || datas.isEmpty()) return;

		List<UUID> ids = datas.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
		{
			logger.debug("checking related - {}", DetailItemEntity.class.getSimpleName());
			List<DetailItemEntity> items = this.queryFactory.query(DetailItemQuery.class).masterItemIds(ids).collect();
			DetailItemDeleter deleter = this.deleterFactory.deleter(DetailItemDeleter.class);
			deleter.delete(items);
		}

		Instant now = Instant.now();

		for (MasterItemEntity item : datas) {
			logger.trace("deleting item {}", item.getId());
			item.setIsActive(IsActive.INACTIVE);
			item.setUpdatedAt(now);
			logger.trace("updating item");
			this.entityManager.merge(item);
			logger.trace("updated item");
		}
	}
}
