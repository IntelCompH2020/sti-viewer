package gr.cite.intelcomp.stiviewer.authorization;

import java.util.List;
import java.util.UUID;

public interface AuthorizationContentResolver {
	IndicatorRolesResource indicatorAffiliation(UUID indicatorId);

	List<UUID> affiliatedIndicators(String... permissions);

	List<HierarchyIndicatorColumnAccess> indicatorAllowedKeywords(UUID... indicatorIds);
}
