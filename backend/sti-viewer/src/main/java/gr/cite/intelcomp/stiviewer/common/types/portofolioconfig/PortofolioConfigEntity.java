package gr.cite.intelcomp.stiviewer.common.types.portofolioconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortofolioConfigEntity implements Serializable {
	private String indicatorGroupCode;
	private List<String> defaultDashboards;
	private String code;
	private String name;
	private List<PortofolioColumnConfigEntity> columns;

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

	public List<PortofolioColumnConfigEntity> getColumns() {
		return columns;
	}

	public void setColumns(List<PortofolioColumnConfigEntity> columns) {
		this.columns = columns;
	}

	public String getIndicatorGroupCode() {
		return indicatorGroupCode;
	}

	public void setIndicatorGroupCode(String indicatorGroupCode) {
		this.indicatorGroupCode = indicatorGroupCode;
	}

	public List<String> getDefaultDashboards() {
		return defaultDashboards;
	}

	public void setDefaultDashboards(List<String> defaultDashboards) {
		this.defaultDashboards = defaultDashboards;
	}
}