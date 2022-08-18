package gr.cite.intelcomp.stiviewer.model.elasticreport;

import com.fasterxml.jackson.annotation.JsonTypeName;
import gr.cite.tools.elastic.query.Aggregation.DateInterval;
import org.elasticsearch.search.sort.SortOrder;

import java.util.TimeZone;

@JsonTypeName("DateHistogram")
public class DateHistogram extends Bucket {
	private SortOrder order = SortOrder.ASC;
	private DateInterval interval;
	private TimeZone timezone;

	public SortOrder getOrder() {
		return order;
	}

	public void setOrder(SortOrder order) {
		this.order = order;
	}

	public DateInterval getInterval() {
		return interval;
	}

	public void setInterval(DateInterval interval) {
		this.interval = interval;
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}
}
