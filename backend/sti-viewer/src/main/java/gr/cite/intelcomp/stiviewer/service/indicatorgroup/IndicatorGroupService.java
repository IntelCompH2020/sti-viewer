package gr.cite.intelcomp.stiviewer.service.indicatorgroup;

import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;

import java.util.List;

public interface IndicatorGroupService {
	List<IndicatorGroupEntity> getIndicatorGroups();
	IndicatorGroupEntity getIndicatorGroupByCode(String code);
}
