package gr.cite.intelcomp.stiviewer.service.portofolioconfig;

import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioConfig;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.List;

public interface PortofolioConfigService {

	List<PortofolioConfig> getMyConfigs(FieldSet fields) throws InvalidApplicationException;
	PortofolioConfig getMyConfigByKey(String key, FieldSet fields) throws InvalidApplicationException;
}
