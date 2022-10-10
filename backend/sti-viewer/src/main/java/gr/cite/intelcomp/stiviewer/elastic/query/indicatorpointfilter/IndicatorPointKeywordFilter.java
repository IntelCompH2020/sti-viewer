package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter;

import java.util.List;
import java.util.Objects;

public class IndicatorPointKeywordFilter {
	private String field;
	private List<String> values;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IndicatorPointKeywordFilter that = (IndicatorPointKeywordFilter) o;

		if (!Objects.equals(field, that.field)) return false;
		if (values != null || that.values != null) {
			if (values == null || that.values == null) return false;
			if (values.size() != that.values.size()) return false;
			for (String value : values) {
				if (!that.values.contains(value)) return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = field != null ? field.hashCode() : 0;
		if (values != null) {
			for (String value : values) {
				result = 31 * result + (value != null ? value.hashCode() : 0);
			}
		}
		return result;
	}
}

