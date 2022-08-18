package gr.cite.intelcomp.stiviewer.authorization;

import gr.cite.intelcomp.stiviewer.authorization.indicatorpoint.IndicatorColumnAccess;

import java.util.List;
import java.util.UUID;

public interface AuthorizationContentResolver {
	IndicatorRolesResource indicatorAffiliation(UUID indicatorId);

	List<UUID> affiliatedIndicators(String... permissions);

	List<IndicatorColumnAccess> indicatorAllowedKeywords(UUID... indicatorIds);
}
