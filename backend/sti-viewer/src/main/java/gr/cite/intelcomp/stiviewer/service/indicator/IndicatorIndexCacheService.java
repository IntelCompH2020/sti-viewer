package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.event.IndicatorTouchedEvent;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

@Service
public class IndicatorIndexCacheService extends CacheService<IndicatorIndexCacheService.IndicatorIndexCacheValue> {

	public static class IndicatorIndexCacheValue {

		public IndicatorIndexCacheValue() {
		}

		public IndicatorIndexCacheValue(UUID id, String indexName) {
			this.id = id;
			this.indexName = indexName;
		}

		private UUID id;

		private String indexName;

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public String getIndexName() {
			return indexName;
		}

		public void setIndexName(String indexName) {
			this.indexName = indexName;
		}
	}

	private final ConventionService conventionService;

	@Autowired
	public IndicatorIndexCacheService(IndicatorIndexCacheOptions options, ConventionService conventionService) {
		super(options);
		this.conventionService = conventionService;
	}

	@EventListener
	public void handleIndicatorTouchedEvent(IndicatorTouchedEvent event) {
		if (event.getId() != null) this.evict(this.buildKey(event.getId()));
	}

	@Override
	protected Class<IndicatorIndexCacheValue> valueClass() {
		return IndicatorIndexCacheValue.class;
	}

	@Override
	public String keyOf(IndicatorIndexCacheValue value) {
		return this.buildKey(value.getId());
	}

	public String buildKey(UUID indicatorId) {
		return this.generateKey(new HashMap<>() {{
			put("$indicator$", indicatorId.toString().toLowerCase(Locale.ROOT));
		}});
	}
}
