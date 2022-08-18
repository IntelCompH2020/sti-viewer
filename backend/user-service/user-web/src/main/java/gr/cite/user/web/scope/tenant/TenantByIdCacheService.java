package gr.cite.user.web.scope.tenant;

import gr.cite.user.convention.ConventionService;
import gr.cite.user.event.TenantTouchedEvent;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

@Service
public class TenantByIdCacheService extends CacheService<TenantByIdCacheService.TenantByIdCacheValue> {

	public static class TenantByIdCacheValue {

		public TenantByIdCacheValue() {
		}

		public TenantByIdCacheValue(String tenantCode, UUID tenantId) {
			this.tenantCode = tenantCode;
			this.tenantId = tenantId;
		}

		private String tenantCode;

		public String getTenantCode() {
			return tenantCode;
		}

		public void setTenantCode(String tenantCode) {
			this.tenantCode = tenantCode;
		}

		private UUID tenantId;

		public UUID getTenantId() {
			return tenantId;
		}

		public void setTenantId(UUID tenantId) {
			this.tenantId = tenantId;
		}
	}

	private final ConventionService conventionService;

	@Autowired
	public TenantByIdCacheService(TenantByIdCacheOptions options, ConventionService conventionService) {
		super(options);
		this.conventionService = conventionService;
	}

	@EventListener
	public void handleTenantTouchedEvent(TenantTouchedEvent event) {
		if (!this.conventionService.isNullOrEmpty(event.getTenantCode())) this.evict(this.buildKey(event.getTenantId()));
	}

	@Override
	protected Class<TenantByIdCacheValue> valueClass() {
		return TenantByIdCacheValue.class;
	}

	@Override
	public String keyOf(TenantByIdCacheValue value) {
		return this.buildKey(value.getTenantId());
	}

	public String buildKey(UUID id) {
		return this.generateKey(new HashMap<>() {{
			put("$tenantId$", id.toString().toLowerCase(Locale.ROOT));
		}});
	}
}
