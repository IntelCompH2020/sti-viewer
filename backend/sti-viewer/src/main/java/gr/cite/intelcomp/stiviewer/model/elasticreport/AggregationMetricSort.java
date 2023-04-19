package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.tools.data.query.Paging;

import java.util.List;

public class AggregationMetricSort {
	private List<AggregationMetricSortField> sortFields;
	private Paging paging;

	public List<AggregationMetricSortField> getSortFields() {
		return sortFields;
	}

	public void setSortFields(List<AggregationMetricSortField> sortFields) {
		this.sortFields = sortFields;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}
}
