package gr.cite.intelcomp.stiviewer.model.elasticindicator;

public class ValueRangeValue {

	public final static String _value = "value";
	private String value;

	public final static String _label = "label";
	private String label;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
