package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorPointLookup;

import java.util.List;

public class IndicatorPointReportLookup {
	private IndicatorPointLookup filters;
	private List<Metric> metrics;
	private Bucket bucket;

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}

	public IndicatorPointLookup getFilters() {
		return filters;
	}

	public void setFilters(IndicatorPointLookup filters) {
		this.filters = filters;
	}
}

