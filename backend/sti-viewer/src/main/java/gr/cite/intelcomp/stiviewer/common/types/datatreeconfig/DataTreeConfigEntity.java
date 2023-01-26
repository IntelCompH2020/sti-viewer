package gr.cite.intelcomp.stiviewer.common.types.datatreeconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataTreeConfigEntity implements Serializable {
	private String id;
	private String name;
	private int order;
	private String indicatorGroupCode;
	private String goTo;

	private List<DataTreeLevelConfigEntity> levelConfigs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataTreeLevelConfigEntity> getLevelConfigs() {
		return levelConfigs;
	}

	public void setLevelConfigs(List<DataTreeLevelConfigEntity> levelConfigs) {
		this.levelConfigs = levelConfigs;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getGoTo() {
		return goTo;
	}

	public void setGoTo(String goTo) {
		this.goTo = goTo;
	}

	public String getIndicatorGroupCode() {
		return indicatorGroupCode;
	}

	public void setIndicatorGroupCode(String indicatorGroupCode) {
		this.indicatorGroupCode = indicatorGroupCode;
	}
}