package gr.cite.intelcomp.stiviewer.model.elasticindicator;

import java.util.List;

public class ValueRange {

	public final static String _min = "min";
	private Double min;

	public final static String _max = "max";
	private Double max;

	public final static String _values = "values";
	private List<ValueRangeValue> values;

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

	public List<ValueRangeValue> getValues() {
		return values;
	}

	public void setValues(List<ValueRangeValue> values) {
		this.values = values;
	}
}
