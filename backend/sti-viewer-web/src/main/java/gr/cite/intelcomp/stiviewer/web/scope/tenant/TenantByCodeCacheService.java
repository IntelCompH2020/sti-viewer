package gr.cite.intelcomp.stiviewer.web.scope.tenant;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.event.TenantTouchedEvent;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class TenantByCodeCacheService extends CacheService<TenantByCodeCacheService.TenantByCodeCacheValue> {

	public static class TenantByCodeCacheValue {

		public TenantByCodeCacheValue() {
		}

		public TenantByCodeCacheValue(String tenantCode, UUID tenantId) {
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
	public TenantByCodeCacheService(TenantByCodeCacheOptions options, ConventionService conventionService) {
		super(options);
		this.conventionService = conventionService;
	}

	@EventListener
	public void handleTenantTouchedEvent(TenantTouchedEvent event) {
		if (!this.conventionService.isNullOrEmpty(event.getTenantCode())) this.evict(this.buildKey(event.getTenantCode()));
		if (!this.conventionService.isNullOrEmpty(event.getPreviousTenantCode())) this.evict(this.buildKey(event.getPreviousTenantCode()));
	}

	@Override
	protected Class<TenantByCodeCacheValue> valueClass() {
		return TenantByCodeCacheValue.class;
	}

	@Override
	public String keyOf(TenantByCodeCacheValue value) {
		return this.buildKey(value.getTenantCode());
	}

	public String buildKey(String code) {
		return this.generateKey(new HashMap<>() {{
			put("$code$", code);
		}});
	}
}
