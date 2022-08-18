package gr.cite.intelcomp.stiviewer.model.elasticreport;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.Map;

@JsonTypeName("Composite")
public class Composite extends Bucket {

	private List<CompositeSource> sources;
	private DateHistogram dateHistogramSource;
	private Map<String, Object> afterKey;

	public List<CompositeSource> getSources() {
		return sources;
	}

	public void setSources(List<CompositeSource> sources) {
		this.sources = sources;
	}

	public DateHistogram getDateHistogramSource() {
		return dateHistogramSource;
	}

	public void setDateHistogramSource(DateHistogram dateHistogramSource) {
		this.dateHistogramSource = dateHistogramSource;
	}

	public Map<String, Object> getAfterKey() {
		return afterKey;
	}

	public void setAfterKey(Map<String, Object> afterKey) {
		this.afterKey = afterKey;
	}
}
