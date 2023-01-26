package gr.cite.intelcomp.stiviewer.common.types.indicatorgroup;

import java.util.List;

public class IndicatorGroupAccessColumnConfigViewEntity {
	private String code;
	private List<IndicatorGroupAccessColumnConfigItemViewEntity> items;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<IndicatorGroupAccessColumnConfigItemViewEntity> getItems() {
		return items;
	}

	public void setItems(List<IndicatorGroupAccessColumnConfigItemViewEntity> items) {
		this.items = items;
	}
}
