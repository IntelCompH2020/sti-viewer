package gr.cite.intelcomp.stiviewer.model.elasticreport;

import gr.cite.tools.data.query.Ordering;
import gr.cite.tools.data.query.Paging;

public class RawDataRequest {
	private String keyField;
	private String valueField;
	private Paging page;
	private Ordering order;

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public Paging getPage() {
		return page;
	}

	public void setPage(Paging page) {
		this.page = page;
	}

	public Ordering getOrder() {
		return order;
	}

	public void setOrder(Ordering order) {
		this.order = order;
	}
}
