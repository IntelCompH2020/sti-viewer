package gr.cite.intelcomp.stiviewer.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticValuesResponse<M> {
	public ElasticValuesResponse() {
	}

	public ElasticValuesResponse(List<M> items, long count, Map<String, Object> afterKey) {
		this.items = items;
		this.count = count;
		this.afterKey = afterKey;
	}

	public List<M> items;
	public long count;
	private Map<String, Object> afterKey;

	public List<M> getItems() {
		return items;
	}

	public void setItems(List<M> items) {
		this.items = items;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public static QueryResult Empty() {
		return new QueryResult(new ArrayList<>(), 0L);
	}

	public Map<String, Object> getAfterKey() {
		return afterKey;
	}

	public void setAfterKey(Map<String, Object> afterKey) {
		this.afterKey = afterKey;
	}
}
