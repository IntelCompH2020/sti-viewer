package gr.cite.intelcomp.stiviewer.model.elasticreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gr.cite.tools.elastic.query.Aggregation.BucketAggregateType;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Terms.class, name = "Terms"),
		@JsonSubTypes.Type(value = DateHistogram.class, name = "DateHistogram"),
		@JsonSubTypes.Type(value = Nested.class, name = "Nested"),
		@JsonSubTypes.Type(value = Composite.class, name = "Composite")
})
public abstract class Bucket {
	private BucketAggregateType type;
	private String field;
	private List<Metric> metrics;
	private AggregationMetricHaving having;
	private AggregationMetricSort bucketSort;
	private Bucket bucket;

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}

	public BucketAggregateType getType() {
		return type;
	}

	public void setType(BucketAggregateType type) {
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public AggregationMetricHaving getHaving() {
		return having;
	}

	public void setHaving(AggregationMetricHaving having) {
		this.having = having;
	}

	public AggregationMetricSort getBucketSort() {
		return bucketSort;
	}

	public void setBucketSort(AggregationMetricSort bucketSort) {
		this.bucketSort = bucketSort;
	}
}


