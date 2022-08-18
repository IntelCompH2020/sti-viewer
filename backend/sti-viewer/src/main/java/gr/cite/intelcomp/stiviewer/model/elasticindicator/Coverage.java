package gr.cite.intelcomp.stiviewer.model.elasticindicator;

public class Coverage {

	public final static String _label = "label";
	private String label;

	public final static String _min = "min";
	private Double min;

	public final static String _max = "max";
	private Double max;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}
}
