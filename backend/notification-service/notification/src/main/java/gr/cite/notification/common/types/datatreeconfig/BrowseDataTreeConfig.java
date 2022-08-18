package gr.cite.notification.common.types.datatreeconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrowseDataTreeConfig implements Serializable {
	private String id;

	private String name;

	private List<BrowseDataTreeLevelConfig> levelConfigs;

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

	public List<BrowseDataTreeLevelConfig> getLevelConfigs() {
		return levelConfigs;
	}

	public void setLevelConfigs(List<BrowseDataTreeLevelConfig> levelConfigs) {
		this.levelConfigs = levelConfigs;
	}
}