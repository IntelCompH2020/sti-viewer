package gr.cite.intelcomp.stiviewer.model.datatreeconfig;


import java.util.List;

public class DataTreeConfig {

	private String id;
	public static final String _id = "id";

	private String name;
	public static final String _name = "name";

	private int order;
	public static final String _order = "order";

	private String goTo;
	public static final String _goTo = "goTo";

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
}
