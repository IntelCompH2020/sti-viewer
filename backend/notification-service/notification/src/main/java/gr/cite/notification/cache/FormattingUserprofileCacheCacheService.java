package gr.cite.notification.cache;

import gr.cite.notification.event.UserTouchedEvent;
import gr.cite.notification.config.formatting.FormattingUserprofileCacheOptions;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

@Service
public class FormattingUserprofileCacheCacheService extends CacheService<FormattingUserprofileCacheCacheService.UserFormattingProfileCacheValue> {

	public static class UserFormattingProfileCacheValue {

		public UserFormattingProfileCacheValue() {
		}

		public UserFormattingProfileCacheValue(UUID userId, String zone, String culture, String language) {
			this.userId = userId;
			this.zone = zone;
			this.culture = culture;
			this.language = language;
		}

		private UUID userId;
		private String zone;
		private String culture;
		private String language;

		public String getZone() {
			return zone;
		}

		public void setZone(String zone) {
			this.zone = zone;
		}

		public String getCulture() {
			return culture;
		}

		public void setCulture(String culture) {
			this.culture = culture;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public UUID getUserId() {
			return userId;
		}

		public void setUserId(UUID userId) {
			this.userId = userId;
		}
	}

	@Autowired
	public FormattingUserprofileCacheCacheService(FormattingUserprofileCacheOptions options) {
		super(options);
	}

	@EventListener
	public void handleUserTouchedEvent(UserTouchedEvent event) {
		if (event.getUserId() != null) this.evict(this.buildKey(event.getUserId()));
	}

	@Override
	protected Class<UserFormattingProfileCacheValue> valueClass() {
		return UserFormattingProfileCacheValue.class;
	}

	@Override
	public String keyOf(UserFormattingProfileCacheValue value) {
		return this.buildKey(value.getUserId());
	}


	public String buildKey(UUID userId) {
		return this.generateKey(new HashMap<>() {{
			put("$user_id$", userId.toString().toLowerCase(Locale.ROOT));
		}});
	}
}
