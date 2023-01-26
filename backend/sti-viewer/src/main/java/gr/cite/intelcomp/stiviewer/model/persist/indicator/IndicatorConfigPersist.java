package gr.cite.intelcomp.stiviewer.model.persist.indicator;


import gr.cite.intelcomp.stiviewer.model.indicator.AccessRequestConfig;

import javax.validation.Valid;

public class IndicatorConfigPersist {
	@Valid
	private AccessRequestConfigPersist accessRequestConfig;

	public AccessRequestConfigPersist getAccessRequestConfig() {
		return accessRequestConfig;
	}

	public void setAccessRequestConfig(AccessRequestConfigPersist accessRequestConfig) {
		this.accessRequestConfig = accessRequestConfig;
	}
}
