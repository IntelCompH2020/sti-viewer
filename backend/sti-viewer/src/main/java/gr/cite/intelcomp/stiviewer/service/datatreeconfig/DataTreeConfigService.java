package gr.cite.intelcomp.stiviewer.service.datatreeconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeConfig;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevel;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioConfig;
import gr.cite.intelcomp.stiviewer.query.lookup.IndicatorReportLevelLookup;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.List;

public interface DataTreeConfigService {
//	IndicatorReportConfigItem getIndicatorReportConfig(String configId) throws InvalidApplicationException;

	List<DataTreeConfig> getMyConfigs(FieldSet fields) throws InvalidApplicationException;

	DataTreeLevel getIndicatorReportLevel(IndicatorReportLevelLookup lookup, FieldSet fields) throws InvalidApplicationException;

	List<DataTreeConfig> getMyConfigByKey(String key, FieldSet fields) throws InvalidApplicationException;

	void updateLastAccess(String configId) throws InvalidApplicationException, JsonProcessingException;

}
