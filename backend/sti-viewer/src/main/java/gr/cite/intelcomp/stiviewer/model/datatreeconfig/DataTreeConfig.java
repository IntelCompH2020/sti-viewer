package gr.cite.intelcomp.stiviewer.model.datatreeconfig;


import java.util.List;

public class DataTreeConfig {

	private String id;
	public static final String _id = "id";

	private String name;
	public static final String _name = "name";

	private List<DataTreeLevelConfig> levelConfigs;
	public static final String _levelConfigs = "levelConfigs";


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

	public List<DataTreeLevelConfig> getLevelConfigs() {
		return levelConfigs;
	}

	public void setLevelConfigs(List<DataTreeLevelConfig> levelConfigs) {
		this.levelConfigs = levelConfigs;
	}
}
