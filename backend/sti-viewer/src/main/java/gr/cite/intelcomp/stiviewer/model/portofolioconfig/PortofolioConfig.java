package gr.cite.intelcomp.stiviewer.model.portofolioconfig;


import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;

import java.util.List;

public class PortofolioConfig {

	private IndicatorGroup indicatorGroup;
	public static final String _indicatorGroup = "indicatorGroup";
	
	private String code;
	public static final String _code = "code";

	private String name;
	public static final String _name = "name";

	private List<PortofolioColumnConfig> columns;
	public static final String _columns = "columns";

	private List<String> defaultDashboards;
	public static final String _defaultDashboards = "defaultDashboards";


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PortofolioColumnConfig> getColumns() {
		return columns;
	}

	public void setColumns(List<PortofolioColumnConfig> columns) {
		this.columns = columns;
	}

	public IndicatorGroup getIndicatorGroup() {
		return indicatorGroup;
	}

	public void setIndicatorGroup(IndicatorGroup indicatorGroup) {
		this.indicatorGroup = indicatorGroup;
	}

	public List<String> getDefaultDashboards() {
		return defaultDashboards;
	}

	public void setDefaultDashboards(List<String> defaultDashboards) {
		this.defaultDashboards = defaultDashboards;
	}
}
