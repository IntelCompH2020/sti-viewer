package gr.cite.user.model.deleter;

import gr.cite.tools.data.deleter.Deleter;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.user.common.JsonHandlingService;
import gr.cite.user.common.enums.UserSettingsEntityType;
import gr.cite.user.common.enums.UserSettingsType;
import gr.cite.user.data.TenantEntityManager;
import gr.cite.user.data.UserSettingsEntity;
import gr.cite.user.model.UserSettings;
import gr.cite.user.query.UserSettingsQuery;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSettingsDeleter implements Deleter {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserSettingsDeleter.class));

	private final TenantEntityManager entityManager;
	protected final QueryFactory queryFactory;
	private final DeleterFactory deleterFactory;
	private final JsonHandlingService jsonHandlingService;

	@Autowired
	public UserSettingsDeleter(
			TenantEntityManager entityManager,
			QueryFactory queryFactory,
			DeleterFactory deleterFactory,
			JsonHandlingService jsonHandlingService
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.deleterFactory = deleterFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public List<UserSettingsEntity> deleteAndSaveById(UUID userId, UUID id) throws InvalidApplicationException {
		logger.debug(new MapLogEntry("collecting to delete").And("id", id));
		UserSettingsEntity data = this.queryFactory.query(UserSettingsQuery.class).entityIds(userId).entityTypes(UserSettingsEntityType.User).ids(id).first();
		if (data == null) return null;
		List<UserSettingsEntity> datas = new ArrayList<>();
		datas.add(data);

		UserSettingsEntity config = this.queryFactory.query(UserSettingsQuery.class).keys(data.getKey())
				.entityIds(userId).entityTypes(UserSettingsEntityType.User)
				.types(UserSettingsType.Config).first();
		if (config != null) datas.add(config);

		logger.trace("received {} items", Optional.of(datas).map(List::size).orElse(0));
		this.deleteAndSave(datas);
		return datas;
	}

	private void deleteAndSave(List<UserSettingsEntity> datas) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(datas).map(List::size).orElse(0));
		if (datas == null || datas.isEmpty()) return;
		for (UserSettingsEntity item : datas.stream().filter(x -> x != null && x.getType() == UserSettingsType.Settings).collect(Collectors.toList())) {
			logger.trace("deleting item {} - {} - {}", item.getEntityId(), item.getKey(), item.getId());
			logger.trace("removing item");

			UserSettingsEntity config = datas.stream().filter(x -> x != null && Objects.equals(x.getEntityId(), item.getEntityId()) &&
					Objects.equals(x.getEntityType(), item.getEntityType()) &&
					Objects.equals(x.getKey(), item.getKey()) &&
					x.getType() == UserSettingsType.Config).findFirst().orElse(null);
			UserSettings.UserSettingsConfig userSettingsConfig = null;
			if (config != null)
				userSettingsConfig = this.jsonHandlingService.fromJsonSafe(UserSettings.UserSettingsConfig.class, config.getValue());

			if (userSettingsConfig != null && userSettingsConfig.getDefaultSetting() != null && userSettingsConfig.getDefaultSetting().equals(item.getId())) {
				this.ResetConfig(config);
			}
			this.entityManager.remove(item);
			logger.trace("removed item");
		}

		logger.trace("saving changes");
		this.entityManager.flush();
		logger.trace("changes saved");
	}

	public void ResetConfig(UserSettingsEntity config) throws InvalidApplicationException {
		if (config == null) return;
		UserSettings.UserSettingsConfig userSettingsConfig = new UserSettings.UserSettingsConfig();
		userSettingsConfig.setDefaultSetting(null); //TODO empty uuid
		config.setValue(this.jsonHandlingService.toJsonSafe(userSettingsConfig));
		this.entityManager.merge(config);
	}
}
