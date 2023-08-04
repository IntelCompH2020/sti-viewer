package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorPointLookup;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class IndicatorPointReportLookup {
	@Valid
	@NotNull(message = "{validation.empty}")
	private IndicatorPointLookup filters;
	@Valid
	private List<Metric> metrics;
	@Valid
	private Bucket bucket;
	private Boolean isRawData;
	@Valid
	private RawDataRequest rawDataRequest;

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

	public Boolean getIsRawData() {
		return isRawData;
	}

	public void setIsRawData(Boolean rawData) {
		isRawData = rawData;
	}

	public RawDataRequest getRawDataRequest() {
		return rawDataRequest;
	}

	public void setRawDataRequest(RawDataRequest rawDataRequest) {
		this.rawDataRequest = rawDataRequest;
	}
}
