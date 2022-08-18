package gr.cite.intelcomp.stiviewer.authorization.cache;

import gr.cite.intelcomp.stiviewer.authorization.indicatorpoint.IndicatorColumnAccess;
import gr.cite.tools.cache.CacheService;
import gr.cite.tools.cipher.CipherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IndicatorColumnAccessCacheService extends CacheService<IndicatorColumnAccessCacheService.IndicatorColumnAccessCacheValue> {

	public static class IndicatorColumnAccessCacheValue {

		public IndicatorColumnAccessCacheValue() {
		}


		public IndicatorColumnAccessCacheValue(UUID userId, List<UUID> indicatorIds, List<IndicatorColumnAccess> indicatorColumnAccesses) {
			this.userId = userId;
			this.indicatorIds = indicatorIds;
			this.indicatorColumnAccesses = indicatorColumnAccesses;
		}

		private UUID userId;

		private List<UUID> indicatorIds;
		private List<IndicatorColumnAccess> indicatorColumnAccesses;

		public UUID getUserId() {
			return userId;
		}

		public void setUserId(UUID userId) {
			this.userId = userId;
		}

		public List<UUID> getIndicatorIds() {
			return indicatorIds;
		}

		public void setIndicatorIds(List<UUID> indicatorIds) {
			this.indicatorIds = indicatorIds;
		}

		public List<IndicatorColumnAccess> getIndicatorColumnAccesses() {
			return indicatorColumnAccesses;
		}

		public void setIndicatorColumnAccesses(List<IndicatorColumnAccess> indicatorColumnAccesses) {
			this.indicatorColumnAccesses = indicatorColumnAccesses;
		}
	}

	private final CipherService cipherService;

	@Autowired
	public IndicatorColumnAccessCacheService(AffiliatedIndicatorsCacheOptions options, CipherService cipherService) {
		super(options);
		this.cipherService = cipherService;
	}

	@Override
	protected Class<IndicatorColumnAccessCacheValue> valueClass() {
		return IndicatorColumnAccessCacheValue.class;
	}

	@Override
	public String keyOf(IndicatorColumnAccessCacheValue value) {
		return this.buildKey(value.getUserId(), value.getIndicatorIds());
	}

	public String buildKey(UUID userId, List<UUID> indicatorIds) {
		String indicatorHash = "";
		if (indicatorIds != null && indicatorIds.size() > 0) {
			try {
				indicatorHash = this.cipherService.toSha1(String.join("", indicatorIds.stream().map(x -> x.toString().toLowerCase(Locale.ROOT)).sorted().collect(Collectors.toList())));
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
		String indicatorFinalHash = indicatorHash;
		return this.generateKey(new HashMap<>() {{
			put("$user_id$", userId.toString().toLowerCase(Locale.ROOT));
			put("$indicators$", indicatorFinalHash);
		}});
	}
}
