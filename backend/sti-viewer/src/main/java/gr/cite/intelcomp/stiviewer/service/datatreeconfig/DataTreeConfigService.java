package gr.cite.intelcomp.stiviewer.service.datatreeconfig;

import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeConfig;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevel;
import gr.cite.intelcomp.stiviewer.query.lookup.IndicatorReportLevelLookup;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.List;

public interface DataTreeConfigService {
//	IndicatorReportConfigItem getIndicatorReportConfig(String configId) throws InvalidApplicationException;

	List<DataTreeConfig> getMyConfigs(FieldSet fields) throws InvalidApplicationException;

	DataTreeLevel getIndicatorReportLevel(IndicatorReportLevelLookup lookup, FieldSet fields);
}
