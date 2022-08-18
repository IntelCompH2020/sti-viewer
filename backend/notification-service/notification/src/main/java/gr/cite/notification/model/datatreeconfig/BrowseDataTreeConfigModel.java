package gr.cite.notification.model.datatreeconfig;


import java.util.List;

public class BrowseDataTreeConfigModel {

	private String id;
	public static final String _id = "id";

	private String name;
	public static final String _name = "name";

	private List<BrowseDataTreeLevelConfigModel> levelConfigs;
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

	public List<BrowseDataTreeLevelConfigModel> getLevelConfigs() {
		return levelConfigs;
	}

	public void setLevelConfigs(List<BrowseDataTreeLevelConfigModel> levelConfigs) {
		this.levelConfigs = levelConfigs;
	}
}
